(ns fullstack-demo.comms
  (:require [taoensso.sente :as sente]
            [taoensso.sente.server-adapters.aleph :refer [get-sch-adapter]]
            [clojure.core.async :refer [go go-loop <! >! alts! timeout close! chan]]
            [mount.core :as m]
            [fullstack-demo.events :as events]
            [taoensso.timbre :as log])
  (:import (java.util UUID)
           (clojure.lang ExceptionInfo)))

(m/defstate ws-socket
  :start (sente/make-channel-socket!
           (get-sch-adapter)
           {:user-id-fn (fn [req] (str (UUID/randomUUID)))}))

(defn send-to-client! [client-event]
  (when (some? (:uid client-event))
    ((:send-fn ws-socket) (:uid client-event) [(:event-key client-event) (:event-data client-event)])))

(defn send-to-all-clients! [event-key event-data]
  (doseq [uid (:any @(:connected-uids ws-socket))]
    ((:send-fn ws-socket) uid [event-key event-data])))

(defn call-handler [handler uid event]
  (go
    (try
      (let [resp (handler uid (:data (second event)))]
        (when (events/client-event? resp)
          (send-to-client! resp)))
      (catch ExceptionInfo ei
        (let [{:keys [status body]} (ex-data ei)]
          (try
            (send-to-client! (events/->ClientEvent uid :server->client/error {:status status, :body body}))
            (catch Exception e
              (log/error e)))))
      (catch Exception e
        (log/error e)))))

(m/defstate event-listener
  :start (let [continue? (atom true)]
           (go-loop []
             (try
               (if-let [ch-recv (:ch-recv ws-socket)]
                 (when-let [[{:keys [event uid]}] (alts! [ch-recv (timeout 1000)])]
                   (when (and (some? event)
                              (not= "chsk" (namespace (first event))))
                     (call-handler (events/find-handler (first event)) uid event)))
                 (log/warn "event-listener :ch-recv was nil"))
               (catch Exception e
                 (log/error e)))
             (when @continue? (recur)))
           continue?)
  :stop (reset! event-listener false))

; ring handler functions
(defn chsk-get-handler []
  (or (:ajax-get-or-ws-handshake-fn ws-socket) identity))

(defn chsk-post-handler []
  (or (:ajax-post-fn ws-socket) identity))

