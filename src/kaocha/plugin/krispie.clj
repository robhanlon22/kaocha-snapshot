(ns kaocha.plugin.krispie
  (:require
   [clojure.spec.alpha :as s]
   [kaocha.plugin :refer [defplugin]]
   [kaocha.specs]
   [krispie]))

(def default-path
  "dev-resources/snapshots")

(defn ensure-base-path
  [config]
  (update config ::base-path #(or % default-path)))

(s/def ::base-path string?)

(s/def ::input-config
  (s/merge :kaocha/config
           (s/keys :opt [::base-path])))

(s/def ::config
  (s/merge :kaocha/config
           (s/keys :req [::base-path])))

(s/fdef ensure-base-path
  :args (s/cat :config ::input-config)
  :ret  ::config)

(defn with-context
  [run _test-plan]
  (fn [testable test-plan]
    (let [id        (:kaocha.testable/id testable)
          base-path (::base-path test-plan)]
      (krispie/with-context id base-path
        (run testable test-plan)))))

(s/def ::run
  (s/fspec :args (s/cat :testable  :kaocha/testable
                        :test-plan :kaocha/test-plan)
           :ret  any?))

;; Re-enable when https://github.com/lambdaisland/kaocha/pull/182 lands.
;; (s/fdef with-assert-context
;;   :args (s/cat :run       ::run
;;                :test-plan :kaocha/test-plan)
;;   :ret  ::run)

(defplugin kaocha.plugin/krispie
  (config [config]
    (ensure-base-path config))
  (wrap-run [run test-plan]
    (with-context run test-plan)))
