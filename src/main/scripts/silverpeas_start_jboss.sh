#!/bin/sh
# ------ silverpeas_start_jboss.sh -----------
cd $JBOSS_HOME/bin

HEAP_SIZE=
if [ "Z${HEAP_SIZE}" != "Z" ]; then
  HEAP_MAX_SIZE=-Xmx${HEAP_SIZE}m
  HEAP_MIN_SIZE=-Xms${HEAP_SIZE}m
fi

ADDITIONAL_JAVA_OPTS=
export JAVA_OPTS="-server $HEAP_MAX_SIZE $HEAP_MIN_SIZE -XX:MaxPermSize=512m -Dorg.jboss.logging.Log4jService.catchSystemOut=false -Dsun.rmi.dgc.client.gcInterval=3600000 -Dsun.rmi.dgc.server.gcInterval=3600000 $ADDITIONAL_JAVA_OPTS"
# Add Silverpeas Properties root repository to path
#JBOSS_CLASSPATH=$SILVERPEAS_HOME/properties
#export JBOSS_CLASSPATH

PROFILE=default
sh run.sh -b 0.0.0.0 -c $PROFILE &
