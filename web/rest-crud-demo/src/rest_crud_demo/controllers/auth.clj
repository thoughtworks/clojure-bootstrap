(ns rest-crud-demo.controllers.auth
  (:require [compojure.api.sweet :refer :all]
            [ring.util.http-response :refer :all]
            [rest-crud-demo.models.user :refer [User]]
            [rest-crud-demo.services.auth :refer :all]
            [schema.core :as s]            
            [toucan.db :as db]))

;; ***** Route helpers ********************************************************

(defmethod compojure.api.meta/restructure-param :auth-roles
  [_ required-roles acc]
  (update-in acc [:middleware] conj [require-roles required-roles]))

(defmethod compojure.api.meta/restructure-param :current-user
  [_ binding acc]
  (update-in acc [:letks] into [binding `(:identity ~'+compojure-api-request+)]))

;; ***** API definition *******************************************************

(s/defschema Credentials
  {:login s/Str
   :password s/Str})

(def auth-routes
  (context "/api/v1" []
    :tags ["auth"]

    (POST "/login" []
      :body [credentials Credentials]
      :summary "Authorization"
      (let [user (db/select-one User :username (:login credentials))]
        (if (not user)
          (ok {:ok false :msg "Invalid credentials"})
          (let [passw (:password_hash user)
                payload (encode user)
                token (str payload "." (sign payload passw))]
            (if (= passw (sign (:password credentials) (:password credentials)))
              (ok {:ok true
                   :user (dissoc user :password_hash)
                   :token token})
              (ok {:ok false :msg "Invalid credentials"}))))))

    (GET "/test-auth-admin" []
      :auth-roles #{"admin"} ;; same as :middleware [[require-roles #{"admin"}]]
      :current-user user
      (ok user))

    (GET "/test-auth-poweruser" []
      :auth-roles #{"poweruser"}
      :current-user user
      (ok user))

    (GET "/test-auth-user" []
      :auth-roles #{"user"}
      :current-user user
      (ok user))

    (GET "/test-auth-any" []
      :auth-roles #{"any"}
      :current-user user
      (ok user))))