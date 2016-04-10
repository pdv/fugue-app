(ns fugue-app.plumbing
  (:require [replumb.core :as replumb]))

(comment

(defn handle-result!
  [console result]
  (let [write-fn (if (replumb/success? result)
                   console/write-return!
                   console/write-error!)]
    (write-fn console (replumb/unwrap-result result))))

(defn read-eval-print!
  [console user-input]
  (replumb/read-eval-call (partial handle-result! console) user-input))

(defn init-repl
  [elt]
  (let [console (console/new-console elt)]
    (read-eval-print! console)))

)
