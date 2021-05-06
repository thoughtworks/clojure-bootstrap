(ns fullstack-demo.api
  (:require [ring.util.response :as r]
            [compojure.core :as c]
            [fullstack-demo.config :as conf]
            [fullstack-demo.comms :as comms]
            [fullstack-demo.pages.index :as index-page]
            [ring.middleware.keyword-params :refer [wrap-keyword-params]]
            [ring.middleware.params :refer [wrap-params]]
            [ring.middleware.cookies :refer [wrap-cookies]]
            [ring.middleware.anti-forgery :refer [wrap-anti-forgery]]
            [ring.middleware.session :refer [wrap-session]]
            [clojure.java.io :as io]
            [fullstack-demo.handler-mapping]))

(defn resource-handler [root-path]
  (fn [request]
    (let [f (-> request :params :*)]
      (if-let [res (io/resource (str root-path f))]
        res
        (r/not-found (str root-path " not found"))))))

(def resource-routes
  (c/routes
    (c/GET "/img/*" request (resource-handler "public/img/"))
    (c/GET "/js/*" request (resource-handler "public/js/"))
    (c/GET "/css/*" request (resource-handler "public/css/"))
    (c/GET "/fonts/*" request (resource-handler "public/fonts/"))))

(def sente-routes
  (c/routes
    (c/GET  "/chsk" req ((comms/chsk-get-handler) req))
    (c/POST "/chsk" req ((comms/chsk-post-handler) req))))

(defn no-cache [f]
  (fn [request]
    (when-let [body (f request)]
      (-> body
          (r/header "Cache-Control" "no-cache, no-store, must-revalidate")
          (r/header "Pragma" "no-cache")
          (r/header "Expires" "0")))))

(def handler
  (-> (c/routes
        (c/routes
          (if (conf/dev-mode?)
            (no-cache resource-routes)
            resource-routes)
          (no-cache sente-routes)
          (no-cache (c/GET "/" [] (index-page/content)))
          (c/ANY "/*" [] (r/not-found "Not found"))))
      (wrap-anti-forgery)
      wrap-session
      wrap-cookies
      wrap-keyword-params
      wrap-params))

