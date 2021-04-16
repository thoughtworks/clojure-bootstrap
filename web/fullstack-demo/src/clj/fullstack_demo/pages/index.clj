(ns fullstack-demo.pages.index
  (:require [fullstack-demo.pages.template :as template]))

(defn content []
  (template/->html
    [:div#app]))
