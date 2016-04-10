(ns fugue-app.repl
  (:require [replumb.core :as replumb]))

(defn clear-marks [c]
  (doseq [mark (.getAllMarks c)]
    (.clear mark))
  c)

(defn mark-range [c from to options]
  (.markText (.getDoc c) from to (clj->js options))
  c)

(def start-pos (js-obj "line" 0 "ch" 0))

(defn end-pos [c]
  (js/CodeMirror.Pos (.lastLine (.getDoc c))))

(defn freeze [c]
  (.markText (.getDoc c)
             start-pos
             (end-pos c)
             (js-obj "className" "repl-buffer"
                     "readOnly" true))
  c)

(defn write! [c s]
  (.replaceRange c s (end-pos c))
  c)

(defn write-ln!
  ([c] (write! c "\n"))
  ([c s] (write! c (str s "\n"))))

(defn set-cursor-to-end [c]
  (.setCursor c (end-pos c)))

(defn prompt!
  "Sends a prompt to the console and freezes it"
  [c]
  (-> c
      (write! "♪  ")
      clear-marks
      freeze
      set-cursor-to-end))

(defn read-eval-print!
  [console user-input]
  (replumb/read-eval-call () ()))

(defn read-eval-print-prompt!
  "TODO: Check if unmatched parens"
  [c]
  (let [pred #(when (= "repl-buffer" (.-className %)) (.find %))
        from (.-to (some pred (-> c .getDoc .getAllMarks)))
        to (end-pos c)]
    (write-ln! c)
    (write-ln! c (.getRange (.getDoc c) from to))
    (prompt! c)))

(defn start-repl!
  "Starts a fugue repl in the input CodeMirror"
  [editor]
  (.setOption editor "extraKeys" (js-obj "Enter" read-eval-print-prompt!))
  (prompt! editor))
