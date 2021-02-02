(ns rest-crud-demo.db.pool
  (:import com.mchange.v2.c3p0.ComboPooledDataSource)
  (:require [taoensso.timbre :as log]))

(def ^:private db-spec
  {:host     "127.0.0.1"
   :port     5432
   :user     "postgres"
   :password "abc123ABC"
   :database "postgres"})

(def ds (doto (ComboPooledDataSource.)
          (.setJdbcUrl (str "jdbc:postgresql://" (:host db-spec) ":" (:port db-spec) "/" (:database db-spec) "?ssl=false"))
          (.setUser (:user db-spec))
          (.setPassword (:password db-spec))

          ;; Pool Size Management
          (.setMinPoolSize 3)
          (.setMaxPoolSize 10)

          ;; Connection eviction
          (.setMaxConnectionAge  (* 6 60 60)) ;; 6 hour
          (.setMaxIdleTime (* 3 60 60))       ;; 3 hour
          (.setMaxIdleTimeExcessConnections (* 4 60)) ;; 4 minutes

          ;; Connection testing
          (.setTestConnectionOnCheckin true)
          (.setIdleConnectionTestPeriod (* 5 60)))) ;; 5 minutes

(defn pool []
  (log/info (str "Starting db pool at " (:host db-spec) ":" (:port db-spec)))
  {:datasource ds})