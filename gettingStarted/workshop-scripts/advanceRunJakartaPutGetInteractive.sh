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

jakartaClientVer="9.4.2.0"
jakartaApiVer="3.1.0"
jsonVer="20250107"

jakartaClientJar="com.ibm.mq.jakarta.client-$jakartaClientVer.jar"
jakartaApiJar="jakarta.jms-api-$jakartaApiVer.jar"
jsonJar="json-$jsonVer.jar"
JakartaAppClass="JakartaPutGetInteractive"
JakartaAppSrc="$JakartaAppClass.java"

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

echo $ec Fetching IBM MQ jakarta client Jar [$jakartaClientJar].
if [ ! -f $jakartaClientJar ]
then
  curl -o $jakartaClientJar https://repo1.maven.org/maven2/com/ibm/mq/com.ibm.mq.jakarta.client/$jakartaClientVer/$jakartaClientJar
  checkReturnCode $? "Error fetching IBM MQ jakarta client Jar."
else
  echo $fileExistsMsg
fi

echo $ec Fetching Jakarta API Jar [$jakartaApiJar].
if [ ! -f $jakartaApiJar ]
then
  curl -o $jakartaApiJar https://repo1.maven.org/maven2/jakarta/jms/jakarta.jms-api/$jakartaApiVer/$jakartaApiJar
  checkReturnCode $? "Error fetching Jakarta API Jar."
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

echo $ec Making Java directory sructure for JakartaPutGetInteractive Utility.
if [ ! -d com/ibm/mq/samples/jakarta ]
then
  mkdir -p com/ibm/mq/samples/jakarta
  checkReturnCode $? "Error making Java directory sructure for JakartaPutGetInteractive Utility."
else 
  echo $fileExistsMsg
fi

echo $ec Changing to com/ibm/mq/samples/jakarta directory.
cd com/ibm/mq/samples/jakarta
checkReturnCode $? "Error changing to com/ibm/mq/samples/jakarta directory."

echo $ec Fetching JakartaPutGetInteractive Utility.
if [ ! -f $JakartaAppSrc ]
then
  curl -o $JakartaAppSrc https://raw.githubusercontent.com/ibm-messaging/mq-dev-samples/master/gettingStarted/jms/com/ibm/mq/samples/jakarta/$JakartaAppSrc
  checkReturnCode $? "Error fethcing JakartaPutGetInteractive Utility."
else 
  echo $fileExistsMsg
fi

echo $ec Changing back to MQClient directory.
cd -
checkReturnCode $? "Error changing to MQClient directory."

read -p "Press Enter to compile JakartaPutGetInteractive Utility..."
echo $ec Compiling JakartaPutGetInteractive Utility application source.
echo $ec Commands to run are:
cmd="javac -cp ./$jakartaClientJar:./$jakartaApiJar:./$jsonJar:. com/ibm/mq/samples/jakarta/$JakartaAppSrc"
echo
echo cd $(pwd)
echo $cmd
echo
$(echo $cmd)
checkReturnCode $? "Error compiling JakartaPutGetInteractive Utility."

confirmNextStep "Run Jakarta Utility application?"
echo $ec Running JakartaPutGetInteractive Utility application.

cmd="java"

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
### If you want to pass your truststore path and password and test the self signed certificates for the tls connection uncomment the below code and   ###
### comment out the "cf.setIntProperty(MQConstants.CERTIFICATE_VALIDATION_POLICY, MQConstants.MQ_CERT_VAL_POLICY_NONE);" line in the application code ###

# read -p "Queue Manager is on cloud? (default Yes) [y]|[n] : " is_qmgr_on_cloud
# is_qmgr_on_cloud=${is_qmgr_on_cloud:-y}

# if { [ "$is_qmgr_on_cloud" == "n" ] || [ "$is_qmgr_on_cloud" == "N" ]; } && [ "$tls" == "-t" ];
# then
#     read -p "Enter the trust store type: " TRUST_STORE_TYPE
#     read -p "Enter the path to your custom trust store: " TRUST_STORE_PATH
#     read -p "Enter the password for the custom trust store: " TRUST_STORE_PASSWORD
#     read -p "Are you using IBM Cipher Mappings? (default Yes) [y]|[n]: " use_ibm_cipher
#     use_ibm_cipher=${use_ibm_cipher:-y}
#     USE_CIPHER_FLAG="true"

#     if [ "$use_ibm_cipher" == "n" ] || [ "$use_ibm_cipher" == "N" ];
#     then
#     USE_CIPHER_FLAG="false"
#     fi
# fi

# add_java_prop_if_set() {
#   local key="$1"
#   local val="$2"
#   if [ -n "$val" ]; then
#     cmd="$cmd -D$key=$val"
#   fi
# }

# # Optional Java system properties
# add_java_prop_if_set "javax.net.ssl.trustStoreType" "$TRUST_STORE_TYPE"
# add_java_prop_if_set "javax.net.ssl.trustStore" "$TRUST_STORE_PATH"
# add_java_prop_if_set "javax.net.ssl.trustStorePassword" "$TRUST_STORE_PASSWORD"
# add_java_prop_if_set "com.ibm.mq.cfg.useIBMCipherMappings" "$USE_CIPHER_FLAG"

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

# If you want to see the TLS handshake uncomment the below line
# cmd="$cmd -Djavax.net.debug=ssl,handshake"
cmd="$cmd -cp ./$jakartaClientJar:./$jakartaApiJar:./$jsonJar:. com.ibm.mq.samples.jakarta.$JakartaAppClass $host_name $port $channel $qmgr $app_user $queue $mode $tls"
echo $ec Commands to run are:
echo 
echo $cmd -pw _your_password_
echo
$(echo $cmd $app_pwd)

checkReturnCode $? "Error running JakartaPutGetInteractive Utility."

echo $ec Done!
exit 0

