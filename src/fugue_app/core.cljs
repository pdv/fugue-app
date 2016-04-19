(ns fugue-app.core
  (:require-macros [cljs.core.async.macros :as async-macros])
  (:require [cljs.core.async :as async]
            [fugue-app.ui :as ui]))

(enable-console-print!)

(ui/init)
