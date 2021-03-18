(ns rest-crud-demo.utils.string-util-test
  (:require [clojure.test :refer :all]
            [rest-crud-demo.utils.string-util :refer [non-blank?
                                                      max-length?
                                                      non-blank-with-max-length?
                                                      min-length?
                                                      length-in-range?]]))

(deftest string-util-test
  (testing "should identify non blank strings"
    (is (= true (non-blank? "something")))
    (is (= false (non-blank? " "))))
  (testing "should validate max length of a string"
    (is (= true (max-length? 5 "1234")))
    (is (= true (max-length? 5 "12345")))
    (is (= false (max-length? 5 "123456"))))
  (testing "should validate non blank and max length of a string"
    (is (= true (non-blank-with-max-length? 5 "1234")))
    (is (= false (non-blank-with-max-length? 5 " ")))
    (is (= false (non-blank-with-max-length? 5 "      ")))
    (is (= true (non-blank-with-max-length? 5 "12345")))
    (is (= false (non-blank-with-max-length? 5 "123456"))))
  (testing "should validate min length of a string"
    (is (= true (min-length? 5 "12345")))
    (is (= true (min-length? 5 "123456")))
    (is (= false (min-length? 5 "1234"))))
  (testing "should validate min length in range of a string"
    (is (= true (length-in-range? 2 5 "12")))
    (is (= true (length-in-range? 2 5 "123")))
    (is (= true (length-in-range? 2 5 "12345")))
    (is (= false (length-in-range? 2 5 "1")))
    (is (= false (length-in-range? 2 5 "123456"))))
)
  