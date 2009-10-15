#!/bin/sh
# ------ silverpeas_start_jboss.sh -----------
cd $JBOSS_HOME/bin

# Add Silverpeas Properties root repository to path
JBOSS_CLASSPATH=$SILVERPEAS_HOME/properties
export JBOSS_CLASSPATH

sh run.sh &