# mq-dev-samples

## Intro to Jakarta Samples

JakartaPut.java - Puts message to a queue

JakartaGet.java - Gets message from a queue

JakartaPutGet.java - Puts and Gets message from a queue

JakartaPutGetInteractive - Puts and Gets message from a queue (But it is interactive, User should press `enter' to recieve the message)

## Using the `gettingStarted/jakarta` samples with TLS 


## Getting setup

- Clone this repo
- Change directory to the working directory
  - Linux `cd mq-dev-samples/gettingStarted/jakarta`
  - Windows `cd mq-dev-samples\gettingStarted\jakarta`
- Download 

-[latest IBM MQ Jakarta client jar](https://central.sonatype.com/search?q=a:com.ibm.mq.jakarta.client&smo=true)
 ```
curl -o com.ibm.mq.jakarta.client-9.4.2.0.jar  https://repo1.maven.org/maven2/com/ibm/mq/com.ibm.mq.jakarta.client/9.4.2.0/com.ibm.mq.jakarta.client-9.4.2.0.jar 
```
 -[Jakarta Messaging API 2.0.1 jar](https://central.sonatype.com/search?q=a:jakarta.jms-api&smo=true)
 ```
curl -o jakarta.jms-api-3.1.0.jar  https://repo1.maven.org/maven2/jakarta/jms/jakarta.jms-api/3.1.0/jakarta.jms-api-3.1.0.jar 
```
 -[JSON parser](https://central.sonatype.com/artifact/org.json/json/20230227)
 ```
curl -o json-20250107.jar https://repo1.maven.org/maven2/org/json/json/20250107/json-20250107.jar  
```

## Generate application credentials and an API key

Navigate to the "Application Credentials" tab of your cloud MQ service instance.

Click `add`, then create a user called "app". Click `Add and generate API key`, which will bring up your API token. Copy this and save it somewhere as you'll need it in the application you run to authenticate yourself to your MQ instance.
Note: TLS is already enabled in IBM Cloud, so there's no need to download the qmgrcert.pem file. You can simply uncomment the following line in your code to connect to the queue manager.

`
cf.setStringProperty(WMQConstants.WMQ_SSL_CIPHER_SUITE, "TLS_ECDHE_RSA_WITH_AES_256_GCM_SHA384");
`

For the Queue Managers running on the container you can follow [Secure communication between IBM MQ endpoints with TLS](https://developer.ibm.com/tutorials/mq-secure-msgs-tls/) tutorial.

## Start coding to put and get a message with jakarta

Open the `com/ibm/mq/samples/jms/JakartaPutGet.java` file (Linux/Mac) or `com\ibm\mq\samples\jms\JakartaPutGet.java` (Windows) in your favorite editor

Uncomment the following line

```java 
cf.setStringProperty(WMQConstants.WMQ_SSL_CIPHER_SUITE, "*TLS12ORHIGHER");
```

Modify the following variables to match your IBM MQ configuration

If your MQ instance is running on IBM cloud 
- login to ibm cloud in a browser --> click resource list --> integration --> mq-ml --> click on a qmgr --> connection info
for more connection info - click connection information and download either json or ccdt. These will have all the MQ configuration details to run the application

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

- Change directory to `mq-dev-samples/gettingStarted/jakarta` (Linux/Mac) or `mq-dev-samples\gettingStarted\jakarta` (Windows)
- Compile your modified `JakartaPutGet.java` application
  - For Linux/Mac `javac -cp ./com.ibm.mq.jakarta.client-9.4.2.0.jar:./jakarta.jms-api-3.1.0.jar:./json-20250107.jar JakartaPutGet.java`
  - For Windows `javac -cp .\com.ibm.mq.jakarta.client-9.4.2.0.jar;.\jakarta.jms-api-3.1.0.jar;.\json-20250107.jar JakartaPutGet.java`
- You should now see a `JakartaPutGet.class` file alongside your `JakartaPutGet.java` source file

### Run your `jakartaPutGet` application

Execute your Java code, (For cloud user password will be an API and username will be mqUsername you'll get this info when you create an API key)
  - For Linux / Mac
  ```
  java -cp ./com.ibm.mq.jakarta.client-9.4.2.0.jar:./jakarta.jms-api-3.1.0.jar:./json-20250107.jar:. JakartaPutGet
  ```
  - For Windows
  ```
  java -cp .\com.ibm.mq.jakarta.client-9.4.2.0.jar;.\jakarta.jms-api-3.1.0.jar;.\json-20250107.jar;. JakartaPutGet
  ```
  
### What you should see

Congratuations, you've just sent you're first IBM MQ Message and you should see output similar to the following:

```console
Sent message:

  JMSMessage class: jms_text
  JMSType:          null
  JMSDeliveryMode:  2
  JMSDeliveryDelay: 0
  JMSDeliveryTime:  1742965156184
  JMSExpiration:    0
  JMSPriority:      4
  JMSMessageID:     ID:414d5120514d48452020202020202020b242d16701183a40
  JMSTimestamp:     1742965156184
  JMSCorrelationID: null
  JMSDestination:   queue:///DEV.QUEUE.1
  JMSReplyTo:       null
  JMSRedelivered:   false
    JMSXAppID: JakartaPutGet (Jakarta)     
    JMSXDeliveryCount: 0
    JMSXUserID: jakartatest 
    JMS_IBM_PutApplType: 28
    JMS_IBM_PutDate: 20250326
    JMS_IBM_PutTime: 04591763
Your lucky number today is 170

Received message:
Your lucky number today is 170
SUCCESS

```
