(ns rest-crud-demo.controllers.hello
  (:require [ring.util.http-response :refer [created ok not-found]]
            [compojure.api.sweet :refer [POST GET PUT DELETE context]]))

(def hello-routes
  (context "/api/v1/hello" []
    :tags ["hello"]

    [(GET "/hello-world" []
       (ok {:success true
            :welcome "hello world"}))
     
     (GET "/ping" []
       :query-params [message :- String]
       (ok {:success true
            :pong message}))]))