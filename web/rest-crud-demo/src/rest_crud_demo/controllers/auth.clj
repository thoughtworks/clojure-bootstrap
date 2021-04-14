(ns rest-crud-demo.controllers.auth
  (:require [compojure.api.sweet :refer :all]
            [ring.util.http-response :refer :all]
            [rest-crud-demo.models.user :refer [User]]
            [rest-crud-demo.services.auth :as auth]
            [schema.core :as s]            
            [toucan.db :as db]))

(s/defschema Credentials
  {:login s/Str
   :password s/Str})

(def auth-routes
  (context "/api/v1/auth" []
    :tags ["auth"]

    (POST "/login" []
      :body [credentials Credentials]
      :summary "Authorization"
      (let [user (db/select-one User :username (:login credentials))]
        (if (not user)
          (ok {:ok false :msg "Invalid credentials"})
          (let [passw (:password_hash user)
                payload (auth/encode user)
                token (str payload "." (auth/sign payload passw))]
            (if (= passw (auth/sign (:password credentials) (:password credentials)))
              (ok {:ok true
                   :user (dissoc user :password_hash)
                   :token token})
              (ok {:ok false :msg "Invalid credentials"}))))))

    (GET "/test-auth-admin" []
      :auth-roles #{"admin"};; same as :middleware [[require-roles #{"admin"}]]
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