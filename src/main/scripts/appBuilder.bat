@echo off

set CLASSPATH=%SILVERPEAS_HOME%\bin\jar\*

java  -classpath "%CLASSPATH%" -Dsilverpeas.home="%SILVERPEAS_HOME%" org.silverpeas.applicationbuilder.ApplicationBuilder -r ext_repository %1
pause
