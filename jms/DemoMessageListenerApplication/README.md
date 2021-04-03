# Message Listener Application

This is an example of a basic message listener application. There is a command line interface for the user to start/stop the listener and send messages. 

It was discussed in [this tutorial](https://developer.ibm.com/messaging/learn-mq/mq-tutorials/slow-lost-messages-high-cpu-improve-your-mq-app/).

In order to run this application, you need to have a connection to a running queue manager. If you need help setting this up, [look here](https://developer.ibm.com/messaging/learn-mq/mq-tutorials/mq-connect-to-queue-manager/).

## Download the .jar files

These contain the classes for JMS and IBM MQ's JMS API.

[JMS classes](http://central.maven.org/maven2/javax/jms/javax.jms-api/2.0.1/javax.jms-api-2.0.1.jar)

[IBM MQ classes for JMS](http://central.maven.org/maven2/com/ibm/mq/com.ibm.mq.allclient/9.0.4.0/com.ibm.mq.allclient-9.0.4.0.jar)

## Compile

Compile with this command:

```bash
javac -cp ./com.ibm.mq.allclient-9.0.4.0.jar:./javax.jms-api-2.0.1.jar com/ibm/mq/samples/jms/*.java
```

## Run

Run with this command:

```bash
java -cp ./com.ibm.mq.allclient-9.0.4.0.jar:./javax.jms-api-2.0.1.jar:. com.ibm.mq.samples.jms.MyFirstMessageListenerApplication
```

## License

(c) Copyright IBM Corporation 2018

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License [here](http://www.apache.org/licenses/LICENSE-2.0).

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
