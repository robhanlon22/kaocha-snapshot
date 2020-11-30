(ns kaocha.plugin.snapshot
  (:require
   [clojure.edn :as edn]
   [clojure.java.io :as io]
   [clojure.string :as str]
   [clojure.test :as t :refer [is]]
   [kaocha.plugin :refer [defplugin]]
   [kaocha.testable :as testable]
   [puget.printer :as puget])
  (:import
   (java.io
    PushbackReader)))

(def default-path
  "dev-resources/snapshots")

(def ^:dynamic *counter_* nil)

(defn read-edn
  [readable]
  (with-open [reader (PushbackReader. (io/reader readable))]
    (edn/read {:readers *data-readers*} reader)))

(defn message
  [file-path]
  (format
   "The actual result did not match the snapshot. If you'd like to update the
snapshot, first delete the following file, then rerun the test:
%s"
   file-path))

(defn file-path
  []
  (let [id       (:kaocha.testable/id testable/*current-testable*)
        cleaned  (-> (str id)
                     (str/replace "-" "_")
                     (str/replace "." "/")
                     (str/replace #"[^\w/]" ""))
        path     (:kaocha.plugin.snapshot/path testable/*config*)
        filename (format "%s_%s.edn" cleaned @*counter_*)]
    (str path "/" filename)))

(defmethod t/assert-expr 'match-snapshot?
  [msg form]
  `(let [value#     ~(second form)
         file-path# (file-path)]
     (swap! *counter_* inc)
     (when-not (.exists (io/as-file file-path#))
       (io/make-parents file-path#)
       (spit file-path# (puget/pprint-str value#))
       (println "\n\n>>> Wrote snapshot to" file-path#))
     (let [snapshot# (read-edn file-path#)]
       (is (~'= snapshot# value#) (or ~msg (message file-path#))))))

(defplugin kaocha.plugin/snapshot
  (config [config]
    (update config :kaocha.plugin.snapshot/path #(or % default-path)))
  (wrap-run [run _test-plan]
    (fn [testable test-plan]
      (binding [*counter_* (atom 0)]
        (run testable test-plan)))))
