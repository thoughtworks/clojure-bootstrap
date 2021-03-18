(ns rest-crud-demo.services.user
  (:require [rest-crud-demo.models.user :refer [User]]
            [rest-crud-demo.services.auth :refer :all]
            [rest-crud-demo.utils.string-util :as str]
            [schema.core :as s]
            [buddy.hashers :as hashers]
            [clojure.set :refer [rename-keys]]            
            [ring.util.http-response :refer [created ok not-found]]
            [compojure.api.meta :refer [restructure-param]]))

(defn valid-username? [name]
  (str/non-blank-with-max-length? 50 name))

(defn valid-password? [password]
  (str/length-in-range? 5 50 password))

(defn valid-role? [role]
  (contains? #{:user :admin :poweruser} (keyword role)))

(s/defschema UserRequestSchema
  {:username (s/constrained s/Str valid-username?)
   :password (s/constrained s/Str valid-password?)
   :email (s/constrained s/Str str/email?)
   :role (s/constrained s/Str valid-role?)})

(defn id->created [id]
  (created (str "/users/" id) {:id id}))

(defn- canonicalize-user-req [user-req]
  (-> (update user-req :password #(sign (str (:password user-req)) %))
      (rename-keys {:password :password_hash})))

(defn create-user-handler [create-user-req db-insert]
  (->> (canonicalize-user-req create-user-req)
       (db-insert User)
       :id
       id->created))

(defn user->response [user]
  (if user
    (ok user)
    (not-found)))

(defn get-user-handler [user-id]
  (-> (User user-id)
      (dissoc :password_hash)
      user->response))

(defn get-users-handler [db-select]
  (->> (db-select User)
       (map #(dissoc % :password_hash))
       ok))

(defn update-user-handler [id update-user-req db-update]
  (db-update User id (canonicalize-user-req update-user-req))
  (ok))

(defn delete-user-handler [user-id db-delete]
  (db-delete User :id user-id)
  (ok))
