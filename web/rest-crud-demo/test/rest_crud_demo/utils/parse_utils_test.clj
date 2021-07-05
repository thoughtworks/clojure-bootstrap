(ns rest-crud-demo.utils.parse-utils-test
  (:require [clojure.test :refer :all]
            [rest-crud-demo.utils.parse-utils :refer [parse-int]]))

(deftest parse-utils-test
  (testing "should parse integers from a string"
    (is (= true (int? (parse-int "0"))))
    (is (= true (int? (parse-int "00"))))
    (is (= true (int? (parse-int "1"))))
    (is (= true (int? (parse-int "01"))))
    (is (= true (int? (parse-int "-1"))))
    (is (= true (int? (parse-int (str (Integer/MAX_VALUE))))))
    (is (= true (int? (parse-int (str (Integer/MIN_VALUE)))))))

  (testing "should not parse strings which are not integers"
    (is (= false (int? (parse-int nil))))
    (is (thrown? NumberFormatException (parse-int "a0")))
    (is (thrown? NumberFormatException (parse-int "")))
    (is (thrown? NumberFormatException (parse-int "  ")))
    (is (thrown? NumberFormatException (parse-int "abc")))
    (is (thrown? NumberFormatException (parse-int "--1")))
    (is (thrown? NumberFormatException (parse-int (str (inc (Integer/MAX_VALUE))))))
    (is (thrown? NumberFormatException (parse-int (str (dec (Integer/MIN_VALUE))))))))
