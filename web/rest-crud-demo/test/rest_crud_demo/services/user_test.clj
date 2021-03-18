(ns rest-crud-demo.services.user-test
  (:require [clojure.test :refer :all]
            [rest-crud-demo.test-utils.utils :refer [gen-string]]
            [rest-crud-demo.services.user :refer [valid-username?
                                                  valid-password?
                                                  valid-role?
                                                  id->created
                                                  create-user-handler]]))

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
  (testing "should return created id"
    (is (= {:status 201 :headers {"Location" "/users/10"} :body {:id 10}}
           (id->created 10)))
    (is (= {:status 201 :headers {"Location" "/users/"} :body {:id nil}}
           (id->created nil))))
  (testing "should handle user creation"
    (is (= {:status 201 :headers {"Location" "/users/10"} :body {:id 10}}
           (create-user-handler {:username "username" :password "password"}
                                #(-> (assoc %2 :id 10))))))
  )