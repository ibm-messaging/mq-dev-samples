# Instructions for following the MQ Samples video

[Video available at this link.](video_link_will_go_here)

We're going to use a Java sample to put a message onto a queue, then a node.js sample to get the message from the queue. What follows is instructions for getting the samples that we show in the video to run. Once you've done the setup steps, you can run any of the samples in that language with a simple run command and adapt them to work in your software solution!

### Steps in this tutorial:
1.	Get IBM MQ and set up a queue manager
2.	Clone the repo
3.	Edit the env.json file to use your MQ information
4.	Set up your environment to use Java and the JMS API 
5.	Put a message with Java
6.	Look at your messages in the MQ console
7.	Set up your envrionment to use Node.js with the MQI API
8.	Get a message with Node.js
9.	Play, explore, contribute!

## Step 1: Get hold of IBM MQ!

In this step, you'll get IBM MQ. This gives you a messaging server and queues to put your messages onto.

[Watch this video to see how to get MQ in a Docker container](https://www.youtube.com/watch?v=xBX1P9OUteg) or [follow this link to get MQ on a different platform](https://developer.ibm.com/components/ibm-mq/series/mq-ready-set-connect/).

## Step 2: Clone the repo
Clone the repo at https://ibm.biz/mq-dev-patterns into a directory of your choice. 

## Step 3: Edit the `env.json` file to use your MQ information
In the root directory of the repo, there's a file called `env.json`. This contains connection information that is used by default by every single sample in this repo. Setting our MQ information in here once means we can run every sample, in every language, with these details!

Remove the first and third elements of the .json structure to leave the middle element. Fill in the details of your IBM MQ installation here (if you followed the above video or tutorial for setting up MQ in Docker, on Linux or on Windows, you can leave the variables as they are). 

## Step 4: Set up your environment to use Java and the JMS API
With Java, we're going to use JMS, which is a provider-agnostic messaging API for Java. We'll build the project with Maven, though there are instructions in the repo to compile and run without it as well.

### Get Java:
First, if you don't have Java on your machine, download it here: https://adoptopenjdk.net/. We recommend OpenJDK Version 11 for this tutorial, though other versions may work too.

### Get Maven:
We use Maven to manage dependencies and to build the code samples. This means you don't need to download the MQ or JMS API libraries as Maven does this for you. If you don't have Maven installed:

**If you're on Mac**, use Homebrew to install maven with

`brew install maven`

**If you're on Windows or Linux**, [download it from here](https://maven.apache.org/download.cgi) then follow the instructions on the site to set it up.

## Step 5: Put a message with Java
First, on your command line, cd into the JMS directory

`cd JMS`

Then run maven to clean out previous builds and compile the samples in the repo, ready to run.

`mvn clean package`

Then, to run a put sample, all you have to do is refer to the .jar file maven created and specify which sample you want to run. If you're putting or publishing, you need to specify how many messages you want to put/publish, e.g.
```
java -jar target/mq-dev-patterns-0.1.0.jar put 1
java -jar target/mq-dev-patterns-0.1.0.jar get
```

## Step 6: Look at your messages in the MQ console
In the video, I put a message and view it in the console. You can access the console via https://localhost:9443/ibmmq/console if you're running MQ on Docker, Windows or Linux. If you're running MQ in the cloud, you can access the console through your IBM Cloud interface. [If you want to know more about the MQ console, watch this video](https://www.youtube.com/watch?v=gp_ep-xYWfU).

## Step 7: Set up your environment to use Node.js with the MQI API
In the video, we get a message with Node.js. We use the IBM MQ Client API (the MQI), along with a Node.js wrapper that interfaces with Node.js. This is also how you can use MQ with Python, Golang etc. (with the correct MQI wrapper), so once you've tried this with Node.js you can also have a go with these languages!

### To follow along with Node.js:
To use MQ with Node.js, Python, Golang etc you'll need the MQ client code (MQI). 

**On Mac**, follow the instructions in this article: https://ibm.biz/mq-mac-dev
Then, set the DYLD_LIBRARY_PATH variable for the current shell by running

`export DYLD_LIBRARY_PATH=/opt/mqm/lib64`

or for all your shells at login by adding the above to your `~/.zshrc` profile.

**On Windows or Linux**, the `npm install` command we'll run will pull in the MQ client code for you, so you don't need to do the above.

Now, cd into the Node.js directory and run an `npm install` of the Node.js wrapper (and MQ API, if you're on Windows or Linux)
```
cd ../Node.js
npm install
```

## Step 8: Get a message with Node.js
Now, you're ready to use the Node.js samples to put and get messages. If you left a message on the queue from the Java step earlier, get it with Node.js by running

`DEBUG=sample*:*,boiler:* node sampleget.js`

This runs in debug mode, so you'll get a lot of info. If you just want to get messages from the queue without seeing the end result, run

`node basicget.js`

You'll then need to check the console to see that the messages have been consumed and have left the queue!

You can also put messages with Node.js by changing the command to

`DEBUG=sample*:*,boiler:* node sampleput.js`

## Step 9: Play, explore, contribute!
If you're happy with using the samples from this tutorial, the other samples in the repo are run in similar ways and they can all be built upon to create messaging applications for your own projects. If you want to contribute, the repo also accepts pull requests if there's anything you'd like to share or improve upon.

Hopefully this tutorial and video were helpful to you! If so, please [leave a like on the video](video_URL_goes_here) and let us know in the video comments how you found the tutorial. (Sorry, it's for the algorithm so more people searching for IBM MQ find our videos.)

Happy making!
