(ns rest-crud-demo.services.user-test
  (:require [clojure.test :refer :all]
            [rest-crud-demo.test-utils.utils :refer [gen-string]]
            [rest-crud-demo.services.user :refer [valid-username?
                                                  valid-password?
                                                  valid-role?
                                                  create-user-handler
                                                  get-user-handler
                                                  get-users-handler]]))

(deftest user-test
  (testing "should validate username"
    (is (= true (valid-username? "a-valid-user-name")))
    (is (= false (valid-username? "")))
    (is (= true (valid-username? (gen-string 50))))
    (is (= false (valid-username? (gen-string 51)))))
  (testing "should validate password"
    (is (= true (valid-password? "valid-password")))
    (is (= true (valid-password? (gen-string 5))))
    (is (= true (valid-password? (gen-string 50))))
    (is (= false (valid-password? (gen-string 4))))
    (is (= false (valid-password? (gen-string 51)))))
  (testing "should validate role"
    (is (= true (valid-role? "admin")))
    (is (= true (valid-role? "user")))
    (is (= true (valid-role? "poweruser")))
    (is (= false (valid-role? "")))
    (is (= false (valid-role? "anything else"))))
  (testing "should handle user creation"
    (is (= {:status 201 :headers {"Location" "/users/10"} :body {:id 10}}
           (create-user-handler {:username "username" :password "password"}
                                #(-> (assoc %2 :id 10))
                                nil))))
  (testing "should handle user retrieval"
    (is (= {:status 200 :headers {} :body {:id 10}}
           (get-user-handler 10 (fn [id] (-> (first (filter #(= (:id %) id) [{:id 10 :password_hash "abc"}])))))))
    (is (= {:status 404 :headers {} :body nil}
           (get-user-handler 11 (fn [id] (-> (first (filter #(= (:id %) id) [{:id 10 :password_hash "abc"}])))))))
    )
  (testing "should handle all users retrieval"
    (is (= {:status 200 :headers {} :body [{:id 10} {:id 11}]}
           (get-users-handler #(-> %) [{:id 10 :password_hash "abc123"}
                                       {:id 11 :password_hash "123abc"}])))
    (is (= {:status 200 :headers {} :body []}
           (get-users-handler #(-> %) []))))
  )