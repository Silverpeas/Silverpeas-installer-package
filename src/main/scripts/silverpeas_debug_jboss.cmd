@echo off

cd "%JBOSS_HOME%\server\default\deploy\silverpeas"
del silverpeas.ear.bak*
del silverpeas-ds.xml.bak*
del silverpeas-hornetq-jms.xml.bak*

cd "%JBOSS_HOME%/bin"

set HEAP_SIZE=
if defined HEAP_SIZE (
  set "HEAP_MAX_SIZE=-Xmx%HEAP_SIZE%m"
  set "HEAP_MIN_SIZE=-Xms%HEAP_SIZE%m"
)

rem Add Silverpeas Properties root repository to path
rem SET JBOSS_CLASSPATH=%SILVERPEAS_HOME%/properties

SET ADDITIONAL_JAVA_OPTS=
SET JAVA_OPTS=-server %HEAP_MIN_SIZE% %HEAP_MAX_SIZE% -XX:MaxPermSize=256m -Dorg.jboss.logging.Log4jService.catchSystemOut=false -Dsun.rmi.dgc.client.gcInterval=3600000 -Dsun.rmi.dgc.server.gcInterval=3600000 -Xdebug -Xnoagent -Xrunjdwp:transport=dt_socket,address=5000,suspend=n,server=y %ADDITIONAL_JAVA_OPTS%

SET PROFILE=default
call run.bat -b 0.0.0.0 -c %PROFILE% 

if ERRORLEVEL 1 pause
