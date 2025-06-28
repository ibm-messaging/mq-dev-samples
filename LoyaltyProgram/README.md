## Build

```
mvn clean package
```

## Put

```
java -jar ./target/mq-dev-lab-samples-0.1.0.jar com.ibm.mq.lab.samples.jakarta 
```

## Get

For all loyalty messages

```
java -cp ./target/mq-dev-lab-samples-0.1.0.jar com.ibm.mq.lab.samples.jakarta.Get
```

For only Gold loyalty messages

```
java -cp ./target/mq-dev-lab-samples-0.1.0.jar com.ibm.mq.lab.samples.jakarta.Get gold
```

For only Silver loyalty messages

```
java -cp ./target/mq-dev-lab-samples-0.1.0.jar com.ibm.mq.lab.samples.jakarta.Get silver
```
