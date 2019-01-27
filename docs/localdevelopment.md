
Technologies
------------
Service built on:
  - [Spring Framework](https://docs.spring.io/spring/docs/5.1.3.RELEASE/spring-framework-reference/)
  - [Thymeleaf](https://www.thymeleaf.org/doc/tutorials/2.1/thymeleafspring.html) Templating language
  - [CSS Bootstrap](https://getbootstrap.com/docs/3.4/css/)
  - [MariaDB/MySQL](https://mariadb.com/kb/en/library/documentation/)
  

Local Development
-----------------
Install Postgresql Server (commands vary)

Create a Postgresql database user "kumoreg" and database "registration" after installing server:

```
root@www0:~# su - postgres 
postgres@www0:~$ createuser --interactive -P kumoreg
    Enter password for new role:
    Enter it again:
    Shall the new role be a superuser? (y/n) n
    Shall the new role be allowed to create databases? (y/n) n
    Shall the new role be allowed to create more new roles? (y/n) n

    postgres@www0:~$ createdb -O someuser registration
```

Build and run the server with maven:
```
mvn spring-boot:run
```

Handy Postgres Commands
-----------------------
```
# Connect with command line SQL client:
psql -h 127.0.0.1 -U kumoreg registration
```

Architecture Notes
------------------
- Server needs to be able to run both in "the cloud" (Typically Amazon Web Services) 
and on a local server because there may not be internet connectivity during the 
convention.
  - This means that timezones need to be handled property, but support for timezones
  other than Pacific has so far been unnecessary
  - Due to the printing flow, printing directly from the server isn't really possible
  when running on a cloud server. This is fine, since AWS is used only for 
  training/test data.
- Some of the computers at con are very low resolution, try to make screens look
  good when running below 1300x700 pixels.
- Working with mobile devices is nice, since during training many people use
  tablets or phones.
- This service is for attendee management only, staff check ins is handled by a 
  different service.
