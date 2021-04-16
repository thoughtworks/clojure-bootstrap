(ns fullstack-demo.server
  (:require [mount.core :as m]
            [aleph.http.server :as http-server]
            [taoensso.timbre :as log]
            [fullstack-demo.config :as conf]
            [fullstack-demo.api :as api]))

(m/defstate server
  :start (if (conf/config-loaded?)
           (let [s (http-server/start-server #'api/handler {:port (conf/server-port)})]
             (log/info "HTTP server running, port:" (conf/server-port))
             s)
           (binding [*out* *err*]
             (println "Config file not loaded, you have the following options for providing configurations:")
             (println "  * Provide a file reference to the config.edn file which is in your file system")
             (println "  * Provide the config.edn content as a CLI argument (one long string)")
             (println "  * Use environment variables, please see https://github.com/yogthos/config for help")
             (System/exit 1)))
  :stop (some-> server .close))

