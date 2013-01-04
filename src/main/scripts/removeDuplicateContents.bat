@echo off

set CLASSPATH=%SILVERPEAS_HOME%\bin\jar\*

java -classpath "%CLASSPATH%" -Ddbbuilder.home="%SILVERPEAS_HOME%" org.silverpeas.migration.contentmanagement.DuplicateContentRemovingApplication

echo.

pause
