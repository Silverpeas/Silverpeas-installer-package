@echo off

set SETUP_ROOT=%SILVERPEAS_HOME%/setup/settings
set CLASSPATH=%SILVERPEAS_HOME%\bin\jar\*

java -classpath "%CLASSPATH%" -Dsilverpeas.home="%SILVERPEAS_HOME%" org.silverpeas.SilverpeasSettings.SilverpeasSettings
pause
