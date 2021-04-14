(ns rest-crud-demo.services.auth
  (:require [buddy.core.mac :as mac]
            [buddy.core.codecs :as codecs]
            [clojure.set :as sets]
            [clojure.string :as cstr]
            [ring.util.http-response :as resp]
            [rest-crud-demo.utils.parse-utils :as parse]))

;; Increment auth-scheme to invalidate all tokens when authentication mechanism is changed
(def ^:private auth-scheme 1)

;; Just sample, use strong one
(def ^:private secret "somesecret")

;; ***** Auth implementation ****************************************************

(defn encode [user]
  (str (:id user) ":"
       (:username user) ":"
       (:role user) ":"
       auth-scheme ":"
       (+ (quot (System/currentTimeMillis) 1000) (* 60 60 24 7)))) ;; valid 7 days

;; Use password to create token to invalidate it when password changes
(defn sign [msg password]
  (-> (mac/hash msg {:key (str secret password) :alg :hmac+sha256})
      (codecs/bytes->hex)))

(defn- parse-token [token model]
  (let [[payload token-sign] (cstr/split token #"\.")
        user (zipmap [:id :username :role :auth :exp]
                     (cstr/split payload #":"))
        id (Integer/parseInt (:id user))
        passw (:password_hash (model id))]
    (if (= token-sign (sign payload passw))
      user
      nil)))

(defn- parse-header [request token-name]
  (some->> (some-> (resp/find-header request token-name)
                   (second))))

(defn- valid-auth-scheme? [user]
  (if (= (-> user :auth parse/parse-int) auth-scheme)
    user
    nil))

(defn- not-expire? [user]
  (let [now (quot (System/currentTimeMillis) 1000)]
    (if (< now (-> user :exp parse/parse-int))
      user
      nil)))

(defn- has-role? [role required-roles]
  (let [has-roles (case role
                    "admin"     #{"any" "user" "poweruser" "admin"}
                    "poweruser" #{"any" "user" "poweruser"}
                    "user"      #{"any" "user"}
                    #{})
        matched-roles (sets/intersection has-roles required-roles)]
    (not (empty? matched-roles))))

(defn require-roles [handler roles model]
  (fn [request]
    (let [user (some-> (parse-header request "authorization")
                       (parse-token model)
                       (valid-auth-scheme?)
                       (not-expire?))]
      (if-not user
        (resp/unauthorized "Unauthorized")
        (if-not (has-role? (:role user) roles)
          (resp/forbidden "Permission denied")
          (let [request (assoc request :identity user)]            
            (handler request)))))))