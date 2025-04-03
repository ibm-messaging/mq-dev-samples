#!/bin/bash

# (c) Copyright IBM Corporation 2025
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
# http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

# Version control

allClientVer="9.4.2.0"
jmsApiVer="2.0.1"
jsonVer="20220320"

allClientJar="com.ibm.mq.allclient-$allClientVer.jar"
jmsApiJar="javax.jms-api-$jmsApiVer.jar"
jsonJar="json-$jsonVer.jar"
JmsAppClass="JmsPutGetInteractive"
JmsAppSrc="$JmsAppClass.java"

ec="###"
fileExistsMsg="$ec File exists, skipping."

checkReturnCode () {
  if [[ $1 -ne 0 ]]
  then
    echo $ec $2
    echo $ec Exiting...
    exit 1
  else
    echo $ec OK.
  fi
}

confirmNextStep () {
  while true; do
    read -p "$1 [y] or [n]" ans
    case $ans in
      [Yy]* ) break;;
      [Nn]* ) exit 1;;
      * ) echo "Please answer [y] or [n]";;
    esac
  done
}

echo $ec Checking JDK \(javac command\) is available.
javac --version >/dev/null 2>&1
checkReturnCode $? "Java Compiler not installed!"

echo $ec Making MQClient directory.
if [ ! -d MQClient ]
then 
  mkdir MQClient
  checkReturnCode $? "Error making MQClient directory."
else
  echo $fileExistsMsg
fi

echo $ec Changing to MQClient directory.
cd MQClient
checkReturnCode $? "Error changing to MQClient directory."

echo $ec Fetching IBM MQ all client Jar [$allClientJar].
if [ ! -f $allClientJar ]
then
  curl -o $allClientJar https://repo1.maven.org/maven2/com/ibm/mq/com.ibm.mq.allclient/$allClientVer/$allClientJar
  checkReturnCode $? "Error fetching IBM MQ all client Jar."
else
  echo $fileExistsMsg
fi

echo $ec Fetching JMS API Jar [$jmsApiJar].
if [ ! -f $jmsApiJar ]
then
  curl -o $jmsApiJar https://repo1.maven.org/maven2/javax/jms/javax.jms-api/$jmsApiVer/$jmsApiJar
  checkReturnCode $? "Error fetching JMS API Jar."
else
  echo $fileExistsMsg
fi

echo $ec Fethcing json Jar [$jsonJar].
 if [ ! -f $jsonJar ]
 then
   curl -o $jsonJar https://repo1.maven.org/maven2/org/json/json/$jsonVer/$jsonJar
   checkReturnCode $? "Error fethcing json Jar."
 else
   echo $fileExistsMsg
 fi

echo $ec Making Java directory sructure for JmsPutGetInteractive Utility.
if [ ! -d com/ibm/mq/samples/jms ]
then
  mkdir -p com/ibm/mq/samples/jms
  checkReturnCode $? "Error making Java directory sructure for JmsPutGetInteractive Utility."
else 
  echo $fileExistsMsg
fi

echo $ec Changing to com/ibm/mq/samples/jms directory.
cd com/ibm/mq/samples/jms
checkReturnCode $? "Error changing to com/ibm/mq/samples/jms directory."

echo $ec Fetching JmsPutGetInteractive Utility.
if [ ! -f $JmsAppSrc ]
then
  curl -o $JmsAppSrc https://raw.githubusercontent.com/ibm-messaging/mq-dev-samples/master/gettingStarted/jms/com/ibm/mq/samples/jms/$JmsAppSrc
  checkReturnCode $? "Error fethcing JmsPutGetInteractive Utility."
else 
  echo $fileExistsMsg
fi

echo $ec Changing back to MQClient directory.
cd -
checkReturnCode $? "Error changing to MQClient directory."

# confirmNextStep "Compile JmsPutGetInteractive Utility?"
read -p "Press Enter to compile JmsPutGetInteractive Utility..."
echo $ec Compiling JmsPutGetInteractive Utility application source.
echo $ec Commands to run are:
cmd="javac -cp ./$allClientJar:./$jmsApiJar:./$jsonJar:. com/ibm/mq/samples/jms/$JmsAppSrc"
echo
echo cd $(pwd)
echo $cmd
echo
$(echo $cmd)
checkReturnCode $? "Error compiling JmsPutGetInteractive Utility."

confirmNextStep "Run JMS Utility application?"
echo $ec Running JmsPutGetInteractive Utility application.

read -p "Enter hostname: " host_name
if [ ! -z $host_name ]
then
  host_name="-host $host_name"
fi

read -p "Enter port number: " port
if [ ! -z $port ]
then
  port="-p $port"
fi

read -p "Enter channel name: " channel
if [ ! -z $channel ]
then
  channel="-c $channel"
fi

read -p "Enter queue manager name: " qmgr
if [ ! -z $qmgr ]
then
  qmgr="-qm $qmgr"
fi

read -p "Enter app user: " app_user
if [ ! -z $app_user ]
then
  app_user="-u $app_user"
fi

echo -n "Enter app password:"
read -s app_pwd
if [ ! -z $app_pwd ]
then
  app_pwd="-pw $app_pwd"
fi
echo ""

read -p "Enter queue name: " queue
if [ ! -z $queue ]
then
  queue="-q $queue"
fi

read -p "Do TLS (default Yes) [y]|[n]" tls
if [ ! -z $tls ]
then
  case $tls in
    [Nn]* ) unset tls;;
    * ) tls="-t";;
  esac
else tls="-t";
fi

read -p "Queue Manager is on cloud? (default Yes) [y]|[n] : " is_qmgr_on_cloud
is_qmgr_on_cloud=${is_qmgr_on_cloud:-y}

JAVA_HOME=$( /usr/libexec/java_home )
DEFAULT_TRUST_STORE_PATH="$JAVA_HOME/lib/security/cacerts"
TRUST_STORE_PASSWORD="changeit"  # Default password for cacerts
TRUST_STORE_PATH="$DEFAULT_TRUST_STORE_PATH"

if [ "$is_qmgr_on_cloud" == "n" ] || [ "$is_qmgr_on_cloud" == "N" ];
then
  read -p "Enter the path to your custom trust store: " TRUST_STORE_PATH
  read -p "Enter the password for the custom trust store: " TRUST_STORE_PASSWORD
fi

read -p "Are you using IBM Cipher Mappings? (default Yes) [y]|[n]: " use_ibm_cipher
use_ibm_cipher=${use_ibm_cipher:-y}
USE_CIPHER_FLAG="true"

if [ "$use_ibm_cipher" == "n" ] || [ "$use_ibm_cipher" == "N" ];
then
  USE_CIPHER_FLAG="false"
fi
read -p "Put, Get or Both (default Both) [p]|[g]|[b]" mode
if [ ! -z $mode ]
then
  case $mode in
    [Pp]* ) mode="-put";;
    [Gg]* ) mode="-get";;
    * ) unset mode;;
  esac
fi
echo $ec Running...
cmd="java -Djavax.net.ssl.trustStoreType=jks -Djavax.net.ssl.trustStore=$TRUST_STORE_PATH -Djavax.net.ssl.trustStorePassword=$TRUST_STORE_PASSWORD -Dcom.ibm.mq.cfg.useIBMCipherMappings=$USE_CIPHER_FLAG -cp ./$allClientJar:./$jmsApiJar:./$jsonJar:. com.ibm.mq.samples.jms.$JmsAppClass $host_name $port $channel $qmgr $app_user $queue $mode $tls"
echo $ec Commands to run are:
echo 
echo $cmd -pw _your_password_
echo
$(echo $cmd $app_pwd)

checkReturnCode $? "Error running JmsPutGetInteractive Utility."

echo $ec Done!
exit 0
