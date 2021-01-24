(ns rest-crud-demo.core
  (:require
    [rest-crud-demo.user :refer [user-routes]]
    [ring.adapter.jetty :refer [run-jetty]]
    [toucan.db :as db]
    [toucan.models :as models]
    [compojure.api.sweet :refer [api routes]])
  (:gen-class))

(def db-spec
  {:dbtype   "postgres"
   :dbname   "postgres"
   :user     "postgres"
   :password "abc123ABC"})

(def swagger-config
  {:ui      "/swagger"
   :spec    "/swagger.json"
   :options {:ui   {:validatorUrl nil}
             :data {:info {:version "1.0.0", :title "Restful CRUD API"}}}})

(def app (api {:swagger swagger-config} (apply routes user-routes)))

(defn -main
  [& args]
  (db/set-default-db-connection! db-spec)
  (models/set-root-namespace! 'rest-crud-demo.models)
  (run-jetty app {:port 3000}))