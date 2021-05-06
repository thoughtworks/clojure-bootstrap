(ns ^:figwheel-hooks fullstack-demo-ui.core
  (:require [reagent.dom :as rdom]
            [re-frame.core :as re-frame :refer [dispatch-sync
                                                subscribe]]
            [fullstack-demo-ui.comms]
            [fullstack-demo-ui.system]
            fullstack-demo-ui.events))

(defn header []
  (fn []
    [:header
     [:p "Header"]]))

(defn body []
  (let [hello-msgs (subscribe [:subscription/hello-responses])]
    (fn []
      [:div#main
       [:p "Hello world!"]

       [:ul
        (map-indexed
          (fn [idx msg]
            [:li {:key (str idx "-" msg)} msg])
          @hello-msgs)]])))

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
