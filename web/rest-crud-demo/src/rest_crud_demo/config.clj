(ns rest-crud-demo.config
  (:require [compojure.api.meta :as comp-meta]
            [rest-crud-demo.models.user :refer [User]]
            [rest-crud-demo.services.auth :as auth]))

(defmethod comp-meta/restructure-param :auth-roles
  [_ required-roles acc]
  (update-in acc [:middleware] conj [#(auth/require-roles %1 %2 User) required-roles]))

(defmethod comp-meta/restructure-param :current-user
  [_ binding acc]
  (update-in acc [:letks] into [binding `(:identity ~'+compojure-api-request+)]))