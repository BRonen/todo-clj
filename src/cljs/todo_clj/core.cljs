(ns todo-clj.core
  (:require
   [reagent.dom :as rdom]
   [reagent.core :as r]))

(defonce counter (r/atom 0))

(defn page []
    [:div
     [:label (str "current count: " @counter)]
     [:button {:class ["bg-red-500"] :on-click #(swap! counter inc)} (str "Click me!")]])

(defn ^:export render []
  (rdom/render
   [page]
   (.getElementById js/document "app")))
