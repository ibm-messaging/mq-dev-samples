# IBM MQ TechCon21 Demo Instructions

### Note: these instructions support the demonstration of an IBM MQ queue manager in a docker container and illustrate various capabilities as part of a demo. This is not intended as a reference guide or as production ready sample. Please refer to the [IBM MQ Documentation](https://www.ibm.com/docs/en/ibm-mq/latest?topic=mq-in-containers-cloud-pak-integration) for more information on planning and deployment IBM MQ in a container.

### TechCon 2021, IBM MQ in a container general demo notes

- Please follow this [tutorial](https://developer.ibm.com/tutorials/mq-connect-app-queue-manager-containers/) for full instructions on installing docker and running IBM MQ Developer Edition in a container.

### Pull the image
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

### Run the queue manager server container

```
 docker run --env LICENSE=accept --env MQ_QMGR_NAME=QM1 --volume qm1data:/mnt/mqm --publish 1414:1414 --publish 9443:9443 --detach --env MQ_APP_PASSWORD=passw0rd ibmcom/mq:latest
```

The ```qm1data``` volume is mounted into the container under ```/mnt/mqm``` to provide a persistant store

In this example, the queue manager is named ```QM1``` and two ports have been bound to the host so that messaging clients can connect to the queue manager and exchange messages on ```1414```. We can also access IBM MQ Web Console via a browser on port ```9443``` 
