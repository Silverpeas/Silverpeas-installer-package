#!/bin/sh
# -------- Active Settings ---------

#test d'existance de la variable SILVERPEAS_HOME
if [ "x${SILVERPEAS_HOME}" = "x" -o "${SILVERPEAS_HOME}" = "" ];then
  echo La variable SILVERPEAS_HOME doit etre initialisee
  exit 1
fi

SETUP_ROOT=$SILVERPEAS_HOME/setup/settings

JARDIR=$SILVERPEAS_HOME/bin/jar
for jarlib in `ls $JARDIR`; do
  CLASSPATH=$CLASSPATH:$JARDIR/$jarlib
done

export CLASSPATH

exec $JAVA_HOME/bin/java -classpath $CLASSPATH -Dsilverpeas.home=$SILVERPEAS_HOME org.silverpeas.SilverpeasSettings.SilverpeasSettings
