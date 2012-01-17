#!/bin/sh
# ------ silverpeas_stop_jboss.sh -----------
cd $JBOSS_HOME/bin

username=
password=

sh shutdown.sh -u $username -p $password -S