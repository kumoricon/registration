# Installation Notes

On Ubuntu 20.04 (or newer)

## Server setup
### Create users
```shell
ssh <reg server>
sudo adduser jason
sudo usermod -aG sudo jason
sudo usermod -aG adm jason
# Repeat for other admins
exit
```

## Service Install
### Install Registration Services
```shell
ssh <reg server>
sudo apt -y update
sudo apt -y upgrade
sudo shutdown -r now # restart, wait a minute
ssh <reg server>
sudo apt intall -y ./registration_X_Y_Z_all.deb
sudo usermod -aG registration jason # repeat for other admins
```


### Create PostgresQL database users for registration and training
If running Postgres on the local server, install it and create the database as a user with access to create users in PostgreSQL (typicaly the `postgres` user),
run `/opt/registration/bin/createdb.sh`.

```shell
sudo apt install -y postgresql postgresql-contrib
sudo usermod -aG postgres jason
sudo su postgres /opt/registration/bin/createdb.sh
```

Otherwise, create the user `registration` and database `registration`.

### Edit Configuration Files
Update the database URL, username and password in `/opt/registration/registration.properties`

### Install Badge Resources
Copy badge resource files to `/opt/registration/data/badgeResource`
Copy `in-line-registration-private.pem` to `/opt/registration/in-line-registration-private.pem`

```shell
# From local machine:
scp -r badgeResource/ reg:/opt/registration/data/badgeResource
scp in-line-registration-private.pem reg:
# From server:
sudo mv ~/in-line-registration-private.pem /opt/registration/
```

### Start the Services
```shell
sudo systemctl start registration
```

### Setup CUPS (Print service)
Allow CUPS to listen on all addresses. Run:
```shell
sudo cupsctl --remote-admin --remote-any --share-printers
```
Add your user (and any other admins) to the `lpadmin` group to allow CUPS admin access:

```shell
sudo usermod -aG lpadmin jason # repeat for other admins
```

### Setup cron tasks
Log out and back in (so that changed groups take effect), then set up
registration-filetransfer from https://github.com/kumoricon/registration-filetransfer/



### Log In
- Browse to http://hostname:8080/
- Printer administration: https://hostname:631/

# Utilities
Installed to `/opt/registration/bin/`

- backup.sh: Does a database dump and tars the server's `data` directory in to the timestamped files in the local directory
- createdb.sh: Runs PostgreSQL commands to create the two databases.
