(ns fullstack-demo-ui.events
  (:require [re-frame.core :refer [reg-event-db
                                   reg-sub]]))

(reg-event-db :server->client/respond-hello
  (fn [db [_ msg]]
    (update db :data/hello-responses (fn [c] (conj (or c []) msg)))))

(reg-sub :subscription/hello-responses
  (fn [db _]
    (-> db :data/hello-responses)))
