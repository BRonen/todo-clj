-- src/clj/todo_clj/db/sql/users.sql
-- To-do users

-- :name select-user
-- :doc Insert multiple characters with :tuple* parameter type
SELECT id, username
FROM users
LIMIT :limit

-- :name insert-user
-- :doc Insert multiple characters with :tuple* parameter type
INSERT INTO users (username, password)
VALUES (:username, :password)
RETURNING id, username

-- :name select-user-by-email
-- :doc Insert multiple characters with :tuple* parameter type
SELECT id, username, password
FROM users
WHERE username = :username

-- :name insert-characters :! :n
-- :doc Insert multiple characters with :tuple* parameter type
/* insert into characters (username, password)
values :tuple*:user
 */
