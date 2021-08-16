# IBM MQ TechCon21 Demo Instructions

### Note: these instructions support the demonstration of an IBM MQ queue manager in a docker container and illustrate various capabilities as part of a demo. This is not intended as a reference guide or as production ready sample. Please refer to the [IBM MQ Documentation](https://www.ibm.com/docs/en/ibm-mq/latest?topic=mq-in-containers-cloud-pak-integration) for more information on planning and deployment IBM MQ in a container.

## TechCon 2021, IBM MQ in a container general demo notes

- Please follow this [tutorial](https://developer.ibm.com/tutorials/mq-connect-app-queue-manager-containers/) for full instructions on installing docker and running IBM MQ Developer Edition in a container.

##Pull the image
```
docker pull ibmcom/mq:latest
```

You can check the images has been successfully pulled with
```
docker images
```
and look for ```ibmcom/mq latest``` in the listing

Now you have the MQ image, you're ready to run an IBM MQ queue manager as a container. However, by default container storage will be 'in memory' so messages will not persist on queues between container restarts. One approach is to crate a docker volume to attach persistant storage to the container.

```
 docker volume create qm1data
```
In this example, the storage is named ```qm1data```

## Run the queue manager server container

Note: the ```docker run``` command includes a default value for the ```app``` user password, please modify this to something more suitable to your needs. 

```
 docker run --env LICENSE=accept --env MQ_QMGR_NAME=QM1 --volume qm1data:/mnt/mqm --publish 1414:1414 --publish 9443:9443 --detach --env MQ_APP_PASSWORD=passw0rd --name QM1 ibmcom/mq:latest
```

The ```qm1data``` volume is mounted into the container under ```/mnt/mqm``` to provide a persistant store

In this example, the queue manager is named ```QM1``` and two ports have been bound to the host so that messaging clients can connect to the queue manager and exchange messages on ```1414```. We can also access IBM MQ Web Console via a browser on port ```9443```. We have also assigned the container instance a name of ```QM1``` for convenience

### Check the queue manager is running

Check the docker process is running

```
docker ps
```

The command should return a container id for a the runnign queue manager

```
CONTAINER ID.         IMAGE              COMMAND            CREATED          STATUS         PORTS           NAMES                                                                                          NAMES
<your-container-id>   ibmcom/mq:latest   "runmqdevserver"   12 seconds ago   Up 6 seconds   0.0.0.0:1414->1414/tcp, :::1414->1414/tcp, 0.0.0.0:9443->9443/tcp, :::9443->9443/tcp, 9157/tcp   sad_leavitt   QM1
```

Assuming the container is running, we can now check the queue manager is available. To do this, we exec into the container and start a bash shell.

```
docker exec -ti QM1 /bin/bash
```

We now have a command prompt inside the running container and can check the status of the queue manager using the ```dspmq``` command.

```
dspmq
```

And we see the queue manager status

```
QMNAME(QM1)                                               STATUS(Running)
```

### Connecting to the queue manager with the IBM MQ Console

The MQ container image includes the IBM MQ Console which we can connect to on port 9443. Open a web browser and naviaget to the console URL

```
https://localhost:9443/ibmmq/console
```

The console uses a self-signed TLS certificate in its default configuration. To proceed we need to accept the certificate and the console login page will load. Sign into the console with the user id ```admin``` and the password ```passw0rd``` (unless a different password chosen in the ```docker run``` command issued earlier). 

Once in the console we can explore the default developer configuration.



## Connecting an application

### Configuration

The MQ dev patterns repo contains a standard set of samples spanning a range of languages and APIs. In this step we will clone the repo and put some messages with a Golang application. 

First create / change to a suitable directory to work on the dev patterns repo clone and then clone the repo. 

```
git clone https://github.com/ibm-messaging/mq-dev-patterns.git
```

under the ```mq-dev-patterns``` top level directory, you'll see an ```env.json``` file and the ```Go``` directory that contians the Golang sample applications.

Open the env.json file in a suitable editor e.g., ```atom env.json```

In the first MQ_ENDPOINTS entry remove the following lines as we have not configured TLS security at this stage

```
"CIPHER": "TLS_RSA_WITH_AES_128_CBC_SHA256",
"CIPHER_SUITE": "TLS_RSA_WITH_AES_128_CBC_SHA256",
"KEY_REPOSITORY": "../keys/clientkey"
````

Then modify the entry to include the correct credentials for the queue manager ```QM1``` running in our container. The entry should look similar to:

```
{
  "MQ_ENDPOINTS": [{
    "HOST": "localhost",
    "PORT": "1414",
    "CHANNEL": "DEV.APP.SVRCONN",
    "QMGR": "QM1",
    "APP_USER": "app",
    "APP_PASSWORD": "passw0rd",
    "QUEUE_NAME": "DEV.QUEUE.1",
    "MODEL_QUEUE_NAME": "DEV.APP.MODEL.QUEUE",
    "DYNAMIC_QUEUE_PREFIX": "APP.REPLIES.*",
    "TOPIC_NAME": "dev/"
  }]
}
```
Save the ```env.json``` file.

### Running the Golang app

Change to the Golang source directory

```
cd Go/src
```

Run the ```basicput.go``` application

```
go run basicput.go
```

You should see successful completion of the Golang application

```
MQ Put: 2021/08/16 18:27:29 Writing Message to Queue
MQ Put: 2021/08/16 18:27:29 Sending message {"greeting":"Hello from Go at ****-**-**T18:27:29+01:00","value":81}
MQ Put: 2021/08/16 18:27:29 Put message to DEV.QUEUE.1
MQ Put: 2021/08/16 18:27:29 MsgId:414d5120514d31202020202020202020fa9f1a6101290040
MQ Put: 2021/08/16 18:27:29 Application is Ending
```

### Checking the messages on the queue

You can use the console to check the messages were delieverd to ```DEV.QUEUE.1```




## TLS Security (self singed)

This [tutorial](https://developer.ibm.com/tutorials/mq-secure-msgs-tls/) contains full step by step instructions

Make and change into a directory called ```keys``

```
mkdir keys
cd keys
```

### Create a Server key and certificate for the queue manager

```
openssl req -newkey rsa:2048 -nodes -keyout key.key -x509 -days 365 -out key.crt
```

Optionally, you can check the key with the following command

```
openssl x509 -text -noout -in key.crt
```

### Create a client key store (Java) and add the server certificate

In this part of the demo we will use a Java client to Put and Get messages to a queue using TLS. The first step is to create a keystore and add the server certificate.

```
keytool -keystore clientkey.jks -storetype jks -importcert -file key.crt -alias server-certificate
```


### Start a new queue manager container, this time passing a key and certificate for the server to use

```
docker run --name mqtls --env LICENSE=accept --env MQ_QMGR_NAME=QM1 --volume [!!path to directory with key and crt files!!]:/etc/mqm/pki/keys/mykey --publish 1414:1414 --publish 9443:9443 --detach --env MQ_APP_PASSWORD=passw0rd ibmcom/mq:latest
```

### Validate that the channel is secured with TLS

```
docker exec -ti mqtls /bin/bash
```

Once attached ot the container with a bash shell

```
runmqsc QM1
```

Now show the Channel config

```
DISPLAY CHANNEL('DEV.APP.SVRCONN')
```

And validate a cipher spec has been set under the ```SSLCIPH``` attribute. You should see something like ```SSLCIPH(ANY_TLS12)```. Exit the shell.

```
exit
exit
```

### Prepare the JMS client config 

Download the three prereq jars [as linked here](https://github.com/ibm-messaging/mq-dev-patterns/tree/master/JMS) and copy them to the ```mq-dev-patterns/JMS``` top level directory and then compile the put sample.

```
javac -cp ./com.ibm.mq.allclient-9.2.3.0.jar:./javax.jms-api-2.0.1.jar:./json-simple-1.1.1.jar:. com/ibm/mq/samples/jms/JmsPut.java
```

Add the following lines to the ```env.json``` file.

```
"CIPHER": "TLS_RSA_WITH_AES_128_CBC_SHA256",
"CIPHER_SUITE": "TLS_RSA_WITH_AES_128_CBC_SHA256",
"KEY_REPOSITORY": "../keys/clientkey"
````

### Run the Java client sample app

```
java -Djavax.net.ssl.trustStoreType=jks -Djavax.net.ssl.trustStore=path-to-keys-directory/clientkey.jks -Djavax.net.ssl.trustStorePassword=passw0rd -Dcom.ibm.mq.cfg.useIBMCipherMappings=false -cp ./com.ibm.mq.allclient-9.2.3.0.jar:./javax.jms-api-2.0.1.jar:./json-simple-1.1.1.jar:. com.ibm.mq.samples.jms.JmsPut
```

The sample app should complete successfully with the message:

```
INFO: Sent all messages!
```

### Checking the messages on the queue

You can use the console to check the messages were delieverd to ```DEV.QUEUE.1```


## Running the Java sample application in a container

### Create the docker network

To simplify the configuration enabling the application container to talk to the queue manager server we'll use a docker network

```
docker network create techcon21
```

### Restart the docker container and connect to the new docker network

```
docker run --name mqtls --env LICENSE=accept --env MQ_QMGR_NAME=QM1 --volume [!!path to directory with key and crt files!!]:/etc/mqm/pki/keys/mykey --publish 1414:1414 --publish 9443:9443 --detach --env MQ_APP_PASSWORD=passw0rd --network techcon21 ibmcom/mq:latest
```

### Update the env.json file

Change localhost to be the name that we gave to our container instance so that it can be found on the docker network

```
"HOST": "mqtls",
```

### Create the application Dockerfile under ```mq-dev-patterns```

```
atom Dockerfile
```

Add the following to your Dockerfile


```
FROM adoptopenjdk/openjdk8

RUN mkdir /mq-java-sample
RUN cd /mq-java-sample

COPY mq-dev-patterns/JMS JMS
COPY mq-dev-patterns/env.json .
COPY keys/clientkey.jks ./JMS

RUN cd JMS
WORKDIR JMS
RUN javac -cp ./com.ibm.mq.allclient-9.2.3.0.jar:./javax.jms-api-2.0.1.jar:./json-simple-1.1.1.jar:. com/ibm/mq/samples/jms/JmsPut.java
CMD java -Djavax.net.ssl.trustStoreType=jks -Djavax.net.ssl.trustStore=./clientkey.jks -Djavax.net.ssl.trustStorePassword=passw0rd -Dcom.ibm.mq.cfg.useIBMCipherMappings=false -cp ./com.ibm.mq.allclient-9.2.3.0.jar:./javax.jms-api-2.0.1.jar:./json-simple-1.1.1.jar:. com.ibm.mq.samples.jms.JmsPut
```

### Build youe MQ JMS application sample docker image

```
docker build . -t mq-app
```

### Run your MQ JMS sample app 

```
docker run --network techcon21 mq-app
```

The sample app should complete successfully with the message:

```
INFO: Sent all messages!
```

### Checking the messages on the queue

You can use the console to check the messages were delieverd to ```DEV.QUEUE.1```
