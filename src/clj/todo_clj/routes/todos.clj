(ns todo-clj.routes.todos
  (:require
   [todo-clj.config :refer [env]]
   [todo-clj.middleware :as middleware]
   [ring.util.response]
   [ring.util.http-response :as response]
   [buddy.sign.jwt :as jwt]
   [todo-clj.db.todos :as todos]))

(defn index-page [request]
  (let [db (env :database-options)
        token (get (:headers request) "authorization")
        payload (jwt/unsign token "test-key")
        searchParams (:body-params request)
        result (todos/select-todos db (assoc searchParams :user_id (:id (:user payload))))]
    (response/ok {:todos result})))

(defn create-page [request]
  (let [db (env :database-options)
        token (get (:headers request) "authorization")
        payload (jwt/unsign token "test-key")
        userid (:id (:user payload))
        body (:body-params request)
        result (first (todos/insert-todo db (assoc body :user_id userid)))]
    (response/ok {:todo result})))

(defn delete-page [request]
  (let [db (env :database-options)
        token (get (:headers request) "authorization")
        payload (jwt/unsign token "test-key")
        userid (:id (:user payload))
        body (:body-params request)
        result (first (todos/delete-todo db {:id (:todo_id body) :user_id userid}))]
    (response/ok {:todo result})))

(defn mark-as-complete-page [request]
  (let [db (env :database-options)
        token (get (:headers request) "authorization")
        payload (jwt/unsign token "test-key")
        userid (:id (:user payload))
        body (:body-params request)
        result (first (todos/update-complete-todo db (assoc {:id (:todo_id body) :completed (:completed body)} :user_id userid)))]
    (response/ok {:todo result})))

(defn todos-routes []
  [""
   {:middleware [middleware/wrap-formats]}
   ["/todos/" {:get index-page :post create-page :delete delete-page :patch mark-as-complete-page}]])
