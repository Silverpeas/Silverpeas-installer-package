@echo off

cd "%JBOSS_HOME%\server\default\deploy"

del silverpeas.ear.bak*
del silverpeas-ds.xml.bak*

cd "%JBOSS_HOME%\server\default\deploy\jms"

del silverpeas-destinations-service.xml.bak*

cd "%JBOSS_HOME%/bin"

rem Add Silverpeas Properties root repository to path
SET JBOSS_CLASSPATH=%SILVERPEAS_HOME%/properties

call run.bat

if ERRORLEVEL 1 pause
