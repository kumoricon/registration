Kumoricon Registration
----------------------
Instructions for setting up a local development environment

Technologies
------------
The service is built on:
  - [Java 17](https://openjdk.java.net/projects/jdk/17/)
  - [Spring Framework](https://docs.spring.io/spring/docs/5.1.3.RELEASE/spring-framework-reference/) Server framework
  - [Thymeleaf](https://www.thymeleaf.org/doc/tutorials/2.1/thymeleafspring.html) Templating language
  - [CSS Bootstrap](https://getbootstrap.com/docs/3.4/css/) Front end formatting
  - [PostgreSQL](https://www.postgresql.org/) Database (developed on 10.7)
  - [Maven](https://maven.apache.org/index.html) Build/dependency management


Setup
-----
**1. Install PostgreSQL Server (commands vary)**

**MacOS Ventura:**
```
brew tap homebrew/core
$ brew install postgresql
```

**Ubuntu Linux 20.04:**
```
$ sudo apt -y install postgresql postgresql-contrib
$ sudo -i -u postgres

```

**2. Start and enable the PostgreSQL service:**
```
Linux:
$ sudo systemctl enable postgresql              # Enable service
$ sudo systemctl start postgresql               # Start service

MacOS:
$ brew services start postgresql@14             # Start service
```


**3. Create a PostgreSQL user and database**

The User name is "kumoreg" and the database name is "registration"
Note: for Ubuntu installs, skip the "su" command below
Note: the createdb parameter -O is the letter "oh" and not a "zero"
```
MacOS:
$ sudo dscl . -create /Users/postgres

MacOS/Linux:
$ su - postgres   # Not needed on MacOS
$ createuser --interactive -P registration
    Enter password for new role:    #Set this to: 'password' without quotes (unless you want to change the defaults in the file 'default.properties')
    Enter it again:
    Shall the new role be a superuser? (y/n) n
    Shall the new role be allowed to create databases? (y/n) n
    Shall the new role be allowed to create more new roles? (y/n) n

$ createdb -O registration registration
```

**4. Enable case-insensitive searching**

By default, Postgres does not include case-insensitive searching. The commands below add the
citext extension, which will enable that. Note that columns have to have the `citext` type.

```
$ psql registration -c "CREATE EXTENSION citext;"
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


Build and Run the Server with Maven (IntelliJ IDEA):
------------------------------------
```
mvn spring-boot:run
```
# For VS Code:
  # Install 'Extension Pack for Java'
  # Install a JDK, can be whatever you are prompted to install by the extension
  # Restart VS Code and JAVA PROJECTS should auto-scan to look for projects
  # Once that's done, expand JAVA PROJECTS > registration and select Run in the context menu

Acessing the Site in a Browser:
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
