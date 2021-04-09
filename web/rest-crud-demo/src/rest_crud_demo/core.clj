(ns rest-crud-demo.core
  (:require
   [rest-crud-demo.controllers.user :as controllers-user]
   [ring.middleware.reload :as mw-reload]
   [toucan.db :as db]
   [toucan.models :as models]
   [rest-crud-demo.controllers.auth :as controllers-auth]
   [rest-crud-demo.controllers.hello :as controllers-hello]
   [rest-crud-demo.controllers.default :as controllers-default]
   [compojure.api.sweet :as sweet])
  (:gen-class))

(def db-spec
  {:dbtype   "postgres"
   :dbname   "restful-crud"
   :user     "postgres"
   :password "abc123ABC"})

(def swagger-config
  {:ui      "/swagger"
   :spec    "/swagger.json"
   :options {:ui   {:validatorUrl nil}
             :data {:info {:version "1.0.0", :title "Restful CRUD API"}}}
   :data {:securityDefinitions {:api_key {:type "apiKey" :name "Authorization" :in "header"}}}})

(def app
  (sweet/api
   {:swagger swagger-config}
   (apply sweet/routes
          controllers-auth/auth-routes
          controllers-user/user-routes
          controllers-hello/hello-routes
          controllers-default/default-route)))

(def app-server
  (mw-reload/wrap-reload #'app))

(def init
  (do
    (db/set-default-db-connection! db-spec)
    (models/set-root-namespace! 'rest-crud-demo.models)
    app-server))
