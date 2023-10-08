How to build a .DEB file for server installation
------------------------------------------------

The POM extension JDEB is used to create a .DEB file.
Maven will need to be installed to proceed.

# In Visual Studio
  # Navigate to MAVEN
  # Expand Lifecycle
  # Run each lifecycle step up to 'verify'
  # Navigate to the 'target' folder at the root of the repository
  # The .DEB file should be in that folder
  # The .JAR file is also there, if needed
 
All the server stuff installs to /opt/registration


Installing the .DEB file on the server
--------------------------------------

# Run 'chmod 777 {filename}' to allow it to be executed
# Install with 'sudo apt install -y ./registration_X_Y_Z_all.deb'

If reinstalling a DEB file over an older version, restart 'registration.service' when prompted after install