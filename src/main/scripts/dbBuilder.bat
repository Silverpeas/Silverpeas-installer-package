@echo off

set SRV_SERVERTYPE=POSTGRES
set SRV_SERVERNAME=localhost
set SRV_LOGINNAME=postgres
set SRV_LOGINPWD=postgres
set SRV_DBNAME=SilverpeasV5
set DRIVERPARAM=org.postgresql.Driver
set DBURLPARAM="jdbc:postgresql://localhost:5432/SilverpeasV5"

set VERBOSEPARAM=-v
set ACTIONPARAM=-I

set SILVERPEAS_DATA=%SILVERPEAS_HOME%
set SILVERPEAS_PROPERTIES=%SILVERPEAS_HOME%\properties

set LINEARGS=-T %SRV_SERVERTYPE% -D %DRIVERPARAM% -d %DBURLPARAM% -l %SRV_LOGINNAME% -p "%SRV_LOGINPWD%" %ACTIONPARAM% %VERBOSEPARAM%

set LIB_ROOT=%SILVERPEAS_HOME%\bin\jar

set CLASSPATH=${classpath};%SILVERPEAS_PROPERTIES%

java -classpath "%CLASSPATH%" -Ddbbuilder.home="%SILVERPEAS_HOME%" -Ddbbuilder.data="%SILVERPEAS_DATA%" com.silverpeas.dbbuilder.DBBuilder %LINEARGS%

echo.

pause
