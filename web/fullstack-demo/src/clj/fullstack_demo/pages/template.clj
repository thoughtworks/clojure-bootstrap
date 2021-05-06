(ns fullstack-demo.pages.template
  (:require [hiccup.core :as html]
            [clojure.java.io :as io]
            [ring.middleware.anti-forgery :as mw-af]
            [fullstack-demo.styles.main :as styles]
            [fullstack-demo.config :as conf]))

(defn- read-app-js-res* []
  (slurp (io/resource "public/js/app.js")))

(def read-app-js-res (memoize read-app-js-res*))

(defn head []
  [:head
   [:title "ThoughtWorks Clojure Full Stack Demo"]
   [:meta {:charset "UTF-8"}]
   (if (conf/dev-mode?)
     [:link {:href "css/style.css" :type "text/css" :rel "stylesheet"}]
     [:style styles/all])])

(defn body [content]
  [:body
   [:span#csrf-token {:style           :none
                      :data-csrf-token mw-af/*anti-forgery-token*}]
   [:div#app]
   (if (conf/dev-mode?)
     [:script {:src "js/app.js"}]
     [:script (read-app-js-res)])])

(defn ->html [& content]
  (html/html
    (head)
    (body content)))
