(ns rest-crud-demo.test-utils.utils
  (:require [cheshire.core :as cheshire]))

(defn parse-body [body]
  (cheshire/parse-string (slurp body) true))

(defn gen-string [n]
  (apply str (repeat n \x)))
