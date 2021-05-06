(ns fullstack-demo-ui.comms
  (:require-macros [cljs.core.async.macros :as asyncm :refer [go go-loop]])
  (:require [cljs.core.async :as async :refer [<! >! put! chan alts! timeout close!]]
            [taoensso.sente :as sente :refer [cb-success?]]
            [re-frame.core :refer [dispatch reg-event-fx reg-fx]]
            [mount.core :as m]
            [taoensso.timbre :as log]))

(def ^:private write-chan (chan))

(defn chsk-state-handler [[old-state new-state]]
  (cond
    (and (false? (:ever-opened? old-state))
         (false? (:open? old-state))
         (true? (:open? new-state)))
    (log/info "Backend connection established")

    (and (true? (:ever-opened? new-state))
         (true? (:open? old-state))
         (false? (:open? new-state)))
    (do
      (log/warn "Backend connection lost")
      (dispatch [:event/server-connection-lost]))

    (and (true? (:ever-opened? new-state))
         (true? (:open? new-state))
         (false? (:open? old-state)))
    (do
      (log/info "Backend connection re-established")
      (dispatch [:event/server-connection-established])
      (dispatch [:event/show-notification "Backend server connection re-established"]))))

(defn chsk-handshake-handler [[_ client-id _ _]]
  (comment "Currently no-op"))

(def chsk-recv-handler dispatch)

(def csrf-token
  (when-let [el (.getElementById js/document "csrf-token")]
    (.getAttribute el "data-csrf-token")))

(m/defstate ws-connection
  :start (do
           (log/info "Opening backend connection")
           (sente/make-channel-socket!
             "/chsk"
             csrf-token
             {:type :auto})))

(m/defstate inbound-event-listener
  :start (go-loop []
                  (let [[{event :event} _] (alts! [(:ch-recv @ws-connection)
                                                   (timeout 1000)])]
                    (when-let [[ev args] event]
                      (condp = ev
                        :chsk/state (chsk-state-handler args)
                        :chsk/handshake (chsk-handshake-handler args)
                        :chsk/recv (chsk-recv-handler args)
                        (log/warn "Unknown sente event" ev)))
                    (recur)))
  :stop (close! @inbound-event-listener))

(m/defstate outbound-event-writer
  :start (go-loop []
                  (if (:open? @(:state @ws-connection))
                    (let [[[event] _] (alts! [write-chan
                                              (timeout 100)])]
                      (when (some? event)
                        ((:send-fn @ws-connection) (update (vec event) 1 (fn [v] {:data v})))))
                    (<! (timeout 50)))
                  (recur))
  :stop (close! @outbound-event-writer))

(reg-event-fx :chsk/ws-ping
  (fn [cofx]
    (log/trace "Backend connection ping")))

(reg-fx :ws/-send!
  (fn [[events]]
    (go
      ; when first element is a vector then expect multiple events
      (if (vector? (first events))
        (doseq [e events] (>! write-chan [e]))
        (>! write-chan [events])))))

(reg-event-fx :ws/send!
  (fn [cofx [_ events]]
    {:ws/-send! [events]}))
