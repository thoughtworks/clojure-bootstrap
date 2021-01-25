(ns task)

(defn hello [to]
  (str "hello " to "!"))

(defn hello! [to] 
  (println (hello to)))
