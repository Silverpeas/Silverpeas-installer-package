@echo off

cd "%JBOSS_HOME%/bin"

call shutdown.bat -S

if ERRORLEVEL 1 pause
