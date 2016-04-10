(ns fugue-app.core
  (:require [goog.dom :refer [getElement]]
            [fugue-app.console :as console]
            [fugue-app.plumbing :as plumbing]))

(defn ^:export main []
  (do
    (js/CodeMirror.
     (getElement "editor")
     (js-obj
      "theme" "base16-ocean"
      "mode" "clojure"
      "lineNumbers" false
      "autoCloseBrackets" true
      "matchBrackets" true
      "styleActiveLine" true))
    (plumbing/read-eval-print! (console/new-console (getElement "repl")) "(+ 1 2)")))

