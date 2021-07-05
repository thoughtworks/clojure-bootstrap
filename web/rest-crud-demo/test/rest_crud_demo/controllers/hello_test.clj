(ns rest-crud-demo.controllers.hello-test
  (:require [clojure.test :refer :all]
            [ring.mock.request :as mock]
            [rest-crud-demo.core :refer :all]
            [rest-crud-demo.test-utils.utils :refer [parse-body]]

    ; to register handlers
            [rest-crud-demo.controllers.hello])
  (:gen-class))

(deftest hello-world-test
  (testing "Should return Hello World"
    (let [response (app (-> (mock/request :get "/api/v1/hello/hello-world")))
          body (parse-body (:body response))]
      (is (= (:status response) 200))
      (is (= (:message body) "hello world"))))
  (testing "Should ping pong"
    (let [response (app (-> (mock/request :get "/api/v1/hello/ping?message=hi")))
          body (parse-body (:body response))]
      (is (= (:status response) 200))
      (is (= (:pong body) "hi")))))

