(ns fullstack-demo.logging
  (:require [taoensso.timbre :as timbre]))

(def log-config
  {:min-level      :debug #_[["taoensso.*" :error] ["*" :debug]]
   :ns-filter      #{"*"} #_{:deny #{"taoensso.*"} :allow #{"*"}}

   :middleware     [] ; (fns [appender-data]) -> ?data, applied left->right

   :timestamp-opts timbre/default-timestamp-opts ; {:pattern _ :locale _ :timezone _}
   :output-fn      timbre/default-output-fn ; (fn [appender-data]) -> string

   :appenders
   #?(:clj
      {:println (timbre/println-appender {:stream :auto})
       ;; :spit (spit-appender    {:fname "./timbre-spit.log"})
       }

      :cljs
      (if (exists? js/window)
        {:console (timbre/console-appender {})}
        {:println (timbre/println-appender {})}))})

(defn init-logging! [config]
  (timbre/set-config! (merge log-config config)))
