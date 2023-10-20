Kumoricon Registration
----------------------
Instructions for setting up a local development environment

Technologies
------------
The service is built on:
  - [Java 17](https://jdk.java.net/java-se-ri/17)
  - [Spring Framework](https://docs.spring.io/spring/docs/5.1.3.RELEASE/spring-framework-reference/) Server framework
  - [Thymeleaf](https://www.thymeleaf.org/doc/tutorials/2.1/thymeleafspring.html) Templating language
  - [CSS Bootstrap](https://getbootstrap.com/docs/3.4/css/) Front end formatting
  - [PostgreSQL](https://www.postgresql.org/) Database (developed on 10.7)
  - [Maven](https://maven.apache.org/index.html) Build/dependency management


Setup
-----
**1. Install PostgreSQL Server (commands vary)**

MacOS Ventura:
```
brew tap homebrew/core
$ brew install postgresql
```

Ubuntu Linux 22.04.02:
```
$ sudo apt -y install postgresql postgresql-contrib
$ sudo su - postgres
```
Alternatively, instead of `sudo su - postgres`:
```
$ sudo usermod -aG postgres myusername   # your local username with superuser privileges

# There is a postgres user account that gets created on Ubuntu automatically;
# However, there is no way to enter a password for that special account
# To run commands with the postgres account on Ubuntu 22.04.02, use: 
# These instructions will use the above methods
# The other option is to use sudo for every command below from an account
# that you have the password for

# TROUBLESHOOTING: if you get prompted for the postgres user password,
# then you won't be able to proceed. You can close the terminal and open it again
# so that you're running as your account that has superuser privileges, and then
# you can try running the same command with sudo, which should prompt for your
# user account's password rather than postgres'
```


**2. Create a PostgreSQL user and database**

The User name is "kumoreg" and the database name is "registration"
```
MacOS:
$ sudo dscl . -create /Users/postgres

MacOS/Linux:
$ createuser --interactive -P registration
    Enter password for new role:    #Set this to: 'password' without quotes (unless you want to change the defaults in the file 'default.properties')
    Enter it again:
    Shall the new role be a superuser? (y/n) n
    Shall the new role be allowed to create databases? (y/n) n
    Shall the new role be allowed to create more new roles? (y/n) n

$ createdb -O registration registration   # -O is "oh" not "zero"
```


**3. Start and enable the PostgreSQL service:**
```
Linux:
$ systemctl enable postgresql    # Enable service
$ systemctl start postgresql     # Start service

MacOS:
$ brew services start postgresql@14             # Start service
```


**4. Enable case-insensitive searching**

By default, Postgres does not include case-insensitive searching. The commands below add the
citext extension, which will enable that. Note that columns have to have the `citext` type.

```
$ psql registration -c "CREATE EXTENSION citext;"
```


**5. Install JDK**

```
$ sudo apt-get install openjdk-17-jre
```

**6. Install Maven**

```
$ sudo apt install maven
```


Handy Postgres Commands
-----------------------
```
# Connect with command line SQL client:
psql -h 127.0.0.1 -U kumoreg registration
```
- \d - List tables
- \d <tablename> - Describe a table
- \l - List databases
- \q - Quit


Build and Run the Server:
------------------------------------
**Maven**
```
mvn spring-boot:run
```
**IDE-specific**

<ins>VSCode:</ins>
  - Install `Extension Pack for Java`
  - Install a JDK, version 17 (see step 5)
  - Restart VS Code and JAVA PROJECTS should auto-scan to look for projects
  - Once that's done, expand JAVA PROJECTS > registration and select Run in the context menu

Accessing the Site in a Browser:
------------------------------------
```
http://127.0.0.1:8080
```
Log in with the user 'admin' and password 'test'

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
