-- src/clj/todo_clj/db/sql/users.sql
-- To-do users

-- :name insert-character
-- :doc Insert a single character returning affected row count
insert into users (username, password)
values (:username, :password)
returning username

-- :name insert-characters :! :n
-- :doc Insert multiple characters with :tuple* parameter type
/* insert into characters (username, password)
values :tuple*:user
 */
