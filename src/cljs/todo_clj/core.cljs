(ns todo-clj.core
  (:require
   [reagent.dom :as rdom]
   [reagent.core :as r]
   [reitit.frontend :as rf]
   [reitit.frontend.easy :as rfe]
   [reitit.coercion.spec :as rss]
   [lambdaisland.fetch :as fetch]
   [goog.object :as gobj]))

(defn loading-component []
  [:div
   {:class "h-60 flex items-center justify-center"}
   [:div {:style {:width "3rem",
                  :height "3rem",
                  :margin "auto",
                  :border "3px solid transparent",
                  :border-bottom "3px solid black",
                  :border-top "3px solid black",
                  :border-radius "9999px",
                  :animation "roll 1.5s linear infinite"}}]])

(defn register-user [payload]
  (-> (fetch/post "/users/" {:accept :json
                             :content-type :json
                             :body payload})
      (.then (fn [resp] (let [token (-> resp :body (gobj/get "token"))]
                          (.setItem (.-localStorage js/window) "auth_token" token)
                          (rfe/push-state ::homepage))))))

(defn register-page []
  (let [formstate (r/atom {:username "" :password ""})
        loading (r/atom false)
        submithandler (fn [e] (.preventDefault e) (swap! loading (fn [_] true)) (register-user @formstate))]
    (fn []
      (if @loading loading-component [:div
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
                                       [:button {:class ["bg-stone-300 mt-4 rounded p-1"]} "Register"]]]))))

(defn login-user [payload]
  (-> (fetch/post "/users/auth" {:accept :json
                                 :content-type :json
                                 :body payload})
      (.then (fn [resp] (let [token (-> resp :body)]
                          (.setItem (.-localStorage js/window) "auth_token" token)
                          (rfe/push-state ::homepage))))))

(defn login-page []
  (let [formstate (r/atom {:username "" :password ""})
        loading (r/atom false)
        submithandler (fn [e] (.preventDefault e) (swap! loading (fn [_] true)) (login-user @formstate))]
    (fn []
      (if @loading loading-component [:div
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
                                       [:button {:class ["bg-stone-300 mt-4 rounded p-1"]} (str "Login")]]]))))

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
