(ns fullstack-demo.events
  (:require [taoensso.timbre :as log]))

(defrecord ClientEvent [uid event-key event-data])

(def event-handlers (atom {}))

(defmacro reg-handler
  [event-key args & body]
  `(swap!
     event-handlers
     assoc
     ~event-key
     (fn [~@args]
       (do ~@body))))

(defn find-handler [event-key]
  (get @event-handlers event-key
       (fn [uid event-data]
         (log/warn "No event handler mapped for" event-key)
         [:error/unknown-event-type (str "No event handler mapped for " event-key)])))

(defn client-event? [resp]
  (isa? (type resp) ClientEvent))

(defn ->ClientEvent
  ([uid event-key]
   (->ClientEvent uid event-key []))
  ([uid event-key event-data]
   (ClientEvent. uid event-key event-data)))

