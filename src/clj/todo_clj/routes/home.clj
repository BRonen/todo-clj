(ns todo-clj.routes.home
  (:require
   [todo-clj.layout :as layout]
   [clojure.java.io :as io]
   [todo-clj.middleware :as middleware]
   [ring.util.response]))

(defn home-page [request]
  (layout/render request "home.html" {:docs (-> "docs/docs.md" io/resource slurp)}))

(defn about-page [request]
  (layout/render request "about.html"))

(defn home-routes []
  [""
   {:middleware [middleware/wrap-csrf
                 middleware/wrap-formats]}
   ["/" {:get home-page}]
   ["/about" {:get about-page}]])

