(ns kaocha.plugin.snapshot-test
  (:require
   [clojure.test :refer [deftest is]]
   [kaocha.api :as api]
   [kaocha.plugin :as plugin]
   [kaocha.plugin.snapshot :as snapshot]))

(defn run-plugin-hook
  [hook init & extra-args]
  (let [chain (plugin/load-all [:kaocha.plugin/snapshot])]
    (apply plugin/run-hook* chain hook init extra-args)))

(def config
  {:kaocha/plugins [:kaocha.plugin/snapshot]
   :kaocha/tests
   [{:kaocha.testable/type :kaocha.type/clojure.test
     :kaocha.testable/id :unit
     :kaocha/ns-patterns ["-tests$"]
     :kaocha/source-paths []
     :kaocha/test-paths ["test"]
     :kaocha.testable/skip-add-classpath? true}]})

(deftest snapshot-test
  (api/run config))
