-- name: sql-auth-get-user
select
  id as uid, login, password, role
from users
where login = :login and password = :password
  limit 1

-- name: sql-auth-get-user-password
select password from users where id = :user