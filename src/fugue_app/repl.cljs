(ns fugue-app.repl
  (:require [replumb.core :as replumb]
            [fugue-app.io :as io]
            [fugue-app.codemirror :as cm]))

(defonce repl-opts (merge (replumb/options :browser ["main.out"] io/fetch-file!)
                          {:context :statement}))

(defonce fugue-prompt "♪  ")
(defonce fugue-init
  "(ns fugue.live
     (:require [fugue.audio :as audio :refer [out gain sin-osc saw]]
               [fugue.midi :as midi]))")
(defonce fugue-boot
  "(audio/init-audio!)")

(defn print-result!
  [cm result]
  (js/console.log (replumb/unwrap-result result))
  (if (replumb/success? result)
    (cm/writeln! cm (replumb/unwrap-result result))
    (cm/writeln! cm (.-cause (replumb/unwrap-result result)))))

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


(defn load-core-async [cm cb]
  (cm/writeln! cm "Loading core.async...")
  (repc! cm "(require 'cljs.core.async)" cb))

(defn load-fugue [cm]
  (cm/writeln! cm "Loading fugue...")
  (repc! cm fugue-init))

(defn boot-fugue [cm]
  (cm/writeln! cm "Booting fugue...")
  (repc! cm fugue-boot))

(defn start-repl!
  "Starts a fugue repl in the input CodeMirror"
  [cm]
  (js/console.log cm)
  (set! *print-fn* (fn [& args] (apply (partial cm/writeln! cm) args)))
  (doto cm
    (cm/add-handler "Enter" #(submit-handler cm))
    (load-core-async #(load-fugue cm))))
