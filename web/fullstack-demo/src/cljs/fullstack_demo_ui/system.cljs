(ns fullstack-demo-ui.system
  (:require [re-frame.core :refer [reg-event-fx]]
            [taoensso.timbre :as log]))

(reg-event-fx
  :system/init!
  (fn [cofx event]
    (log/info "System initialized")))
