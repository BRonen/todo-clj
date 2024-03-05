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
                                       [:input {:type "password"
                                                :placeholder "password..."
                                                :name "password"
                                                :id "password"
                                                :class "px-2 py-1 border rounded"
                                                :on-change #(swap! formstate assoc :password (.-value (.-target %)))
                                                :value (str (:password @formstate))}]
                                       [:a {:class "w-full text-end underline" :href (rfe/href ::loginpage)} "login page"]
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
                                       [:input {:type "password"
                                                :placeholder "password..."
                                                :name "password"
                                                :id "password"
                                                :class "px-2 py-1 border rounded"
                                                :on-change #(swap! formstate assoc :password (.-value (.-target %)))
                                                :value (str (:password @formstate))}]
                                       [:a {:class "w-full text-end underline" :href (rfe/href ::registerpage)} "register page"]
                                       [:button {:class ["bg-stone-300 mt-4 rounded p-1"]} (str "Login")]]]))))

(defn mark-as-completed [todo]
  (-> (fetch/request "/todos/" {:method :patch
                                :accept :json
                                :content-type :json
                                :body {:todo_id (.-id todo) :completed (not (boolean (.-completed todo)))}
                                :headers {"authorization" (.getItem (.-localStorage js/window) "auth_token")}})
      (.then (fn [_] (rfe/push-state ::homepage)))))

(defn delete-todo [todo]
  (fetch/delete "/todos/" {:accept :json
                               :content-type :json
                               :body {:todo_id (.-id todo) :completed (not (boolean (.-completed todo)))}
                               :headers {"authorization" (.getItem (.-localStorage js/window) "auth_token")}}))

(defn get-todos []
  (-> (fetch/get "/todos/?limit=10" {:accept :json
                                     :content-type :json
                                     :headers {"authorization" (.getItem (.-localStorage js/window) "auth_token")}})
      (.then (fn [response] (-> response :body (gobj/get "todos") vec)))))

(defn home-page []
  (let [loading (r/atom true)
        todos (r/atom [])
        load-todos (fn []
                     (reset! loading true)
                     (-> (get-todos)
                         (.then (fn [data]
                                  (reset! todos data)
                                  (reset! loading false)))))
        checkbox-handler (fn [todo] (-> (mark-as-completed todo) (.then load-todos)))]
    (r/create-class
     {:display-name "home-page"
      :reagent-render (fn home-page-render []
                        (if @loading
                          (loading-component)
                          [:div {:class "flex flex-col gap-4 md:w-1/3 w-2/3 mx-auto"}
                           [:h1 {:class "text-3xl"} "To-dos:"]
                           [:ul (map (fn [todo]
                                       [:li {:key (.-id todo) :class "flex gap-4 mt-2 cursor-pointer"}
                                        [:input {:type "checkbox" :class "w-4 cursor-pointer" :default-checked (.-completed todo) :on-click (fn [] (checkbox-handler todo))}]
                                        [:div {:on-click (fn [] (checkbox-handler todo))}
                                         [:h1 {:class (str "text-xl" (when (.-completed todo) " text-stone-400 line-through"))} (.-name todo)]
                                         [:p {:class (when (.-completed todo) "text-stone-400 line-through")} (.-description todo)]]
                                        [:button {:class "ml-auto flex justify-center items-center" :on-click (fn [] (-> (delete-todo todo) ((fn [_] (load-todos)))))}
                                         [:img {:width "24" :height "24"
                                                :src "https://api.iconify.design/material-symbols:delete-outline.svg"
                                                :alt "delete to-do button"}]]]) @todos)]
                           [:button {:class ["bg-stone-300 mt-3"] :on-click (fn [] (rfe/push-state ::newtodo))} "New todo"]]))
      :component-did-mount load-todos})))

(defn create-todo [payload]
  (-> (fetch/post "/todos/" {:accept :json
                             :content-type :json
                             :headers {"authorization" (.getItem (.-localStorage js/window) "auth_token")}
                             :body (assoc payload :completed false)})
      (.then (fn [_] (rfe/push-state ::homepage)))))

(defn new-todo-page []
  (let [formstate (r/atom {:name "" :description ""})
        loading (r/atom false)
        submithandler (fn [e] (.preventDefault e) (swap! loading (fn [_] true)) (create-todo @formstate))]
    (fn []
      (if @loading loading-component [:div
                                      [:form {:class "flex flex-col w-2/3 md:w-1/3 mx-auto mt-12" :on-submit submithandler}
                                       [:h1 {:class "text-xl text-center"} "New Todo"]
                                       [:label {:for "name"} "Name"]
                                       [:input {:type "text"
                                                :placeholder "name"
                                                :name "name"
                                                :id "name"
                                                :class "px-2 py-1 border rounded mb-2"
                                                :on-change #(swap! formstate assoc :name (.-value (.-target %)))
                                                :value (str (:name @formstate))}]
                                       [:label {:for "description"} "Description"]
                                       [:input {:type "description"
                                                :placeholder "description"
                                                :name "description"
                                                :id "description"
                                                :class "px-2 py-1 border rounded"
                                                :on-change #(swap! formstate assoc :description (.-value (.-target %)))
                                                :value (str (:description @formstate))}]
                                       [:div {:class "flex gap-2"}
                                        [:button {:class ["bg-stone-300 mt-4 rounded p-1 w-full"] :on-click (fn [e] (.preventDefault e) (rfe/push-state ::homepage))} "Cancel"]
                                        [:button {:class ["bg-stone-300 mt-4 rounded p-1 w-full"]} "Confirm"]]]]))))

(defonce match (r/atom nil))

(defn current-page []
  [:div
   (when (= (:view (:data @match)) home-page)
     [:div
      {:class "flex justify-end w-full"}
      [:a {:class "py-2 px-4"
           :href (rfe/href ::loginpage)
           :on-click (fn [] (.clear (.-localStorage js/window) "auth_token"))} "Logout"]])
   (if @match
     (let [view (:view (:data @match))]
       [view @match])
     ())])

(def routes
  [["/" {:name ::homepage
         :view home-page}]
   ["/new-todo" {:name ::newtodo
                 :view new-todo-page}]
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
