(ns rest-crud-demo.auth
  (:require [compojure.api.sweet :refer :all]
            [ring.util.http-response :refer :all]
            [rest-crud-demo.models.user :refer [User]]
            [schema.core :as s]
            [buddy.core.mac :as mac]
            [buddy.core.codecs :as codecs]
            [clojure.string :as cstr]
            [taoensso.timbre :as log]
            [toucan.db :as db]
            [rest-crud-demo.utils :refer [parse-int]]))

;; Increment auth-scheme to invalidate all tokens when authentication mechanism is changed
(def ^:private auth-scheme 1)

;; Just sample, use strong one
(def ^:private secret "somesecret")

;; ***** Auth implementation ****************************************************

(defn- encode [user]
  (str (:id user) ":"
       (:username user) ":"
       (:role user) ":"
       auth-scheme ":"
       (+ (quot (System/currentTimeMillis) 1000) (* 60 60 24 7)))) ;; valid 7 days

;; Use password to create token to invalidate it when password changes
(defn sign [msg password]
  (-> (mac/hash msg {:key (str secret password) :alg :hmac+sha256})
      (codecs/bytes->hex)))

(defn- parse-token [token]
  (let [[payload token-sign] (cstr/split token #"\.")
        user (zipmap [:id :username :role :auth :exp]
                     (cstr/split payload #":"))
        id (Integer/parseInt (:id user))
        passw (:password_hash (User id))]
    (if (= token-sign (sign payload passw))
      user
      nil)))

(defn- parse-header [request token-name]
  (some->> (some-> (find-header request "authorization")
                   (second))))

(defn- valid-auth-scheme? [user]
  (if (= (-> user :auth parse-int) auth-scheme)
    user
    nil))

(defn- not-expire? [user]
  (let [now (quot (System/currentTimeMillis) 1000)]
    (if (< now (-> user :exp parse-int))
      user
      nil)))

(defn- has-role? [role required-roles]
  (let [has-roles (case role
                    "admin"     #{"any" "user" "poweruser" "admin"}
                    "poweruser" #{"any" "user" "poweruser"}
                    "user"      #{"any" "user"}
                    #{}
                    )
        matched-roles (clojure.set/intersection has-roles required-roles)]
    (not (empty? matched-roles))))

(defn require-roles [handler roles]
  (fn [request]
    (let [user (some-> (parse-header request "authorization")
                       (parse-token)
                       (valid-auth-scheme?)
                       (not-expire?))]
      (if-not user
        (unauthorized "Unauthorized")
        (if-not (has-role? (:role user) roles)
          (forbidden "Permission denied")
          (let [request (assoc request :identity user)]
            (handler request)))))))

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
            (ok {:ok true
                 :user (dissoc user :password_hash)
                 :token token})))))

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
      (ok user))

    ))