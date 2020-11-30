(ns kaocha.plugin.snapshot
  (:require
   [clojure.edn :as edn]
   [clojure.java.io :as io]
   [clojure.pprint :as pp]
   [clojure.string :as str]
   [clojure.test :as t :refer [is]]
   [kaocha.plugin :as p])
  (:import
   (java.io
    PushbackReader)))

(def ^:dynamic *testable-id*)

(defn pathify
  [testable-id])

(defn read-edn
  [file]
  (edn/read {:readers *data-readers*}
            (PushbackReader. (io/reader file))))

(defn message
  [file-path]
  (format
   "The actual result did not match the snapshot. If you'd like to update the
snapshot, first delete the following file, then rerun the test:
%s"
   file-path))

(defmethod t/assert-expr 'match-snapshot?
  [msg form]
  `(let [value#         ~(second form)
         snapshot-path# (-> (str ~`*testable-id*)
                            (str/replace "-" "_")
                            (str/replace "." "/")
                            (str/replace #"[^\w/]" ""))
         resource-path# (str "snapshots/" snapshot-path# ".edn")
         file-path#     (str "dev-resources/" resource-path#)]
     (when-not (io/resource resource-path#)
       (io/make-parents file-path#)
       (spit file-path# (with-out-str (pp/pprint value#)))
       (println "\n\n>>> Wrote snapshot to" file-path#))
     (is (~'= (read-edn (io/resource resource-path#)) value#)
         (or ~msg (message file-path#)))))

(p/defplugin kaocha.plugin/snapshot
  (wrap-run [run _test-plan]
    (fn [testable test-plan]
      (binding [*testable-id* (:kaocha.testable/id testable)]
        (run testable test-plan)))))
