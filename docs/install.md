# Installation Notes

On Ubuntu 20.04 (or newer)

### Install Registration Services
```
sudo apt intall ./registration_X_Y_Z_all.deb
```

### Create Postgres database users for registration and training
If running Postgres on the local server, as a user with access to create users in Postgres (typicaly the `postgres` user),
run `/opt/registration/bin/createdb.sh`.

```
$ sudo su postgres
$ /opt/registration/bin/createdb.sh
```

Otherwise, create users `registration` and `registrationtraining` in your database.

### Edit Configuration Files
Update the database URL, username and password in `/opt/registration/application.properties`
and `/opt/registration/application-training.properties`

### Install Badge Resources
Copy badge resource files to `/opt/registration/badgeResource`

### Start the Services
```
sudo systemctl start registration
sudo systemctl start registration-training
```

### Log In
Browse to http://hostname:8080/


# Utilities
Installed to `/opt/registration/bin/`

- backup.sh: Does a database dump and tars the server's `data` directory in to the timestamped files in the local directory
 (but does not include the `data` direcotory for the training instance of the service)
- createdb.sh: Runs Postgres commands to create the two databases.
