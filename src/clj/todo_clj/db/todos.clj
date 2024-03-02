(ns todo-clj.db.todos
  (:require [hugsql.core :as hugsql]))

(hugsql/def-db-fns "clj/todo_clj/db/sql/todos.sql")

(hugsql/def-sqlvec-fns "clj/todo_clj/db/sql/todos.sql")
