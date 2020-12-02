(ns krispie
  (:require
   [clojure.edn :as edn]
   [clojure.java.io :as io]
   [clojure.spec.alpha :as s]
   [clojure.string :as str]
   [clojure.test :as t :refer [is]]
   [puget.printer :as puget]))

(def ^:dynamic *id*)
(def ^:dynamic *base-path*)
(def ^:dynamic *counter_*)

(s/def ::id any?)
(s/def ::path string?)
(s/def ::counter nat-int?)

(defn ^:no-gen read-edn!
  [readable]
  (with-open [reader (java.io.PushbackReader. (io/reader readable))]
    (edn/read {:readers *data-readers*} reader)))

(s/fdef read-edn!
  :args (s/cat :readable string?)
  :ret  any?)

(defn message
  [path]
  (format
   "The actual result did not match the snapshot. If you'd like to update the
snapshot, first delete the following file, then rerun the test:
%s"
   path))

(s/fdef message
  :args (s/cat :path string?)
  :ret  string?)

(defn snapshot-path
  [id base-path counter]
  (let [cleaned  (-> (str id)
                     (str/replace "-" "_")
                     (str/replace "." "/")
                     (str/replace #"[^\w/]" ""))
        filename (format "%s_%s.edn" cleaned counter)]
    (str base-path "/" filename)))

(s/fdef snapshot-path
  :args (s/cat :id        ::id
               :base-path ::path
               :counter   ::counter)
  :ret  ::path)

(defn ^:no-gen ensure-snapshot!
  [id base-path counter value]
  (let [path (snapshot-path id base-path counter)]
    (when-not (.exists (io/as-file path))
      (io/make-parents path)
      (spit path (puget/pprint-str value))
      (println "\n\n>>> Wrote snapshot to" path))
    path))

(s/fdef ensure-snapshot!
  :args (s/cat :id        ::id
               :base-path ::path
               :counter   ::counter
               :value     any?)
  :ret  ::path)

(defmethod t/assert-expr 'match-snapshot?
  [msg form]
  `(let [counter# (swap! *counter_* inc)
         value#   ~(second form)
         path#    (ensure-snapshot! *id* *base-path* counter# value#)]
     (let [snapshot# (read-edn! path#)]
       (is (~'= snapshot# value#) (or ~msg (message path#))))))

(defmacro with-context
  [id base-path & body]
  `(binding [*id*        ~id
             *base-path* ~base-path
             *counter_*  (atom 0)]
     ~@body))
