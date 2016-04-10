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

(defn writeln!
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

(defn handle-result!
  [c result]
  (if (replumb/success? result)
    (writeln! c (replumb/unwrap-result result))
    (writeln! c (.-message (replumb/unwrap-result result))))
  (prompt! c))

(defn read-eval-print-prompt!
  "TODO: Check if unmatched parens"
  [c]
  (let [pred #(when (= "repl-buffer" (.-className %)) (.find %))
        from (.-to (some pred (-> c .getDoc .getAllMarks)))
        to (end-pos c)
        user-input (.getRange (.getDoc c) from to)]
    (writeln! c)
    (replumb/read-eval-call (partial handle-result! c) user-input)))

(defn start-repl!
  "Starts a fugue repl in the input CodeMirror"
  [editor]
  (.setOption editor "extraKeys" (js-obj "Enter" read-eval-print-prompt!))
  (prompt! editor))
