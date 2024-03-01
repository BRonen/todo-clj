(ns todo-clj.routes.users
  (:require
   [todo-clj.config :refer [env]]
   [todo-clj.middleware :as middleware]
   [ring.util.response]
   [ring.util.http-response :as response]
   [todo-clj.db.users :as users]))

(defn index-page [request]
  (let [db (env :database-options)
        searchParams (:body-params request)
        result (users/select-user db searchParams)]
    (response/ok {:users result})))

(defn create-page [request]
  (let [db (env :database-options)
        userData (:body-params request)
        result (users/insert-user db userData)]
    (response/ok {:user result})))

(defn users-routes []
  [""
   {:middleware [middleware/wrap-formats]}
   ["/users/" {:get index-page :post create-page}]])

