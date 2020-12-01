(ns kaocha.plugin.snapshot.assert-test
  (:require
   [clojure.java.io :as io]
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

(def value {:ham "eggs"})

(defn ensure-snapshot!
  [id]
  (let [base-path (System/getProperty "java.io.tmpdir")]
    (assert/ensure-snapshot! id base-path 1 value)))

(deftest ensure-snapshot!-test
  (let [out  (java.io.StringWriter.)
        id   (gensym "ensure-snapshot")
        path (binding [*out* out] (ensure-snapshot! id))]
    (try
      (is (= value (assert/read-edn! path)) "wrote the snapshot")
      (let [message (str out)]
        (is (str/includes? message "Wrote snapshot"))
        (is (str/includes? message path)))
      (let [message (with-out-str (ensure-snapshot! id))]
        (is (empty? message)))
      (finally
        (io/delete-file path)))))
