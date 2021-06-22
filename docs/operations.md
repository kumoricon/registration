Operational Notes

*Assumes Ubuntu 20.04*

- System users must be a member of the group lpadmin to manage cups: 

  ```
  sudo usermod -a -G lpadmin <username>
  ```

- And the user registration to copy files in to the queues

  ```
  sudo usermod -A -G registration <username>
  ```