(ns fugue-app.repl
  (:require [replumb.core :as replumb]
            [fugue-app.io :as io]
            [fugue-app.codemirror :as cm]))

(defonce repl-opts (merge (replumb/options :browser ["main.out"] io/fetch-file!)
                          {:context :statement}))

(defonce fugue-prompt "♪  ")

(defn print-result!
  [cm result]
  (js/console.log (replumb/unwrap-result result))
  (if (replumb/success? result)
    (cm/writeln! cm (replumb/unwrap-result result))
    (cm/writeln! cm (.-cause (replumb/unwrap-result result)))))

(defn repp!
  "[r]eads s, [e]vals it, [p]rints the result to cm, [p]rompts"
  [cm s]
  (cm/writeln! cm)
  (replumb/read-eval-call repl-opts
                          (fn [result]
                            (print-result! cm result)
                            (cm/prompt! cm fugue-prompt))
                          s))

(defn submit-handler
  "When ENTER is pressed, get the user input, rep it, and prompt."
  [cm]
  (let [user-input (cm/user-input cm)]
    (doto cm
      cm/set-cursor-to-end!
      (repp! user-input))))

(defn start-repl!
  "Starts a fugue repl in the input CodeMirror"
  [cm]
  (set! *print-fn* (fn [& args] (apply (partial cm/writeln! cm) args)))
  (doto cm
    (cm/add-handler "Enter" #(submit-handler cm))
    (repp! "(println \";; Starting fugue...\")")))
