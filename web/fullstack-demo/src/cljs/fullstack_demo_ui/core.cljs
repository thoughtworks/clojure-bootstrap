(ns ^:figwheel-hooks fullstack-demo-ui.core
  (:require [reagent.dom :as rdom]
            [re-frame.core :as re-frame :refer [dispatch-sync
                                                subscribe]]))

(defn header []
  (fn []
    [:header
     [:p "Header"]]))

(defn body []
  (fn []
    [:div#main
     [:p "Hello world!"]]))

(defn main-component []
  [:<>
   [header]
   [body]])

(defn render []
  (rdom/render main-component (.getElementById js/document "app")))

(defn run []
  (dispatch-sync [:system/init!])
  (render))

(defn ^:after-load fig-reload []
  (re-frame/clear-subscription-cache!)
  (render))

(.addEventListener js/window "DOMContentLoaded" run)
