#!/bin/sh
# ------ silverpeas_stop_jboss.sh -----------
cd $JBOSS_HOME/bin

username=admin
password=admin

sh shutdown.sh -u $username -p $password -S
