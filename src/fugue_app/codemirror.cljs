(ns fugue-app.codemirror)

(defn append-codemirror!
  "Appends a new CodeMirror to elem and returns it."
  [elem options]
  (js/CodeMirror. elem (clj->js options)))

(def read-only (js-obj "className" "repl-buffer"
                       "readOnly" true))

(def start-pos (js-obj "line" 0 "ch" 0))

(defn end-pos [cm]
  (js/CodeMirror.Pos (.lastLine (.getDoc cm))))

(defn get-text [cm]
  (-> cm .getDoc .getValue))

(defn clear-marks [cm]
  (doseq [mark (.getAllMarks cm)]
    (.clear mark)))

(defn freeze!
  "Marks all text currently in the cm as read-only.
   Returns TextMarker of frozen area."
  [cm]
  (clear-marks cm)
  (.markText (.getDoc cm) start-pos (end-pos cm) read-only))

(defn write!
  "Appends string s to the body of cm.
   Purely side-effecting. Returns undefined."
  [cm s]
  (.replaceRange cm s (end-pos cm)))

(defn writeln!
  "Writes string s followed by a newline to cm.
   Purely side-effecting. Returns undefined."
  ([cm] (write! cm "\n"))
  ([cm s] (write! cm (str s "\n"))))

(defn set-cursor-to-end! [cm]
  (.setCursor cm (end-pos cm)))

(defn prompt!
  "Prompts the console and freezes it."
  [cm prompt-str]
  (doto cm
    (write! prompt-str)
    freeze!
    set-cursor-to-end!))

(defn user-input
  "Returns the user input (non-buffer) from cm"
  [cm]
  (let [pred #(when (= "repl-buffer" (.-className %)) (.find %))
        from (.-to (some pred (-> cm .getDoc .getAllMarks)))]
    (.getRange (.getDoc cm) from (end-pos cm))))

(defn set-option [cm option value]
  (.setOption cm option value))

(defn add-handler [cm key f]
  (set-option cm "extraKeys" (js-obj key f)))

