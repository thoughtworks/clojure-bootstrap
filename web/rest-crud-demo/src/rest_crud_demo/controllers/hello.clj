(ns rest-crud-demo.controllers.hello
  (:require [ring.util.http-response :refer [created ok not-found]]
            [compojure.api.sweet :refer [POST GET PUT DELETE]]))

(def hello-routes
  [(GET "/hello-world" []
    (ok {:success true
         :welcome "hello world"}))])
