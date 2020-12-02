(ns krispie-test
  (:require
   [clojure.string :as str]
   [clojure.test :refer [deftest is]]
   [krispie]))

(deftest message-test
  (let [path    "foo/bar/baz"
        message (krispie/message path)]
    (is (str/includes? message "did not match the snapshot"))
    (is (str/includes? message path))))

(deftest snapshot-path-test
  (is (= "foo/bar/baz/qux_1.edn" (krispie/snapshot-path 'baz/q+u*x "foo/bar" 1))))

(defn ^:private base-path
  []
  (System/getProperty "java.io.tmpdir"))

(deftest ensure-snapshot!-test
  (let [out              (java.io.StringWriter.)
        id               (gensym "ensure-snapshot!-test")
        value            {:ham "eggs"}
        ensure-snapshot! #(krispie/ensure-snapshot! id (base-path) 1 value)
        path             (binding [*out* out] (ensure-snapshot!))]
    (is (= value (krispie/read-edn! path)) "wrote the snapshot")
    (let [message (str out)]
      (is (str/includes? message "Wrote snapshot"))
      (is (str/includes? message path)))
    (let [message (with-out-str (ensure-snapshot!))]
      (is (empty? message)))))

(deftest with-context-test
  (let [id (gensym "with-context-test")]
    (krispie/with-context id (base-path)
      (is (= id krispie/*id*))
      (is (= (base-path) krispie/*base-path*))
      (is (= 0 @krispie/*counter_*)))))

(deftest match-snapshot?-test
  (let [id (gensym "match-snapshot?-test")]
    (krispie/with-context id (base-path)
      (is (match-snapshot? {:foo "bar"}))
      (is (match-snapshot? {:banana "split"}))
      (is (= 2 @krispie/*counter_*)))))
