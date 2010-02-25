#!/bin/bash
# ------ dbBuilder -----------

#test d'existance de la variable SILVERPEAS_HOME
if [ "x${SILVERPEAS_HOME}" = "x" -o "${SILVERPEAS_HOME}" = "" ];then
	echo La variable SILVERPEAS_HOME doit etre initialisee
exit 1
fi

VERBOSEPARAM=-v
ACTIONPARAM=-I

SILVERPEAS_DATA=$SILVERPEAS_HOME
SILVERPEAS_PROPERTIES=$SILVERPEAS_HOME/properties

LINEARGS="-T $SRV_SERVERTYPE $ACTIONPARAM $VERBOSEPARAM"

CLASSPATH=${classpath}
export CLASSPATH

echo $CLASSPATH

exec $JAVA_HOME/bin/java -classpath $CLASSPATH -Ddbbuilder.home=$SILVERPEAS_HOME -Ddbbuilder.data=$SILVERPEAS_DATA com.silverpeas.dbbuilder.DBBuilder $LINEARGS
