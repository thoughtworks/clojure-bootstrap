(ns rest-crud-demo.controllers.default
  (:require [ring.util.http-response :refer [created ok not-found]]
            [compojure.api.sweet :refer [POST GET PUT DELETE context]]))


(def default-route
  [(GET "/" []
     (ok {:message "nothing to see here"}))])