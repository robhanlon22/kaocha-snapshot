(ns kaocha.plugin.match-tests
  (:require
   [clojure.test :refer [deftest is]]))

(deftest match-test
  (is (match-snapshot? {:foo "bar"})))
