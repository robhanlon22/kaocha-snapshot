(ns user
  (:require
   [clojure.tools.namespace.repl :as namespace.repl]
   [kaocha.repl]))

(defn r
  []
  (namespace.repl/refresh))

(defn ra
  []
  (namespace.repl/refresh-all))

(defn refresh-then
  [f]
  (let [result (r)]
    (if (= result :ok)
      (f)
      result)))

(defn t
  [& args]
  (refresh-then #(apply kaocha.repl/run args)))

(defn ta
  [& args]
  (refresh-then #(apply kaocha.repl/run-all args)))
