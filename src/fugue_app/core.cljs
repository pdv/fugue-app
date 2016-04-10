(ns fugue-app.core
  (:require [goog.dom :refer [getElement]]
            [fugue-app.console :as console]))

(defn ^:export main []
  (do
    (js/CodeMirror.
     (getElement "editor")
     (js-obj
      "theme" "base16-ocean"
      "mode" "clojure"
      "lineNumbers" false))
    (console/write! (console/new-console (getElement "repl")) "Hello world")))

