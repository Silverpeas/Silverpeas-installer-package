@echo off

cd "%JBOSS_HOME%\server\default\deploy\silverpeas"
del silverpeas.ear.bak*
del silverpeas-ds.xml.bak*
del silverpeas-hornetq-jms.xml.bak*

cd "%JBOSS_HOME%/bin"
rem Add Silverpeas Properties root repository to path
rem SET JBOSS_CLASSPATH=%SILVERPEAS_HOME%/properties
SET JAVA_OPTS=-server -Xms512m -Xmx768m -XX:MaxPermSize=256m -Dorg.jboss.logging.Log4jService.catchSystemOut=false -Xdebug -Xnoagent -Xrunjdwp:transport=dt_socket,address=5000,suspend=n,server=y

SET PROFILE=default
call run.bat -c %PROFILE%

if ERRORLEVEL 1 pause
