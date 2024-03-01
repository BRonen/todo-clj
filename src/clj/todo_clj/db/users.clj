(ns todo-clj.db.users
  (:require [hugsql.core :as hugsql]))

(hugsql/def-db-fns "clj/todo_clj/db/sql/users.sql")

(hugsql/def-sqlvec-fns "clj/todo_clj/db/sql/users.sql")
