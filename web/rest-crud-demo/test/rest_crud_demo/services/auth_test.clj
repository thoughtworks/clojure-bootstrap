(ns rest-crud-demo.services.auth-test
  (:require [clojure.test :refer :all]
            [rest-crud-demo.services.auth :refer [encode
                                                  sign
                                                  require-roles]]))

(deftest auth-test
  (testing "should encode user"
    ;TODO: I hate the call to current time from within encode function. Maybe inject it?
    (let [token (+ (quot (System/currentTimeMillis) 1000) (* 60 60 24 7))]
      (is (= (str  "10:user:admin:1:" token)
             (encode {:id 10
                      :username "user"
                      :role "admin"})))))
  (testing "should sign message with password"
    (is (= "f109dbd1c8e124e9e0bbba2116bb167da7d607672d12a58cdd6ea15e637585ce"
           (sign "a message" "a password"))))
  (testing "should allow access to resource for admin user"
    (let [middleware (require-roles #(-> %) #{"admin"} #(get {3 {:password_hash "f9480456ebc3a3259592d1affa832643c3f7e29c3cf5f8afcfeaa62fb84f1d85"}} %))
          authorized-response (middleware {:headers 
                                           {"Authorization" "3:user:admin:1:1616907904.014fd2c1c2ab87a484a8d9da971825d1a91d3f0f9e355315619e7f6171591a98"}})]
      (is (= {:headers
              {"Authorization" "3:user:admin:1:1616907904.014fd2c1c2ab87a484a8d9da971825d1a91d3f0f9e355315619e7f6171591a98"}
              :identity
              {:id "3", :username "user", :role "admin", :auth "1", :exp "1616907904"}}
             authorized-response))))
  (testing "should not allow access to unauthorized user"
    (let [middleware (require-roles #(-> "do nothing handler") #{"admin"} #(get {3 {:password_hash "f9480456ebc3a3259592d1affa832643c3f7e29c3cf5f8afcfeaa62fb84f1d85"}} %))
          unauthorized-response (middleware {})]
      (is (= {:status 401 :headers {} :body "Unauthorized"} unauthorized-response))))
  (testing "should not allow access to forbidden resource"
    (let [middleware (require-roles #(-> "do nothing handler") #{"admin"} #(get {2 {:password_hash "bfebc2fee6cd4d3ecc0285e8b811c6e1c73cc9bdea8853b52256c674edf8eb16"}} %))
          forbidden-response (middleware {:headers
                                          {"Authorization" "2:jpsilva:user:1:1616906452.8c7b818360db4c5fb012a668ff23ae21107b2fdf1ebe712ae433ed1d3f51564d"}})]
      (is (= {:status 403 :headers {} :body "Permission denied"} forbidden-response))))
  )