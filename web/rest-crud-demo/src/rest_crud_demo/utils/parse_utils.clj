(ns rest-crud-demo.utils.parse-utils)

(defn parse-int [s]
  (if s
    (Integer/parseInt s)))