(ns fugue-app.repl
  (:require [replumb.core :as replumb]
            [fugue-app.io :as io]
            [fugue-app.escher :as escher]
            [fugue-app.codemirror :as cm]))

(defonce repl-opts (merge (replumb/options :browser ["main.out"] io/fetch-file!)
                          {:context :statement}))

(defonce fugue-init-url "cljs/live.cljs")
(defonce fugue-prompt "♪  ")

(defn print-result!
  [cm result]
  (let [unwrapped (replumb/unwrap-result result)
        response (if (replumb/success? result)
                   unwrapped
                   (.-message unwrapped))]
    (js/console.log unwrapped)
    (if (not= "nil" response)
      (cm/writeln! cm response))))

(defn repc!
  "[r]eads s, [e]vals it, [p]rints the result to cm, [c]allback"
  ([cm s] (repc! cm s #(cm/prompt! cm fugue-prompt)))
  ([cm s cb]
   (replumb/read-eval-call repl-opts
                           (fn [result]
                             (print-result! cm result)
                             (cb))
                           s)))

(defn submit-handler
  "When ENTER is pressed, get the user input, rep it, and prompt."
  [cm]
  (let [user-input (cm/user-input cm)]
    (doto cm
      cm/set-cursor-to-end!
      cm/writeln!
      (repc! user-input))))

(defn load-fugue [cm]
  (cm/write! cm "Loading fugue...")
  (io/fetch-file! fugue-init-url #(repc! cm %)))

(defn start-repl!
  "Starts a fugue repl in the input CodeMirror"
  [cm]
  (escher/set-repl cm)
  (doto cm
    (cm/add-handler "Enter" #(submit-handler cm))
    (load-fugue)))
