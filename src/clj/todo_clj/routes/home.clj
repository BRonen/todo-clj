(ns todo-clj.routes.home
  (:require
   [todo-clj.layout :as layout]
   [todo-clj.middleware :as middleware]
   [ring.util.response]))

(defn home-page [request] (layout/render request "index.html"))

(defn home-routes []
  [""
   {:middleware [middleware/wrap-csrf
                 middleware/wrap-formats]}
   ["/" {:get home-page}]])

