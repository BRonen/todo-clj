(ns todo-clj.routes.home
  (:require
   [todo-clj.config :refer [env]]
   [todo-clj.layout :as layout]
   [clojure.java.io :as io]
   [todo-clj.middleware :as middleware]
   [ring.util.response]
   [ring.util.http-response :as response]
   [todo-clj.db.users :as users]))

(defn tttt-page [request]
  (println request)
  (let [db (env :database-url)]
    (println (users/insert-user db {:username "hello" :password "worldpass"})))
  (response/ok {:foo "bar"}))

(defn home-page [request]
  (layout/render request "home.html" {:docs (-> "docs/docs.md" io/resource slurp)}))

(defn about-page [request]
  (layout/render request "about.html"))

(defn home-routes []
  [""
   {:middleware [middleware/wrap-csrf
                 middleware/wrap-formats]}
   ["/" {:get home-page}]
   ["/wasd" {:get tttt-page}]
   ["/about" {:get about-page}]])

