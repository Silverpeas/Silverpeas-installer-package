#!/bin/bash
# ------ dbBuilder -----------

#test d'existance de la variable SILVERPEAS_HOME
if [ "x${SILVERPEAS_HOME}" = "x" -o "${SILVERPEAS_HOME}" = "" ];then
echo La variable SILVERPEAS_HOME doit etre initialisee
exit 1
fi

CLASSPATH=${classpath}
export CLASSPATH

echo $CLASSPATH

exec $JAVA_HOME/bin/java -classpath $CLASSPATH -Ddbbuilder.home=$SILVERPEAS_HOME com.silverpeas.migration.contentmanagement.DuplicateContentRemovingApplication 
