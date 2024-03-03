(ns todo-clj.core
  (:require
   [reagent.dom :as rdom]
   [reagent.core :as r]
   [reitit.frontend :as rf]
   [reitit.frontend.easy :as rfe]
   [reitit.coercion.spec :as rss]
   [lambdaisland.fetch :as fetch]
   [goog.object :as gobj]))

(defn register-user [payload]
  (-> (fetch/post "/users/" {:accept :json
                             :content-type :json
                             :body payload})
      (.then (fn [resp] (.log js/console (-> resp :body (gobj/get "users")))))))

(defn register-page []
  (let [formstate (r/atom {:username "" :password ""})
        submithandler (fn [e] (.preventDefault e) (register-user @formstate))]
    (fn []
      [:div
       [:form {:class "flex flex-col w-2/3 md:w-1/3 mx-auto mt-12" :on-submit submithandler}
        [:h1 {:class "text-xl text-center"} "Register"]
        [:label {:for "username"} "Username"]
        [:input {:type "text"
                 :placeholder "username..."
                 :name "username"
                 :id "username"
                 :class "px-2 py-1 border rounded mb-2"
                 :on-change #(swap! formstate assoc :username (.-value (.-target %)))
                 :value (str (:username @formstate))}]
        [:label {:for "password"} "Password"]
        [:input {:type "text"
                 :placeholder "password..."
                 :name "password"
                 :id "password"
                 :class "px-2 py-1 border rounded"
                 :on-change #(swap! formstate assoc :password (.-value (.-target %)))
                 :value (str (:password @formstate))}]
        [:button {:class ["bg-stone-300 mt-4 rounded p-1"]} "Register"]]])))

(defn login-user [payload]
  (-> (fetch/post "/users/auth" {:accept :json
                                 :content-type :json
                                 :body payload})
      (.then (fn [resp] (.log js/console (-> resp :body (gobj/get "users")))))))

(defn login-page []
  (let [formstate (r/atom {:username "" :password ""})
        submithandler (fn [e] (.preventDefault e) (login-user @formstate))]
    (fn []
      [:div
       [:form {:class "flex flex-col w-2/3 md:w-1/3 mx-auto mt-12" :on-submit submithandler}
        [:h1 {:class "text-xl text-center"} "Login"]
        [:label {:for "username"} "Username"]
        [:input {:type "text"
                 :placeholder "username..."
                 :name "username"
                 :id "username"
                 :class "px-2 py-1 border rounded mb-2"
                 :on-change #(swap! formstate assoc :username (.-value (.-target %)))
                 :value (str (:username @formstate))}]
        [:label {:for "password"} "Password"]
        [:input {:type "text"
                 :placeholder "password..."
                 :name "password"
                 :id "password"
                 :class "px-2 py-1 border rounded"
                 :on-change #(swap! formstate assoc :password (.-value (.-target %)))
                 :value (str (:password @formstate))}]
        [:button {:class ["bg-stone-300 mt-4 rounded p-1"]} (str "Login")]]])))

(defn home-page []
  (let [counter (r/atom 0)]
    (fn []
      [:div
       [:label (str "current count: " @counter)]
       [:button {:class ["bg-red-500"] :on-click #(swap! counter inc)} (str "Home!")]])))

(defonce match (r/atom nil))

(defn current-page []
  [:div
   [:ul {:class "flex gap-5"}
    [:li [:a {:href (rfe/href ::loginpage)} "Login"]]
    [:li [:a {:href (rfe/href ::homepage)} "About"]]
    [:li [:a {:href (rfe/href ::registerpage)} "Register"]]]
   (if @match
     (let [view (:view (:data @match))]
       [view @match])
     ())])

(def routes
  [["/" {:name ::homepage
         :view home-page}]
   ["/login" {:name ::loginpage
              :view login-page}]
   ["/register" {:name ::registerpage
                 :view register-page}]])

(defn ^:export render []
  (rfe/start!
   (rf/router routes {:data {:coercion rss/coercion}})
   (fn [m] (reset! match m))
   {:use-fragment false})
  (rdom/render
   [current-page]
   (.getElementById js/document "app")))
