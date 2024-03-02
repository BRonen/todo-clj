-- src/clj/todo_clj/db/sql/todos.sql
-- To-do todo

-- :name select-todos
-- :doc Insert multiple characters with :tuple* parameter type
SELECT id, name, description, completed, user_id
FROM todos
WHERE user_id = :user_id
LIMIT :limit

-- :name insert-todo
-- :doc Insert multiple characters with :tuple* parameter type
INSERT INTO todos (name, description, completed, user_id)
VALUES (:name, :description, :completed, :user_id)
RETURNING id, name, description, completed, user_id

-- :name delete-todo
-- :doc Insert multiple characters with :tuple* parameter type
DELETE FROM todos
WHERE user_id = :user_id AND id = :id
RETURNING *

-- :name insert-characters :! :n
-- :doc Insert multiple characters with :tuple* parameter type
/* insert into characters (username, password)
values :tuple*:user
 */
