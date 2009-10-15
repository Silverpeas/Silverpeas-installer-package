@echo off
java  -classpath "${classpath}" -Dsilverpeas.home="%SILVERPEAS_HOME%" com.silverpeas.applicationbuilder.ApplicationBuilder -r ext_repository %1
pause
