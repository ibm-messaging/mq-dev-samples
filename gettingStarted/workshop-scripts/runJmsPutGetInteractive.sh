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

allClientJar="com.ibm.mq.allclient-$allClientVer.jar"
jmsApiJar="javax.jms-api-$jmsApiVer.jar"
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

echo $ec Checking JDK (javac command) is available.
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

echo $ec Making Java directory sructure for JmsPutGet Utility.
if [ ! -d com/ibm/mq/samples/jms ]
then
  mkdir -p com/ibm/mq/samples/jms
  checkReturnCode $? "Error making Java directory sructure for JmsPutGet Utility."
else 
  echo $fileExistsMsg
fi

echo $ec Changing to com/ibm/mq/samples/jms directory.
cd com/ibm/mq/samples/jms
checkReturnCode $? "Error changing to com/ibm/mq/samples/jms directory."

echo $ec Fetching JmsPutGet Utility.
if [ ! -f $jmsAppSrc ]
then
  curl -o $jmsAppSrc https://raw.githubusercontent.com/ibm-messaging/mq-dev-samples/master/gettingStarted/jms/com/ibm/mq/samples/jms/$JmsAppSrc
  checkReturnCode $? "Error fethcing JmsPutGet Utility."
else 
  echo $fileExistsMsg
fi

echo $ec Changing back to MQClient directory.
cd -
checkReturnCode $? "Error changing to MQClient directory."

confirmNextStep "Compile JmsPutGet Utility?"
echo $ec Compiling JmsPutGet Utility application source.
javac -cp ./$allClientJar:./$jmsApiJar:. com/ibm/mq/samples/jms/$JmsAppSrc
checkReturnCode $? "Error compiling JmsPutGet Utility."

confirmNextStep "Run JMS Utility application?"
echo $ec Running JmsPutGet Utility application.

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

java -cp ./$allClientJar:./$jmsApiJar:. com.ibm.mq.samples.jms.$JmsAppClass $host_name $port $channel $qmgr $app_user $app_pwd $queue $mode $tls

checkReturnCode $? "Error running JmsPutGet Utility."

echo $ec Done!
exit 0