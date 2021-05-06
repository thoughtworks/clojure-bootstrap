(ns fullstack-demo-ui.system
  (:require [re-frame.core :refer [reg-event-fx]]
            [taoensso.timbre :as log]
            [mount.core :as m]))

(reg-event-fx
  :system/init!
  (fn [cofx event]
    (m/start)
    (log/info "System initialized")))
