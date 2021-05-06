(defproject fullstack-demo "0.1.0-SNAPSHOT"
  :dependencies [[org.clojure/clojure "1.10.3"]
                 [org.clojure/clojurescript "1.10.773" :exclusions [org.clojure/tools.reader]]
                 [org.clojure/tools.reader "1.3.5"]

                 ; System level stuff
                 [mount "0.1.16"]
                 [com.cemerick/url "0.1.1"]
                 [com.taoensso/sente "1.16.0" :exclusions [org.clojure/tools.analyzer]]
                 [org.clojure/core.async "1.3.610" :exclusions [org.clojure/tools.analyzer]]
                 [org.clojure/tools.analyzer "1.0.0"]

                 ; Server API stuff
                 [aleph "0.4.6" :exclusions [org.clojure/tools.logging]]
                 [ring/ring-core "1.9.1"]
                 [ring/ring-anti-forgery "1.3.0"]
                 [compojure "1.6.2" :exclusions [ring/ring-codec]]
                 [ring/ring-codec "1.1.3"]
                 [metosin/jsonista "0.2.7"]

                 ; Server side libs
                 [hiccup "1.0.5"]
                 [garden "1.3.10"]
                 [yogthos/config "1.1.7"]
                 [com.cognitect/transit-clj "1.0.324"]

                 ; Logging and bindings for Java libs, one more under uberjar profile!
                 [com.taoensso/timbre "5.1.0" :exclusions [com.taoensso/encore]]
                 [com.taoensso/encore "3.9.2"]
                 [org.slf4j/log4j-over-slf4j "1.7.30"]
                 [org.slf4j/jul-to-slf4j "1.7.30"]
                 [org.slf4j/jcl-over-slf4j "1.7.30"]
                 [org.slf4j/slf4j-nop "1.7.30"]]

  :plugins [[lein-garden "0.3.0" :exclusions [garden]]
            [lein-cljfmt "0.6.4"]
            ; plugin to help recognizing old dependencies, use with "lein ancient"
            [lein-ancient "0.6.15"]]

  :source-paths ["src/clj" "src/cljs" "src/cljc"]
  :test-paths ["test/clj" "test/cljs" "test/cljc"]

  :profiles {
             ; Client only libs - do not add CLJS dependencies to normal dependencies list unless they are
             ; for both backend and frontend. Why? Since if you do, they'll end up into uberjar doing nothing,
             ; they are only used when doing CLJS advanced compilation where all of that is combined into one.
             :provided {:dependencies [[com.bhauman/figwheel-main "0.2.13" :exclusions [org.eclipse.jetty/jetty-xml]]
                                       [re-frame "1.1.2" :exclusions [org.clojure/tools.reader
                                                                      reagent]]
                                       [reagent "0.10.0"]
                                       [reagent-utils "0.3.3" :exclusions [org.clojure/tools.reader]]
                                       [cljs-http "0.1.46" :exclusions [org.clojure/core.async
                                                                        com.cognitect/transit-cljs]]
                                       [com.cognitect/transit-cljs "0.8.264"]
                                       [haslett "0.1.6" :exclusions [org.clojure/clojurescript]]
                                       [com.andrewmcveigh/cljs-time "0.5.2"]]}


             :dev      {:dependencies   [[figwheel-sidecar "0.5.20" :exclusions [org.clojure/tools.nrepl]]
                                         [cider/piggieback "0.5.2"]
                                         [hawk "0.2.11"]
                                         #_[org.clojure/tools.namespace "1.0.0"]
                                         [binaryage/devtools "1.0.2"]
                                         [day8.re-frame/test "0.1.5"]
                                         [re-frisk "1.3.4" :exclusions [org.clojure/tools.analyzer
                                                                        org.clojure/clojurescript]]
                                         #_[clj-chrome-devtools "20200423"]]
                        :repl-options   {:nrepl-middleware [cider.piggieback/wrap-cljs-repl]}
                        :source-paths   ["dev" "src/cljs"]
                        :resource-paths ["resources" "dev-resources" "target"]
                        :main           user}

             :uberjar  {:aot          [fullstack-demo.core]
                        ; add slf4j-timbre to project only when using uberjar. This is to circumvent a
                        ; problem with Figwheel main where it causes a ton of unnecessary Jetty logging to
                        ; appear.
                        :dependencies [[com.fzakaria/slf4j-timbre "0.3.20"]]
                        :auto-clean   false}}

  :garden {:builds [{:id           "prod"
                     :source-paths ["src/clj"]
                     :stylesheet   fullstack-demo.styles.main/all
                     :compiler     {:output-to     "resources/public/css/style.css"
                                    :pretty-print? false}}]}

  :clean-targets ^{:protect false} ["resources/public/js" :target
                                    "resources/public/css" :target]


  :aliases {"fig"            ["trampoline" "run" "-m" "figwheel.main"]
            "fig:min"        ["run" "-m" "figwheel.main" "-bo" "prod"]
            "garden-compile" ["garden" "once"]

            "make-uberjar"   ^{:doc "Cleans, compiles cljs code, then creates an uberjar"}
                             ["do" ["clean"] ["garden-compile"] ["fig:min"] ["uberjar"]]}

  :uberjar-name "fullstack-demo.jar"

  :main fullstack-demo.core)

