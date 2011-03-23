@echo off

set SETUP_ROOT=%SILVERPEAS_HOME%/setup/settings

java -classpath "${classpath}" -Dsilverpeas.home="%SILVERPEAS_HOME%" com.silverpeas.SilverpeasSettings.SilverpeasSettings
pause