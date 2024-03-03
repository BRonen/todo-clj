(defproject todo-clj "0.1.0-SNAPSHOT"

  :description "To do list app made with clojure and luminus"
  :url "https://github.com/bronen/todo-clj"

  :dependencies [[ch.qos.logback/logback-classic "1.4.4"]
                 [clojure.java-time "1.1.0"]
                 [cprop "0.1.19"]
                 [expound "0.9.0"]
                 [funcool/struct "1.4.0"]
                 [json-html "0.4.7"]
                 [luminus-transit "0.1.5"]
                 [luminus-undertow "0.1.18"]
                 [luminus/ring-ttl-session "0.3.3"]
                 [markdown-clj "1.11.3"]
                 [metosin/muuntaja "0.6.8"]
                 [metosin/reitit "0.5.18"]
                 [metosin/ring-http-response "0.9.3"]
                 [mount "0.1.16"]
                 [nrepl "1.0.0"]
                 [org.clojure/clojure "1.11.1"]
                 [org.clojure/tools.cli "1.0.214"]
                 [org.clojure/tools.logging "1.2.4"]
                 [org.webjars.npm/bulma "0.9.4"]
                 [org.webjars.npm/material-icons "1.10.8"]
                 [org.webjars/webjars-locator "0.45"]
                 [org.webjars/webjars-locator-jboss-vfs "0.1.0"]
                 [ring-webjars "0.2.0"]
                 [ring/ring-core "1.9.6"]
                 [ring/ring-defaults "0.3.4"]
                 [selmer "1.12.55"]
                 [migratus "1.5.5"]
                 [com.layerware/hugsql "0.5.3"]
                 [org.postgresql/postgresql "42.1.4"]
                 [buddy/buddy-sign "1.1.0"]
                 [buddy/buddy-hashers "1.4.0"]
                 [reagent "1.1.0"]
                 [thheller/shadow-cljs "2.15.2" :scope "provided"]
                 [cljsjs/react "17.0.2-0"]
                 [cljsjs/react-dom "17.0.2-0"]
                 [lambdaisland/fetch "1.5.83"]]

  :min-lein-version "2.0.0"

  :source-paths ["src/clj" "src/cljs"]
  :test-paths ["test/clj"]
  :resource-paths ["resources", "src"]
  :target-path "target/%s/"
  :main ^:skip-aot todo-clj.core

  :plugins []

  :profiles
  {:uberjar {:omit-source true
             :aot :all
             :uberjar-name "todo-clj.jar"
             :source-paths ["env/prod/clj"]
             :resource-paths ["env/prod/resources"]
             :prep-tasks ["compile" ["run" "-m" "shadow.cljs.devtools.cli" "release" "app"]]}

   :dev           [:project/dev :profiles/dev]
   :test          [:project/dev :project/test :profiles/test]

   :project/dev  {:jvm-opts ["-Dconf=dev-config.edn"]
                  :dependencies [[org.clojure/tools.namespace "1.3.0"]
                                 [pjstadig/humane-test-output "0.11.0"]
                                 [prone "2021-04-23"]
                                 [ring/ring-devel "1.9.6"]
                                 [ring/ring-mock "0.4.0"]]
                  :plugins      [[com.jakemccrary/lein-test-refresh "0.24.1"]
                                 [jonase/eastwood "1.2.4"]
                                 [cider/cider-nrepl "0.26.0"]
                                 [migratus-lein "0.7.3"]]

                  :source-paths ["env/dev/clj"]
                  :resource-paths ["env/dev/resources"]
                  :repl-options {:init-ns user
                                 :timeout 120000}
                  :injections [(require 'pjstadig.humane-test-output)
                               (pjstadig.humane-test-output/activate!)]}
   :project/test {:jvm-opts ["-Dconf=test-config.edn"]
                  :resource-paths ["env/test/resources"]}
   :profiles/dev {}
   :profiles/test {}}
  :migratus {:store :database
             :migration-dir "./resources/migrations"
             :db {:dbtype "postgresql"
                  :dbname "db"
                  :user "postgres"
                  :host "localhost"
                  :password "password"}})
