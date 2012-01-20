@echo off
java  -classpath "${classpath}" -Dsilverpeas.home="%SILVERPEAS_HOME%" org.silverpeas.applicationbuilder.ApplicationBuilder -r ext_repository %1
pause
