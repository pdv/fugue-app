(ns fugue-app.core
  (:require [goog.dom :as dom]
            [goog.events :as events]
            [fugue-app.repl :as repl]
            [fugue-app.codemirror :as cm]))

(enable-console-print!)

(defonce cm-opts
  {:theme "base16-ocean"
   :mode "clojure"
   :lineNumbers false
   :autoCloseBrackets true
   :matchBrackets true
   :styleActiveLine true})

(defn bind-onclick
  "Attaches the onclick of dom elem with id to f"
  [elem f]
  (events/listen elem events/EventType.CLICK f))

(defn ^:export main []
  (let [editor (cm/append-codemirror! (dom/getElement "editor") cm-opts)
        repl-cm (cm/append-codemirror! (dom/getElement "repl") cm-opts)]
    (repl/start-repl! repl-cm)
    (bind-onclick (dom/getElement "play")
                  #(repl/repp! repl-cm (cm/get-text editor)))))

