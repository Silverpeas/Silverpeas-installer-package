@echo off

cd "%JBOSS_HOME%/bin"

set username=admin
set password=admin

call shutdown.bat -u %username% -p %password% -S

if ERRORLEVEL 1 pause
