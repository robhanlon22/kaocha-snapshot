(ns kaocha.plugin.snapshot.a-fixture
  (:require
   [clojure.test :as t :refer [deftest is]]))

(deftest success-test
  (is (match-snapshot? {:foo "bar"})))

(deftest failure-test
  (is (match-snapshot? {:ham "eggs"})))
