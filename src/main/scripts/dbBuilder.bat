@echo off

set VERBOSEPARAM=-v
set ACTIONPARAM=-I

set SRV_SERVERTYPE=postgres
set SILVERPEAS_DATA=%SILVERPEAS_HOME%
set SILVERPEAS_PROPERTIES=%SILVERPEAS_HOME%/properties

set LINEARGS=-T %SRV_SERVERTYPE% %ACTIONPARAM% %VERBOSEPARAM%

set LIB_ROOT=%SILVERPEAS_HOME%/bin/jar

set CLASSPATH=%SILVERPEAS_HOME%\bin\jar\*

java -classpath "%CLASSPATH%" -Dsilverpeas.home="%SILVERPEAS_HOME%" -Ddbbuilder.home="%SILVERPEAS_HOME%" -Ddbbuilder.data="%SILVERPEAS_DATA%" org.silverpeas.dbbuilder.DBBuilder %LINEARGS%

echo.

pause
