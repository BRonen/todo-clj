(ns todo-clj.routes.users
  (:require
   [todo-clj.config :refer [env]]
   [todo-clj.middleware :as middleware]
   [ring.util.response]
   [ring.util.http-response :as response]
   [buddy.sign.jwt :as jwt]
   [buddy.hashers :as hashers]
   [todo-clj.db.users :as users]))

(defn index-page [request]
  (let [db (env :database-url)
        rawparams (:params request)
        searchParams {:limit (Integer/parseInt (:limit rawparams))}
        result (users/select-users db searchParams)]
    (response/ok {:users result})))

(defn create-page [request]
  (let [db (env :database-url)
        body (:body-params request)
        userdata {:username (:username body)
                  :password (hashers/derive (:password body))}
        result (first (users/insert-user db userdata))
        token (jwt/sign {:user result} "test-key")]
    (response/ok {:user result :token token})))

(defn check-auth-page [request]
  (let [token (get (:headers request) "authorization")
        payload (jwt/unsign token "test-key")]
    (response/ok payload)))

(defn create-auth-page [request]
  (let [db (env :database-url)
        body (:body-params request)
        result (first (users/select-user-by-email db body))]
    (if (hashers/check
         (:password body)
         (:password result))
      (response/ok (jwt/sign {:user (dissoc result :password)} "test-key"))
      (response/ok {:error "user not found or wrong password"}))))

(defn users-routes []
  [""
   {:middleware [middleware/wrap-formats]}
   ["/users/" {:get index-page :post create-page}]
   ["/users/auth" {:get check-auth-page :post create-auth-page}]])

