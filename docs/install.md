# Installation Notes

On Ubuntu 20.04 (or newer)

### Install Registration Services
```
sudo apt intall ./registration_X_Y_Z_all.deb
```

### Create PostgreSQL database users for registration and training
If running Postgres on the local server, install it and create the database as a user with access to create users in PostgreSQL (typicaly the `postgres` user),
run `/opt/registration/bin/createdb.sh`.

```
sudo apt install postgresql postgresql-contrib
sudo su postgres
/opt/registration/bin/createdb.sh
```

Otherwise, create the user `registration` and database `registration`.

### Edit Configuration Files
Update the database URL, username and password in `/opt/registration/registration.properties`

### Install Badge Resources
Copy badge resource files to `/opt/registration/badgeResource`

### Start the Services
```
sudo systemctl start registration
```

### Log In
Browse to http://hostname:8080/


# Utilities
Installed to `/opt/registration/bin/`

- backup.sh: Does a database dump and tars the server's `data` directory in to the timestamped files in the local directory
 (but does not include the `data` direcotory for the training instance of the service)
- createdb.sh: Runs PostgreSQL commands to create the two databases.
