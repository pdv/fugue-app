(ns fugue-app.console
  (:require [clojure.string :as s]))

(defn new-console
  "Appends a CodeMirror console to the element"
  [elt]
  (let [cm-opts (js-obj
                 "theme" "base16-ocean"
                 "mode" "clojure"
                 "lineNumbers" false)]
    (js/CodeMirror. elt cm-opts)))

(defn write!
  "Writes a message to the input console."
  [console message]
  (let [out-str (str message "\n")
        cur-pos (js/CodeMirror.Pos (.lastLine console))]
    (.replaceRange console message cur-pos)))

(defn write-return!
  "Writes messages to the input console"
  [console & messages]
  (apply write! console messages))

(defn write-error!
  "Writes error messages to the input console"
  [console & messages]
  (apply write! console messages))

