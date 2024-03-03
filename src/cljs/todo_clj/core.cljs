(ns todo-clj.core
  (:require
   [reagent.dom :as rdom]
   [reagent.core :as r]
   [reitit.frontend :as rf]
   [reitit.frontend.easy :as rfe]
   [reitit.coercion.spec :as rss]
   [lambdaisland.fetch :as fetch]))

(defn login-page []
  (let [counter (r/atom 0)]
    (fn []
      [:div
       [:label (str "current count: " @counter)]
       [:button {:class ["bg-red-500"] :on-click #(swap! counter inc)} (str "Login")]])))

(defn home-page []
  (let [counter (r/atom 0)]
    (fn []
      [:div
       [:label (str "current count: " @counter)]
       [:button {:class ["bg-red-500"] :on-click #(swap! counter inc)} (str "Home!")]])))


(defonce match (r/atom nil))

(defn current-page []
  [:div
   [:ul
    [:li [:a {:href (rfe/href ::loginpage)} "Frontpage"]]
    [:li [:a {:href (rfe/href ::homepage)} "About"]]]
   (if @match
     (let [view (:view (:data @match))]
       [view @match])
     ())])

(def routes
  [["/login" {:name ::loginpage
              :view login-page}]
   ["/home" {:name ::homepage
             :view home-page}]])

(defn ^:export render []
  (rfe/start!
   (rf/router routes {:data {:coercion rss/coercion}})
   (fn [m] (reset! match m))
      ;; set to false to enable HistoryAPI
   {:use-fragment false})
  (rdom/render
   [current-page]
   (.getElementById js/document "app")))
