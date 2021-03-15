(ns rest-crud-demo.hello
  (:require [ring.util.http-response :refer [created ok not-found]]
            [compojure.api.sweet :refer [POST GET PUT DELETE]]))

(def hello-routes
  [(GET "/" []
    (ok {:success true
         :welcome "hello"}))])
