@echo off

set SETUP_ROOT=%SILVERPEAS_HOME%/setup/settings

java -classpath "${classpath}" -Dsilverpeas.home="%SILVERPEAS_HOME%" org.silverpeas.SilverpeasSettings.SilverpeasSettings
pause