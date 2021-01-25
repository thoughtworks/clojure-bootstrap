(ns task-test
  (:require [clojure.test :refer [deftest testing is]]
            [task]))

(deftest hello
  (testing "Should compose a welcome message"
    (is (= "hello world!"
           (task/hello "world")))))