(ns rest-crud-demo.db.dbname
  (:require [yesql.core :refer [defqueries]]
            [taoensso.timbre :as log]
            [rest-crud-demo.db.pool :refer [pool]]
            [rest-crud-demo.utils :refer [parse-int]]))

(defqueries "sql/dbname.sql" {:connection (pool)})

(defn- get-value [dataset]
  (if (empty? dataset)
    nil
    (-> dataset first first val)))

(defn find-user [params]
  (-> params
      sql-auth-get-user
      first))

(defn get-password [user-id]
  (-> (sql-auth-get-user-password {:user user-id})
      (get-value)))