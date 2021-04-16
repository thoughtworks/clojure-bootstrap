(ns fullstack-demo.config
  (:require [clojure.java.io :as io]
            [clojure.edn :as edn]
            [mount.core :as m])
  (:import (java.io PushbackReader)))

(defn read-as-file [s]
  (try
    (with-open [stream (PushbackReader. (io/reader (io/as-file s)))]
      (edn/read stream))
    (catch Exception e
      nil)))

(defn read-as-edn-str [s]
  (try
    (edn/read-string s)
    (catch Exception e
      nil)))

(defn lazy-juxt [& funs]
  (fn [& args]
    (map apply funs (repeat args))))

(defn parse
  "Parses configurations from a given string. It is evaluated in the following order:

  1. Try to read an edn file from the path defined by the given string s
  2. Try to evaluate given string as an edn

  If both fail a nil is returned"
  [s]
  (when (some? s)
    (first (filter some? ((lazy-juxt read-as-file read-as-edn-str) s)))))



(defn config-loaded? []
  (not= {} (m/args)))

(defn dev-mode? []
  (or (-> (m/args) :dev-mode) false))

(defn server-port []
  (or (-> (m/args) :server :port) 8080))
