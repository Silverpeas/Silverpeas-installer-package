#!/bin/bash
# ------ dbBuilder -----------

#test d'existance de la variable SILVERPEAS_HOME
if [ "x${SILVERPEAS_HOME}" = "x" -o "${SILVERPEAS_HOME}" = "" ];then
	echo La variable SILVERPEAS_HOME doit etre initialisee
exit 1
fi

SRV_SERVERTYPE=POSTGRES
SRV_SERVERNAME=localhost
SRV_LOGINNAME=postgres
SRV_LOGINPWD=postgres
SRV_DBNAME=SilverpeasV5
DRIVERPARAM=org.postgresql.Driver
DBURLPARAM="jdbc:postgresql://localhost:5432/SilverpeasV5"

VERBOSEPARAM=-v
ACTIONPARAM=-I

SILVERPEAS_DATA=$SILVERPEAS_HOME
SILVERPEAS_PROPERTIES=$SILVERPEAS_HOME/properties

LINEARGS="-T $SRV_SERVERTYPE -D $DRIVERPARAM -d $DBURLPARAM -l $SRV_LOGINNAME -p "$SRV_LOGINPWD" $ACTIONPARAM $VERBOSEPARAM"

CLASSPATH=${classpath}:$SILVERPEAS_PROPERTIES
export CLASSPATH

echo $CLASSPATH

exec $JAVA_HOME/bin/java -classpath $CLASSPATH -Ddbbuilder.home=$SILVERPEAS_HOME -Ddbbuilder.data=$SILVERPEAS_DATA com.silverpeas.dbbuilder.DBBuilder $LINEARGS
