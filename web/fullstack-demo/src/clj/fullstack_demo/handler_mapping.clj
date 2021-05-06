(ns fullstack-demo.handler-mapping
  (:require [fullstack-demo.events :refer [reg-handler ->ClientEvent]]))

(reg-handler :client->server/hello
  [uid _]
  (->ClientEvent uid :server->client/respond-hello "Hello from backend!"))
