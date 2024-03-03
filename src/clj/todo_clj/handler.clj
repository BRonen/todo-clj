(ns todo-clj.handler
  (:require
    [todo-clj.middleware :as middleware]
    [todo-clj.layout :refer [error-page, render]]
    [todo-clj.routes.home :refer [home-routes]]
    [todo-clj.routes.users :refer [users-routes]]
    [todo-clj.routes.todos :refer [todos-routes]]
    [reitit.ring :as ring]
    [ring.middleware.content-type :refer [wrap-content-type]]
    [ring.middleware.webjars :refer [wrap-webjars]]
    [todo-clj.env :refer [defaults]]
    [mount.core :as mount]))

(mount/defstate init-app
  :start ((or (:init defaults) (fn [])))
  :stop  ((or (:stop defaults) (fn []))))

(defn- async-aware-default-handler
  ([_] nil)
  ([_ respond _] (respond nil)))


(mount/defstate app-routes
  :start
  (ring/ring-handler
    (ring/router
      [(home-routes) (users-routes) (todos-routes)])
    (ring/routes
      (wrap-content-type
        (wrap-webjars async-aware-default-handler))
      (ring/create-default-handler
        {:not-found
         (fn home-page [request] (render request "index.html"))
         :method-not-allowed
         (constantly (error-page {:status 405, :title "405 - Not allowed"}))
         :not-acceptable
         (constantly (error-page {:status 406, :title "406 - Not acceptable"}))}))))

(defn app []
  (middleware/wrap-base #'app-routes))
