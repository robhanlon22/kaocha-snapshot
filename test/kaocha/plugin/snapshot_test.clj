(ns kaocha.plugin.snapshot-test
  (:require
   [clojure.string :as str]
   [clojure.test :as t :refer [deftest is]]
   [kaocha.api :as api]
   [kaocha.plugin.snapshot]
   [kaocha.result :as result]
   [matcher-combinators.test]
   [spy.core :as spy]))

(def config
  {:kaocha/plugins                               [:kaocha.plugin/snapshot]
   :kaocha.plugin.randomize/randomize?           false
   :kaocha.plugin.capture-output/capture-output? false
   :kaocha.plugin.snapshot/base-path
   "dev-resources/fixtures/snapshots"
   :kaocha/tests
   [{:kaocha.testable/type                :kaocha.type/clojure.test
     :kaocha.testable/id                  :unit
     :kaocha/ns-patterns                  [".*-fixture$"]
     :kaocha/source-paths                 []
     :kaocha/test-paths                   ["test"]
     :kaocha.testable/skip-add-classpath? true}]})

(deftest match-snapshot-test
  (is (match-snapshot? {:foo "bar"})))

(defn correct-message?
  [{:keys [message]}]
  (and message
       (str/starts-with? message
                         "The actual result did not match the snapshot.")))

(deftest snapshot-test
  (with-redefs [t/do-report (spy/spy t/do-report)]
    (let [result (api/run config)]
      (is (match? #:kaocha.result{:count   2
                                  :error   0
                                  :fail    1
                                  :pass    1
                                  :pending 0}
                  (result/testable-totals result))))
    (is (spy/call-matching? t/do-report #(some correct-message? %)))))
