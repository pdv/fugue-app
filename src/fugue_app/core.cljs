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

(defn html-toggle [elem on-str on-f off-str off-f]
  (if (= off-str (.-innerHTML elem))
    (do
      (set! (.-innerHTML elem) on-str)
      (on-f))
    (do
      (set! (.-innerHTML elem) off-str)
      (off-f))))

(defn vim-toggle [cm elem]
  (let [set-keymap (partial cm/set-option cm "keyMap")]
    (html-toggle elem
                 "ON" #(set-keymap "vim")
                 "OFF" #(set-keymap "default"))))

(defn ln-toggle [cm elem]
  (let [set-ln (partial cm/set-option cm "lineNumbers")]
    (html-toggle elem
                 "ON" #(set-ln true)
                 "OFF"  #(set-ln false))))

(defn set-theme! [bg-color text-color]
  (set! (.-backgroundColor js/document.body.style) bg-color)
  (set! (.-color js/document.body.style) text-color))

(defn theme-toggle [elem]
  (html-toggle elem
               "DARK" #(set-theme! "#2b303b" "#c0c5ce")
               "LIGHT" #(set-theme! "#f8f8f8" "#4f5b66")))

(defn play-toggle [editor repl elem]
  (html-toggle elem
               "PLAY" #(repl/repp! repl "(stop!)")
               "STOP" #(repl/repp! repl (cm/get-text editor))))

(defn create-toggle [id f]
  (let [elem (dom/getElement id)]
    (bind-onclick elem #(f elem))))

(defn ^:export main []
  (let [editor (cm/append-codemirror! (dom/getElement "editor") cm-opts)
        repl-cm (cm/append-codemirror! (dom/getElement "repl") cm-opts)]
    (repl/start-repl! repl-cm)
    (create-toggle "vim" (partial vim-toggle editor))
    (create-toggle "line-numbers" (partial ln-toggle editor))
    (create-toggle "theme" theme-toggle)
    (bind-onclick (dom/getElement "play")
                  #(repl/repp! repl-cm (cm/get-text editor)))))

