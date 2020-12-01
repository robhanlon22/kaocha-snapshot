(ns kaocha.plugin.snapshot.assert-test
  (:require
   [clojure.string :as str]
   [clojure.test :refer [deftest is]]
   [kaocha.plugin.snapshot.assert :as assert]))

(deftest message-test
  (let [path    "foo/bar/baz"
        message (assert/message path)]
    (is (str/includes? message "did not match the snapshot"))
    (is (str/includes? message path))))

(deftest snapshot-path-test
  (is (= "foo/bar/baz/qux_1.edn" (assert/snapshot-path 'baz/q+u*x "foo/bar" 1))))

(defn ^:private base-path
  []
  (System/getProperty "java.io.tmpdir"))

(deftest ensure-snapshot!-test
  (let [out              (java.io.StringWriter.)
        id               (gensym "ensure-snapshot!-test")
        value            {:ham "eggs"}
        ensure-snapshot! #(assert/ensure-snapshot! id (base-path) 1 value)
        path             (binding [*out* out] (ensure-snapshot!))]
    (is (= value (assert/read-edn! path)) "wrote the snapshot")
    (let [message (str out)]
      (is (str/includes? message "Wrote snapshot"))
      (is (str/includes? message path)))
    (let [message (with-out-str (ensure-snapshot!))]
      (is (empty? message)))))

(deftest with-context-test
  (let [id (gensym "with-context-test")]
    (assert/with-context id (base-path)
      (is (= id assert/*id*))
      (is (= (base-path) assert/*base-path*))
      (is (= 0 @assert/*counter_*)))))

(deftest match-snapshot?-test
  (let [id (gensym "match-snapshot?-test")]
    (assert/with-context id (base-path)
      (is (match-snapshot? {:foo "bar"}))
      (is (match-snapshot? {:banana "split"}))
      (is (= 2 @assert/*counter_*)))))
