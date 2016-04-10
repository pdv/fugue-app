(ns fugue-app.repl
  (:require [replumb.core :as replumb]))


(defn mark-read-only [console from to]
  (.markText (.getDoc console) from to (js-obj "readOnly" true)))

(def start-pos (js-obj "line" 0 "ch" 0))

(defn end-pos [console]
  (js/CodeMirror.Pos (.lastLine console)))

(defn freeze! [console]
  (mark-read-only console start-pos (end-pos console)))

(defn write!
  [console str]
  (let [cursor-pos (js/CodeMirror.Pos (.lastLine console))]
    (.replaceRange console str cursor-pos)))

(defn write-ln!
  [console s]
  (write! console (str s "\n")))

(defn prompt!
  "Sends a prompt to the console and calls cb on <ENTER>"
  [console cb]
  (write! console "♪  ")
  (freeze! console))

(defn read-eval-print-loop!
  [console user-input]
  (replumb/read-eval-call () ()))

(defn start-repl!
  "Starts a fugue repl in the input CodeMirror"
  [cm]
  (prompt! cm read-eval-print-loop!))
