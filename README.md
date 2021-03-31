# mq-dev-samples

## What this repo is for

This repo hosts sample code referred to and used as part of [tutorials for IBM MQ on IBM Developer](https://developer.ibm.com/components/ibm-mq/).

## Where to find code to copy/paste into your app

We have a wealth of messaging patterns, represented in many languages, including Java, .NET, Python, Node.js and Golang. [They are available at the mq-dev-patterns repo, here!](https://github.com/ibm-messaging/mq-dev-patterns)

Feel free to use code from the mq-dev-patterns repo to create your own messaging applications.

## Using the `gettingStarted/jms` samples with TLS (IBM DDC MQ Badge Lab)

For reference tese instructions were built from [this tutorial](https://developer.ibm.com/components/ibm-mq/tutorials/mq-develop-mq-jms/). Check it out for additional hints and tips.

### Getting setup

- Clone this repo
- Change directory to `mq-dev-samples/gettingStarted/jms` (Linix) or `mq-dev-samples\gettingStarted\jms` (Windows)
- Download the `javax.jms-api-2.0.1.jar` jar

```
curl -o javax.jms-api-2.0.1.jar https://repo1.maven.org/maven2/javax/jms/javax.jms-api/2.0.1/javax.jms-api-2.0.1.jar
```

- Download the IBM MQ all client (9.2.2.0) `com.ibm.mq.allclient-9.2.2.0.jar` jar

```
curl -o com.ibm.mq.allclient-9.2.2.0.jar https://repo1.maven.org/maven2/com/ibm/mq/com.ibm.mq.allclient/9.2.2.0/com.ibm.mq.allclient-9.2.2.0.jar
```

- Copy the Queue Manager's public certificate .pem file `qmgrcert.pem` to the `mq-dev-samples/gettingStarted/jms` directory

`cp <YOUR_DOWNLOAD_DIRECTORY>/qmgrcert.pem .` (Linux) or `copy <YOUR_DOWNLOAD_DIRECTORY>\qmgrcert.pem .` (Windows)

- Use `keytool` to create a client `.p12` trust store

```
keytool -keystore clientTruststore.p12 -storetype pkcs12 -importcert -file qmgrcert.pem -alias server-certificate
```

- Enter and then re-enter a password for your new trustrore file `clientTruststore.p12`. It's important to remember this password as we'll use it when we run the smaple later.
- Type `yes` to trust this certificate

### Start coding to put a message jms

Open the `com/ibm/mq/samples/jms/JmsPut.java` file (Linux) or `com\ibm\mq\samples\jms\JmsPut.java` (Windows) in your favorate editor

uncoment the following line

```java 
cf.setStringProperty(WMQConstants.WMQ_SSL_CIPHER_SUITE, "*TLS12");
```

Modify the following variables to match your IBM MQ configuration

```java
private static final String HOST = "_YOUR_HOSTNAME_"; // Host name or IP address
private static final int PORT = 1414; // Listener port for your queue manager
private static final String CHANNEL = "DEV.APP.SVRCONN"; // Channel name
private static final String QMGR = "QM1"; // Queue manager name
private static final String APP_USER = "app"; // User name that application uses to connect to MQ
private static final String APP_PASSWORD = "_APP_PASSWORD_"; // Password that the application uses to connect to MQ
private static final String QUEUE_NAME = "DEV.QUEUE.1"; // Queue that the application uses to put and get messages to and from
```

### Compile your jms code

- Change directory to `mq-dev-samples/gettingStarted/jms` (Linix) or `mq-dev-samples\gettingStarted\jms` (Windows)
- compile your modified `JmsPut.java` application
  - For Liniux `javac -cp ./com.ibm.mq.allclient-9.1.4.0.jar:./javax.jms-api-2.0.1.jar com/ibm/mq/samples/jms/JmsPut.java`
  - For Windows `javac -cp .\com.ibm.mq.allclient-9.1.4.0.jar;.\javax.jms-api-2.0.1.jar com\ibm\mq\samples\jms\JmsPut.java`
- You should now see a `JmsPut.class` file alongside your `JmsPut.java` source file

### Run your `jmsPut` application

Execute your Java code, specifying the `clientTruststore.p12` and `password` you set up earlier
  - For Linux 
  ```
  java -Djavax.net.ssl.trustStore=clientTruststore.p12 -Djavax.net.ssl.trustStorePassword=passw0rd -cp ./com.ibm.mq.allclient-9.2.2.0.jar:./javax.jms-api-2.0.1.jar:. com.ibm.mq.samples.jms.JmsPut
  ```
  - For Windows
  ```
  java -Djavax.net.ssl.trustStore=clientTruststore.p12 -Djavax.net.ssl.trustStorePassword=passw0rd -cp .\com.ibm.mq.allclient-9.2.2.0.jar;.\javax.jms-api-2.0.1.jar;. com.ibm.mq.samples.jms.JmsPut
  ```
  
### What you should see

Congratuations, you've just sent you're first IBM MQ Message and you should see output similar to the following:

```console
Sent message:

  JMSMessage class: jms_text
  JMSType:          null
  JMSDeliveryMode:  2
  JMSDeliveryDelay: 0
  JMSDeliveryTime:  1617186154396
  JMSExpiration:    0
  JMSPriority:      4
  JMSMessageID:     ID:414d512049424d5f4444435f514d202000476460013f0040
  JMSTimestamp:     1617186154396
  JMSCorrelationID: null
  JMSDestination:   queue:///DEV.QUEUE.1
  JMSReplyTo:       null
  JMSRedelivered:   false
    JMSXAppID: JmsPutGet (JMS)             
    JMSXDeliveryCount: 0
    JMSXUserID: app         
    JMS_IBM_PutApplType: 28
    JMS_IBM_PutDate: 20210331
    JMS_IBM_PutTime: 10223528
Your lucky number today is 369
SUCCESS
```
