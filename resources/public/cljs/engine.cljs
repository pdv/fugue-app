(ns fugue.engine)

(defonce ctx (atom (js/AudioContext.)))

(defn reload!
  "Closes the current context and replaces it with a new one"
  []
  (.close @ctx)
  (reset! ctx (js/AudioContext.)))

(defn now []
  (.-currentTime @ctx))

(defn out [in]
  (.connect in (.-destination @ctx))
  in)

(def audio? (partial instance? js/AudioNode))


;; Parameters and modulation
(comment
(defprotocol Modulator
  "A protocol for controlling AudioParams"
  (set-param! [modulator param]))

(extend-protocol Modulator
  number
  (set-param! [n param] (set! (.-value param) n))
  cljs.core.async.impl.channels.ManyToManyChannel
  (set-param! [c param]
    (go-loop []
      (let [x (<! c)]
        (attach param x)))))

(defn schedule-value!
  "Ramps the parameter to the value at the given time from now"
  [param value time]
  (if (= value 0)
    (.exponentialRampToValueAtTime param 0.00001 time)
    (.exponentialRampToValueAtTime param value time)))

(defn cancel-scheduled-values!
  "Cancels scheduled values but maintains the current value"
  [param]
  (let [current (.-value param)]
    (.cancelScheduledValues param (now))
    (.setValueAtTime param current (now))
    (schedule-value! param current (now))))


(defrecord EnvGen [env gate scale bias]
  Modulator
  (attach [this param]
    (go-loop []
      (let [g (<! gate)]
        ()))))


(defrecord CV [value node]
  Modulator
  (attach [this param]
    (attach value param)
    (.connect node param)))
)

;; Nodes

(defn oscillator
  "Creates and starts an OscillatorNode at the given freq"
  [type freq]
  (let [osc-node (.createOscillator @ctx)]
    (set! (.-type osc-node) (clj->js type))
    (attach freq (.-frequency osc-node))
    (.start osc-node)
    osc-node))

(defn biquad-filter
  "Apply a biquad filter to the input signal"
  [in type freq]
  (let [filter-node (.createBiquadFilter @ctx)]
    (set! (.-type filter-node) (clj->js type))
    (attach freq (.-frequency filter-node))
    (.connect in filter-node)
    filter-node))

(defn sig-delay
  "Delays in the input signal by the given amount in seconds"
  [in delay-t]
  (let [delay-node (.createDelay @ctx (* 2 delay-t))]
    (attach delay-t (.-delayTime delay-node))
    (.connect in delay-node)
    delay-node))

(defn gain
  "Creates a GainNode and attaches it to the input node"
  [in amount]
  (let [gain-node (.createGain @ctx)]
    (attach amount (.-gain gain-node))
    (.connect in gain-node)
    gain-node))
