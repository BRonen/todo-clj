(ns todo-clj.env
  (:require [clojure.tools.logging :as log]))

(def defaults
  {:init
   (fn []
     (log/info "\n-=[todo-clj started successfully]=-"))
   :stop
   (fn []
     (log/info "\n-=[todo-clj has shut down successfully]=-"))
   :middleware identity})
