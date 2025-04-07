@ECHO OFF

REM (c) Copyright IBM Corporation 2025
REM Licensed under the Apache License, Version 2.0 (the "License");
REM You may not use this file except in compliance with the License.
REM You may obtain a copy of the License at
REM http://www.apache.org/licenses/LICENSE-2.0

SETLOCAL ENABLEEXTENSIONS
SET PATH=%~dps0;%PATH%

REM Version Control
SET jakartaClientVer=9.4.2.0
SET jakartaApiVer=3.1.0
SET jsonVer=20250107

SET jakartaClientJar=com.ibm.mq.jakarta.client-%jakartaClientVer%.jar
SET jakartaApiJar=jakarta.jms-api-%jakartaApiVer%.jar
SET jsonJar=json-%jsonVer%.jar
SET JakartaAppClass=JakartaPutGetInteractive
SET JakartaAppSrc=%JakartaAppClass%.java
SET ec=###
SET fileExistsMsg=%ec% File exists, skipping.

ECHO %ec% Checking JDK (javac command) is available.
javac -version >NUL 2>&1
IF %ERRORLEVEL% EQU 0 (ECHO OK.) ELSE (ECHO %ec% Java Compiler not installed! & EXIT /B %ERRORLEVEL%)

ECHO %ec% Making MQClient directory.
IF NOT EXIST MQClient (md MQClient) ELSE (ECHO %fileExistsMsg%)
IF %ERRORLEVEL% GTR 1 (ECHO %ec% Error making MQClient directory. & EXIT /B %ERRORLEVEL%) ELSE (ECHO OK.)

ECHO %ec% Changing to MQClient directory.
cd MQClient
IF %ERRORLEVEL% NEQ 0 (ECHO %ec% Error changing to MQClient directory. & EXIT /B %ERRORLEVEL%) ELSE (ECHO OK.)

ECHO %ec% Fetching IBM MQ jakarta client Jar [%jakartaClientJar%].
IF NOT EXIST %jakartaClientJar% (curl -o %jakartaClientJar% https://repo1.maven.org/maven2/com/ibm/mq/com.ibm.mq.jakarta.client/%jakartaClientVer%/%jakartaClientJar%) ELSE (ECHO %fileExistsMsg%)  
IF %ERRORLEVEL% GTR 1 (ECHO Error fetching IBM MQ jakarta client Jar. & EXIT /B %ERRORLEVEL%) ELSE (ECHO OK.)

ECHO %ec% Fetching JMS API Jar [%jakartaApiJar%].
IF NOT EXIST %jakartaApiJar% (curl -o %jakartaApiJar% https://repo1.maven.org/maven2/jakarta/jms/jakarta.jms-api/%jakartaApiVer%/%jakartaApiJar%) ELSE (ECHO %fileExistsMsg%)
IF %ERRORLEVEL% GTR 1 (Error fetching JMS API Jar. & EXIT /B %ERRORLEVEL%) ELSE (ECHO OK.)

ECHO %ec% Fetching json Jar [%jsonJar%].
IF NOT EXIST %jsonJar% (curl -o %jsonJar% https://repo1.maven.org/maven2/org/json/json/%jsonVer%/%jsonJar%) ELSE (ECHO %fileExistsMsg%)
IF %ERRORLEVEL% GTR 1 (Error fetching json Jar. & EXIT /B %ERRORLEVEL%) ELSE (ECHO OK.)

ECHO %ec% Making Java directory structure for JakartaPutGetInteractive Utility.
IF NOT EXIST com\ibm\mq\samples\jakarta (md com\ibm\mq\samples\jakarta) ELSE (ECHO %fileExistsMsg%)
IF %ERRORLEVEL% GTR 1 (Error making Java directory structure. & EXIT /B %ERRORLEVEL%) ELSE (ECHO OK.)

ECHO %ec% Changing to com\ibm\mq\samples\jakarta directory.
cd com\ibm\mq\samples\jakarta
IF %ERRORLEVEL% NEQ 0 (Error changing to com\ibm\mq\samples\jakarta directory. & EXIT /B %ERRORLEVEL%) ELSE (ECHO OK.)

ECHO %ec% Fetching JakartaPutGetInteractive Utility.
IF NOT EXIST %jakartaAppSrc% (curl -o %jakartaAppSrc% https://raw.githubusercontent.com/Anitha-KJ/mq-dev-samples/refs/heads/anitha_jakarta_updates/gettingStarted/jms/com/ibm/mq/samples/jakarta/%JakartaAppSrc%) ELSE (ECHO %fileExistsMsg%)
IF %ERRORLEVEL% GTR 1 (Error fetching JakartaPutGetInteractive Utility. & EXIT /B %ERRORLEVEL%) ELSE (ECHO OK.)

ECHO %ec% Changing to MQClient directory.
cd ..\..\..\..\..
IF %ERRORLEVEL% NEQ 0 (ECHO Error changing to MQClient directory. & EXIT /B %ERRORLEVEL%) ELSE (ECHO OK.)

ECHO %ec% Compiling JakartaPutGetInteractive Utility application source.
ECHO %ec% Commands to run are:
ECHO: 
FOR /F %%a IN ('cd') DO ECHO cd %%a
ECHO javac -cp .\%jakartaClientJar%;.\%jakartaApiJar%;.\%jsonJar%;. com\ibm\mq\samples\jakarta\%JakartaAppSrc%
ECHO:
javac -cp .\%jakartaClientJar%;.\%jakartaApiJar%;.\%jsonJar%;. com\ibm\mq\samples\jakarta\%JakartaAppSrc%
IF %ERRORLEVEL% GTR 0 (ECHO Error compiling JakartaPutGetInteractive Utility. & EXIT /B %ERRORLEVEL%) ELSE (ECHO OK.)

CALL :confirmNextStep "Run JMS Utility application?"
IF %ERRORLEVEL% GTR 0 (ECHO Exiting... & EXIT /B %ERRORLEVEL%) ELSE (ECHO OK.)

set /p host_name=Enter Hostname: 
IF DEFINED host_name (SET host_name=-host %host_name% )

set /p port=Enter Port: 
IF DEFINED port (SET port=-p %port% )

set /p channel=Enter Channel: 
IF DEFINED channel (SET channel=-c %channel% )

set /p qmgr=Enter Queue Manager Name: 
IF DEFINED qmgr (SET qmgr=-qm %qmgr% )

set /p app_user=Enter App User: 
IF DEFINED app_user (SET app_user=-u %app_user% )

ECHO Enter App Password: 
WHERE Powershell >NUL 2>&1
IF %ERRORLEVEL% EQU 0 (for /f %%i in ('Powershell -Command "$enc = Read-Host -AsSecureString; $stringValue = [Runtime.InteropServices.Marshal]::PtrToStringAuto([Runtime.InteropServices.Marshal]::SecureStringToBSTR($enc));echo $stringValue"') DO set app_pwd=%%i) ELSE (ECHO %ec% !!WARNING!! Password will be displayed in clear text. See %jakartaAppSrc% or -pw options for workshop. & set /p app_pwd=)

IF DEFINED app_pwd (SET app_pwd=-pw %app_pwd% )

set /p queue=Enter Queue Name: 
IF DEFINED queue (SET queue=-q %queue% )

CHOICE /c yn /m "Use TLS?"
IF %ERRORLEVEL% EQU 1 (SET TLS=-t ) ELSE (SET TLS=)

CHOICE /c pgb /m "Mode: Put, Get or Both?"
IF %ERRORLEVEL% EQU 1 (SET mode=-put )
IF %ERRORLEVEL% EQU 2 (SET mode=-get )
IF %ERRORLEVEL% EQU 3 (SET mode=)

ECHO %ec% Running...
ECHO %ec% Commands to run are:
ECHO:
ECHO java -cp .\%jakartaClientJar%;.\%jakartaApiJar%;.\%jsonJar%;. com.ibm.mq.samples.jakarta.%JakartaAppClass% %host_name%%port%%channel%%qmgr%%app_user%-pw _your_password_ %queue%%mode%%TLS%
ECHO:

java -cp .\%jakartaClientJar%;.\%jakartaApiJar%;.\%jsonJar%;. com.ibm.mq.samples.jakarta.%JakartaAppClass% %host_name% %port% %channel% %qmgr% %app_user% %app_pwd% %queue% %mode% %TLS%
IF %ERRORLEVEL% NEQ 0 (ECHO Error running JakartaPutGetInteractive Utility.)

ECHO %ec% Done!
EXIT /B 0

:confirmNextStep
CHOICE /c yn /m "%~1"
IF %ERRORLEVEL% EQU 1 (EXIT /B 0)
IF %ERRORLEVEL% EQU 2 (EXIT /B 1)
