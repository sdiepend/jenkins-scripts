# jenkins-scripts
Scripts used for monitoring and managing jenkins setups.

#### checkExecutors.groovy 
Script used to monitor the number of idle executors in your jenkins setup.

#### checkXvncDisplayNumbers.groovy
Script used to monitor the number of free display numbers when using the Xvnc plugin.

#### cleanXvncDisplayNumbers.groovy
Clean up the allocated display numbers from a running jenkins master. Be aware, also display numbers for running jobs will be removed from the allocated list.
