(ns fugue-app.core
  (:require [goog.dom :as dom]
            [goog.events :as events]
            [fugue-app.repl :as repl]))

(enable-console-print!)

(defn test-onclick []
  (events/listen (dom/getElement "play") events/EventType.CLICK, #(println "wow")))

(defn code-mirror [elem]
  (js/CodeMirror.
   elem
   (js-obj
    "theme" "base16-ocean"
    "mode" "clojure"
    "lineNumbers" false
    "autoCloseBrackets" true
    "matchBrackets" true
    "styleActiveLine" true)))

(defn ^:export main []
  (do
    (code-mirror (dom/getElement "editor"))
    (repl/start-repl! (code-mirror (dom/getElement "repl")))))

