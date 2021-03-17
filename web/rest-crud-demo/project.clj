(defproject rest-crud-demo "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.10.0"]
                 ; Web
                 [prismatic/schema "1.1.9"]
                 [metosin/compojure-api "2.0.0-alpha31" :exclusions [frankiesardo/linked]]
                 [ring/ring-jetty-adapter "1.6.3"]

                 ; Database
                 [toucan "1.1.9"]
                 [org.postgresql/postgresql "42.2.4"]
                 [com.mchange/c3p0 "0.9.5.2"]

                 ; Password Hashing
                 [buddy/buddy-hashers "1.3.0"]

                 [org.clojure/tools.reader "1.2.2"]
                 ;; log
                 [com.taoensso/timbre "4.8.0"]]
  :main ^:skip-aot rest-crud-demo.core
  :jvm-opts ["-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5010"]
  :target-path "target/%s"
  :ring {:handler rest-crud-demo.core/app}
  :profiles {:dev {:dependencies [[javax.servlet/javax.servlet-api "3.1.0"]]
                   :plugins [[lein-ring "0.12.5"]]
                   :ring {:port 8080}}
             :uberjar    {:aot :all
                          :ring {:port 80}}})
