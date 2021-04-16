(ns user
  (:require [figwheel.main.api :as figwheel]
            [mount.core :as m]
            [hawk.core :as hawk]
            [clojure.string :as st]
            [clojure.java.io :as io]
            [clojure.edn :as edn]
            [fullstack-demo.core :as fc]
            [fullstack-demo.styles.main :as main-css])
  (:import (java.io File)))

(defn measure [f]
  (let [now (System/nanoTime)
        res (f)]
    (println "Took" (/ (- (System/nanoTime) now) 1e6) "ms")
    res))

(defn- open-browser [url]
  (println "Open browser to" url "and start hacking!"))

(defn compile-garden-css! []
  (require 'fullstack-demo.styles.main :reload-all)

  (let [f (io/file "./resources/public/css/style.css")]
    (io/make-parents f)
    (spit f main-css/styles)))

(m/defstate figwheel
  :start (figwheel/start {:mode :serve} "dev")
  :stop (figwheel/stop "dev"))

(m/defstate garden-watcher
  :start (hawk/watch! [{:paths   ["src/clj/fullstack_demo/styles"]
                        :handler (fn [ctx e]
                                   (when (and (= :modify (:kind e))
                                              (st/ends-with? (.getAbsolutePath (:file e)) ".clj"))
                                     (print "Garden CSS change recognized, recompiling ... ")
                                     (measure #(compile-garden-css!)))
                                   ctx)}])
  :stop (hawk/stop! garden-watcher))

(defn start []
  (let [conf (edn/read-string (slurp "dev-resources/config.edn"))]
    (println "STARTING CLOJURESCRIPT DEV ENVIRONMENT")
    (m/start #'figwheel #'garden-watcher)
    (println "COMPILING GARDEN CSS")
    (compile-garden-css!)
    (println "STARTING CLOJURE BACKEND")
    (fc/start conf)
    (open-browser (str "http://localhost:" (-> conf :server :port)))))

(defn stop []
  (println "STOPPING DEV ENVIRONMENT")
  (m/stop))

(defn restart-backend []
  (m/stop-except #'figwheel #'garden-watcher)
  (let [conf (edn/read-string (slurp "dev-resources/config.edn"))]
    (fc/start conf)))

(defn delete-recursively! [^File f]
  (when (.isDirectory f)
    (doseq [f2 (.listFiles f)]
      (delete-recursively! f2)))
  (.delete f))

(defn delete-dir-content! [path]
  (doseq [f (.listFiles (io/as-file path))]
    (delete-recursively! f)))

(defn force-cljs-rebuild []
  (m/stop #'figwheel)
  (delete-dir-content! "resources/public/js")
  (m/start #'figwheel))

(defn cljs-repl []
  (figwheel/cljs-repl "dev"))
