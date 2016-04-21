(ns fugue-app.escher
  (:require [fugue-app.codemirror :as cm]))

(defonce repl (atom nil))

(defn set-repl [cm]
  (reset! repl cm))

(defn print-fn [& args]
  (apply (partial cm/write! @repl) args))

