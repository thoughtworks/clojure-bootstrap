(ns main
  (:require [cli-matic.core :refer [run-cmd]]
            [task])
  (:gen-class))

(defn hello [{:keys [to]}] (task/hello! to))

(def CONFIGURATION
  {:app         {:command     "say"
                 :description "A command-line to to say something"
                 :version     "0.1"}
   :commands    [{:command     "hello" :short "h"
                  :description ["Prints hello *!"]
                  :opts        [{:option "to" :short "2" :as "To" :type :string}]
                  :runs        hello}]})

(defn -main
  "This is our entry point.
  Just pass parameters and configuration.
  Commands (functions) will be invoked as appropriate."
  [& args]
  (run-cmd args CONFIGURATION))