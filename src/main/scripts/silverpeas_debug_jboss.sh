#!/bin/sh
# ------ silverpeas_start_jboss.sh -----------
cd $JBOSS_HOME/bin

export JAVA_OPTS=-Xdebug -Xnoagent -Xrunjdwp:transport=dt_socket,address=5000,suspend=n,server=y

# Add Silverpeas Properties root repository to path
JBOSS_CLASSPATH=$SILVERPEAS_HOME/properties
export JBOSS_CLASSPATH

sh run.sh &