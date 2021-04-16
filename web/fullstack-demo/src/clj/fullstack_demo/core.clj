(ns fullstack-demo.core
  (:require [mount.core :as m]
            [taoensso.timbre :as log]
            [fullstack-demo.config :as config]
            [fullstack-demo.logging :as logging]

            [fullstack-demo.server]))

(defn start [config-data]
  ; Increase core async threadpool size
  (System/setProperty "clojure.core.async.pool-size" "96")

  (logging/init-logging! (:logging config-data))

  (log/info "Starting up the system...")

  (let [now (System/nanoTime)]
    (-> (m/with-args config-data)
        m/start)

    (log/info (format "System started, took %s ms" (/ (- (System/nanoTime) now) 1e6)))))

(defn -main [& args]
  (.addShutdownHook
    (Runtime/getRuntime)
    (Thread.
      ^Runnable
      (fn []
        (log/info "Shutting down system...")
        (m/stop))))
  (start (config/parse args)))
