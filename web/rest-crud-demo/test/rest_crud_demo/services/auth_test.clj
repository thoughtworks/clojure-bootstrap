(ns rest-crud-demo.services.auth-test
  (:require [clojure.test :refer :all]
            [rest-crud-demo.services.auth :as auth]
            [clojure.string :as str]))

(defn ->auth-header [user pw-hash]
  (let [payload (str
                  (str/join ":" [(:id user)
                                 (:username user)
                                 (:role user)
                                 (:auth user)
                                 (:exp user)]))]
    (str payload "." (auth/sign payload pw-hash))))

(defn with-exp [partial-user]
  (assoc partial-user :exp (str (quot (+ (System/currentTimeMillis) 10000) 1000))))

(defn fake-handler [req]
  (assoc req :fake-called? true))

(deftest auth-test
  (testing "should encode user"
    ;TODO: I hate the call to current time from within encode function. Maybe inject it?
    (let [token (+ (quot (System/currentTimeMillis) 1000) (* 60 60 24 7))]
      (is (= (str "10:user:admin:1:" token)
             (auth/encode {:id       10
                           :username "user"
                           :role     "admin"})))))

  (testing "should sign message with password"
    (is (= "f109dbd1c8e124e9e0bbba2116bb167da7d607672d12a58cdd6ea15e637585ce"
           (auth/sign "a message" "a password"))))

  (testing "should allow access to resource for admin user"
    (let [pw-hash       "f9480456ebc3a3259592d1affa832643c3f7e29c3cf5f8afcfeaa62fb84f1d85"
          expected-user (with-exp {:id "3", :username "user", :role "admin", :auth "1"})
          middleware    (auth/require-roles fake-handler #{"admin"} {3 {:password_hash pw-hash}})
          response      (middleware {:headers {"Authorization" (->auth-header expected-user pw-hash)}})]

      (is (true? (:fake-called? response)))
      (is (= expected-user (:identity response)))))

  (testing "should allow access to resource for power user"
    (let [pw-hash       "f9480456ebc3a3259592d1affa832643c3f7e29c3cf5f8afcfeaa62fb84f1d85"
          expected-user (with-exp {:id "3", :username "user", :role "poweruser", :auth "1"})
          middleware    (auth/require-roles fake-handler #{"poweruser"} {3 {:password_hash pw-hash}})
          response      (middleware {:headers {"Authorization" (->auth-header expected-user pw-hash)}})]

      (is (true? (:fake-called? response)))
      (is (= expected-user (:identity response)))))

  (testing "should not allow access to resource for user with invalid role"
    (let [pw-hash               "f9480456ebc3a3259592d1affa832643c3f7e29c3cf5f8afcfeaa62fb84f1d85"
          middleware            (auth/require-roles fake-handler #{"nobody"} {3 {:password_hash pw-hash}})
          unauthorized-response (middleware {:headers {"Authorization" (->auth-header
                                                                         (with-exp {:id       3
                                                                                    :username "user"
                                                                                    :role     "nobody"
                                                                                    :auth     "1"})
                                                                         pw-hash)}})]

      (is (not (true? (:fake-called? unauthorized-response))))
      (is (= {:status 403 :headers {} :body "Permission denied"} unauthorized-response))))

  (testing "should not allow access to resource for admin user with invalid password in token"
    (let [pw-hash1              "f9480456ebc3a3259592d1affa832643c3f7e29c3cf5f8afcfeaa62fb84f1d85"
          pw-hash2              "014fd2c1c2ab87a484a8d9da971825d1a91d3f0f9e355315619e7f6171591000"
          middleware            (auth/require-roles fake-handler #{"admin"} {3 {:password_hash pw-hash1}})
          unauthorized-response (middleware {:headers {"Authorization" (->auth-header (with-exp {:id       3
                                                                                                 :username "user"
                                                                                                 :role     "admin"
                                                                                                 :auth     "1"})
                                                                                      pw-hash2)}})]
      (is (= {:status 401 :headers {} :body "Unauthorized"} unauthorized-response))))

  (testing "should not allow access to resource for admin user with invalid auth scheme in token"
    (let [pw-hash               "f9480456ebc3a3259592d1affa832643c3f7e29c3cf5f8afcfeaa62fb84f1d85"
          middleware            (auth/require-roles fake-handler #{"admin"} {3 {:password_hash pw-hash}})
          unauthorized-response (middleware {:headers {"Authorization" (->auth-header (with-exp {:id       3
                                                                                                 :username "user"
                                                                                                 :role     "admin"
                                                                                                 :auth     "2"})
                                                                                      pw-hash)}})]

      (is (not (true? (:fake-called? unauthorized-response))))
      (is (= {:status 401 :headers {} :body "Unauthorized"} unauthorized-response))))

  (testing "should not allow access to unauthorized user"
    (let [pw-hash               "f9480456ebc3a3259592d1affa832643c3f7e29c3cf5f8afcfeaa62fb84f1d85"
          middleware            (auth/require-roles fake-handler #{"admin"} {3 {:password_hash pw-hash}})
          unauthorized-response (middleware {})]

      (is (not (true? (:fake-called? unauthorized-response))))
      (is (= {:status 401 :headers {} :body "Unauthorized"} unauthorized-response))))

  (testing "should not allow access to forbidden resource"
    (let [pw-hash            "bfebc2fee6cd4d3ecc0285e8b811c6e1c73cc9bdea8853b52256c674edf8eb16"
          middleware         (auth/require-roles fake-handler #{"admin"} {2 {:password_hash pw-hash}})
          forbidden-response (middleware {:headers {"Authorization" (->auth-header (with-exp {:id       2
                                                                                              :username "jpsilva"
                                                                                              :role     "user"
                                                                                              :auth     "1"})
                                                                                   pw-hash)}})]

      (is (not (true? (:fake-called? forbidden-response))))
      (is (= {:status 403 :headers {} :body "Permission denied"} forbidden-response)))))
