
Technologies
------------
Service built on:
  - [Spring Framework](https://docs.spring.io/spring/docs/5.1.3.RELEASE/spring-framework-reference/)
  - [Thymeleaf](https://www.thymeleaf.org/doc/tutorials/2.1/thymeleafspring.html) Templating language
  - [CSS Bootstrap](https://getbootstrap.com/docs/3.4/css/)
  - [MariaDB/MySQL](https://mariadb.com/kb/en/library/documentation/)
  

Local Development
-----------------

Create a MySQL/MariaDB database user and database after installing server:

```mysql
$ sudo mysql
create database registration character set utf8mb4;
create user kumoreg identified by 'password';
grant all on registration.* to 'kumoreg'@'localhost' identified by 'password';
```
