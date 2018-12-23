Local Development
-----------------

Create a MySQL/MariaDB database user and database after installing server:

```mysql
$ sudo mysql
create database registration character set utf8mb4;
create user kumoreg identified by 'password';
grant all on registration.* to 'kumoreg'@'localhost' identified by 'password';
```