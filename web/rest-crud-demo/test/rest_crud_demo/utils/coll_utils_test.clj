(ns rest-crud-demo.utils.coll-utils-test
  (:require [rest-crud-demo.utils.coll-utils :as u]
            [clojure.test :refer :all]
            [clojure.string :as str]))

(deftest map-by-test
  (is (= (u/map-by :foo [{:foo 1 :bar "doge"}
                         {:foo 2 :bar "wow"}])
         {1 {:foo 1 :bar "doge"}
          2 {:foo 2 :bar "wow"}}))

  (is (= (u/map-by (comp str/upper-case :bar) [{:foo 1 :bar "doge"}
                                               {:foo 2 :bar "wow"}])
         {"DOGE" {:foo 1 :bar "doge"}
          "WOW" {:foo 2 :bar "wow"}}))

  (is (= (u/map-by :foo :bar [{:foo 1 :bar "doge"}
                              {:foo 2 :bar "wow"}])
         {1 "doge"
          2 "wow"})))
