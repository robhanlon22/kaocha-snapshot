(ns user
  (:require
   [clojure.tools.namespace.repl :as namespace.repl]
   [kaocha.repl]))

(defn refresh
  []
  (namespace.repl/refresh))

(defn refresh-all
  []
  (namespace.repl/refresh-all))

(defn run-tests
  [& args]
  (let [result (refresh)]
    (if (= result :ok)
      (apply kaocha.repl/run args)
      result)))

(defn run-all-tests
  [& args]
  (let [result (refresh)]
    (if (= result :ok)
      (apply kaocha.repl/run-all args)
      result)))
