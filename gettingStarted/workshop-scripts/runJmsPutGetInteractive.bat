@ECHO OFF

REM (c) Copyright IBM Corporation 2025
REM
REM Licensed under the Apache License, Version 2.0 (the "License");
REM you may not use this file except in compliance with the License.
REM You may obtain a copy of the License at
REM
REM http://www.apache.org/licenses/LICENSE-2.0
REM
REM Unless required by applicable law or agreed to in writing, software
REM distributed under the License is distributed on an "AS IS" BASIS,
REM WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
REM See the License for the specific language governing permissions and
REM limitations under the License.

SETLOCAL ENABLEEXTENSIONS
SET PATH=%~dps0;%PATH%

REM Version Control
SET allClientVer=9.4.2.0
SET jmsApiVer=2.0.1
SET jsonVer=20220320

SET allClientJar=com.ibm.mq.allclient-%allClientVer%.jar
SET jmsApiJar=javax.jms-api-%jmsApiVer%.jar
SET jsonJar=json-%jsonVer%.jar
SET JmsAppClass=JmsPutGetInteractive
SET JmsAppSrc=%JmsAppClass%.java
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

ECHO %ec% Fetching IBM MQ all client Jar [%allClientJar%].
IF NOT EXIST %allClientJar% (curl -o %allClientJar% https://repo1.maven.org/maven2/com/ibm/mq/com.ibm.mq.allclient/%allClientVer%/%allClientJar%) ELSE (ECHO %fileExistsMsg%)  
IF %ERRORLEVEL% GTR 1 (ECHO Error fetching IBM MQ all client Jar. & EXIT /B %ERRORLEVEL%) ELSE (ECHO OK.)

ECHO %ec% Fetching JMS API Jar [%jmsApiJar%].
IF NOT EXIST %jmsApiJar% (curl -o %jmsApiJar% https://repo1.maven.org/maven2/javax/jms/javax.jms-api/%jmsApiVer%/%jmsApiJar%) ELSE (ECHO %fileExistsMsg%)
IF %ERRORLEVEL% GTR 1 (Error fetching JMS API Jar. & EXIT /B %ERRORLEVEL%) ELSE (ECHO OK.)

ECHO %ec% Fethcing json Jar [%jsonJar%].
 IF NOT EXIST %jsonJar% (curl -o %jsonJar% https://repo1.maven.org/maven2/org/json/json/%jsonVer%/%jsonJar%) ELSE (ECHO %fileExistsMsg%)
 IF %ERRORLEVEL% GTR 1 (Error fethcing json Jar. & EXIT /B %ERRORLEVEL%) ELSE (ECHO OK.)

ECHO %ec% Making Java directory sructure for JmsPutGetInteractive Utility.
IF NOT EXIST com\ibm\mq\samples\jms (md com\ibm\mq\samples\jms) ELSE (ECHO %fileExistsMsg%)
IF %ERRORLEVEL% GTR 1 (Error making Java directory sructure for JmsPutGetInteractive Utility. & EXIT /B %ERRORLEVEL%) ELSE (ECHO OK.)

ECHO %ec% Changing to com\ibm\mq\samples\jms directory.
cd com\ibm\mq\samples\jms
IF %ERRORLEVEL% NEQ 0 (Error changing to com/ibm/mq/samples/jms directory. & EXIT /B %ERRORLEVEL%) ELSE (ECHO OK.)

ECHO %ec% Fethcing JmsPutGetInteractive Utility.
IF NOT EXIST %jmsAppSrc% (curl -o %jmsAppSrc% https://raw.githubusercontent.com/ibm-messaging/mq-dev-samples/master/gettingStarted/jms/com/ibm/mq/samples/jms/%JmsAppSrc%) ELSE (ECHO %fileExistsMsg%)
IF %ERRORLEVEL% GTR 1 (Error fetching JmsPutGetInteractive Utility. & EXIT /B %ERRORLEVEL%) ELSE (ECHO OK.)

ECHO %ec% Changing to MQClient directory.
cd ..\..\..\..\..
IF %ERRORLEVEL% NEQ 0 (ECHO Error changing to MQClient directory. & EXIT /B %ERRORLEVEL%) ELSE (ECHO OK.)

CALL :confirmNextStep "Compile JmsPutGetInteractive Utility?"
IF %ERRORLEVEL% GTR 0 (ECHO Exiting... & EXIT /B %ERRORLEVEL%) ELSE (ECHO OK.)

ECHO %ec% Compiling JmsPutGetInteractive Utility application source.
ECHO %ec% Commands to run are:
ECHO: 
FOR /F %%a IN ('cd') DO ECHO cd %%a
ECHO javac -cp .\%allClientJar%;.\%jmsApiJar%;.\%jsonJar%;. com\ibm\mq\samples\jms\%JmsAppSrc%
ECHO:
javac -cp .\%allClientJar%;.\%jmsApiJar%;.\%jsonJar%;. com\ibm\mq\samples\jms\%JmsAppSrc%
IF %ERRORLEVEL% GTR 0 (ECHO Error compiling JmsPutGetInteractive Utility. & EXIT /B %ERRORLEVEL%) ELSE (ECHO OK.)

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
IF %ERRORLEVEL% EQU 0 (for /f %%i in ('Powershell -Command "$enc = Read-Host -AsSecureString; $stringValue = [Runtime.InteropServices.Marshal]::PtrToStringAuto([Runtime.InteropServices.Marshal]::SecureStringToBSTR($enc));echo $stringValue"') DO set app_pwd=%%i) ELSE (ECHO %ec% !!WARNING!! Password will be displayed in clear text. See %jmsAppSrc% or -pw options for workshop. & set /p app_pwd=)

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
ECHO java -cp .\%allClientJar%;.\%jmsApiJar%;.\%jsonJar%;. com.ibm.mq.samples.jms.%JmsAppClass% %host_name%%port%%channel%%qmgr%%app_user%-pw _your_password_ %queue%%mode%%TLS%
ECHO:

java -cp .\%allClientJar%;.\%jmsApiJar%;.\%jsonJar%;. com.ibm.mq.samples.jms.%JmsAppClass% %host_name% %port% %channel% %qmgr% %app_user% %app_pwd% %queue% %mode% %TLS%
IF %ERRORLEVEL% NEQ 0 (ECHO Error running JmsPutGetInteractive Utility.)

REM Complete, exit 0
ECHO %ec% Done!
EXIT /B 0

:confirmNextStep
CHOICE /c yn /m "%~1"
IF %ERRORLEVEL% EQU 1 (EXIT /B 0)
IF %ERRORLEVEL% EQU 2 (EXIT /B 1)
