(ns user
  (:require
   [clojure.tools.namespace.repl :as namespace.repl]
   [kaocha.repl]
   [orchestra.spec.test :as orchestra]))

(defn refresh
  [f]
  (let [result (f)]
    (orchestra/instrument)
    result))

(defn r
  [& args]
  (refresh #(apply namespace.repl/refresh args)))

(defn ra
  [& args]
  (refresh #(apply namespace.repl/refresh-all args)))

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
