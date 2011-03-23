@echo off

set VERBOSEPARAM=-v
set ACTIONPARAM=-I

set SRV_SERVERTYPE=postgres
set SILVERPEAS_DATA=%SILVERPEAS_HOME%
set SILVERPEAS_PROPERTIES=%SILVERPEAS_HOME%/properties

set LINEARGS=-T %SRV_SERVERTYPE% %ACTIONPARAM% %VERBOSEPARAM%

set LIB_ROOT=%SILVERPEAS_HOME%/bin/jar

set CLASSPATH=${classpath}

java -classpath "%CLASSPATH%" -Ddbbuilder.home="%SILVERPEAS_HOME%" -Ddbbuilder.data="%SILVERPEAS_DATA%" com.silverpeas.dbbuilder.DBBuilder %LINEARGS%

echo.

pause