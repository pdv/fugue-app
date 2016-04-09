(ns fugue-app.core
  (:require [cljs.tools.reader :refer [read-string]]
            [cljs.js :refer [empty-state eval js-eval]]))

(defn eval-str [s]
  (eval (empty-state)
        (read-string s)
        {:eval js-eval
         :source-map true
         :context :expr}
        (fn [result] result)))

(defn test1 []
  (js/console.log "Test1"))

(defn ^:export main []
  (eval-str "(test1)"))
