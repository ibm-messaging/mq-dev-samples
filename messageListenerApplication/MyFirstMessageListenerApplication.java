/*
* (c) Copyright IBM Corporation 2018
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package com.ibm.mq.samples.jms;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.Destination;
import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.JMSProducer;

import com.ibm.msg.client.jms.JmsConnectionFactory;
import com.ibm.msg.client.jms.JmsFactoryFactory;
import com.ibm.msg.client.wmq.WMQConstants;

public class MyFirstMessageListenerApplication { 

	// Create variables for the connection to MQ
	private static final String HOST = "localhost"; // Host name or IP address
	private static final int PORT = 1414; // Listener port for your queue manager
	private static final String CHANNEL = "DEV.APP.SVRCONN"; // Channel name
	private static final String QMGR = "QM1"; // Queue manager name
	private static final String APP_USER = "app"; // User name that application uses to connect to MQ
	private static final String APP_PASSWORD = "password"; // Password that the application uses to connect to MQ
	private static final String QUEUE_NAME = "DEV.QUEUE.1"; // Queue that the application uses to put and get messages to and from

	public static void main(String[] args) {
		// Declare JMS 2.0 objects
		JMSContext context;
		Destination destination; // The destination will be a queue, but could also be a topic 
		JMSProducer producer;
		JMSConsumer consumer;
		
		JmsConnectionFactory connectionFactory = createJMSConnectionFactory();

		setJMSProperties(connectionFactory);

		System.out.println("MQ Test: Connecting to " + HOST + ", Port " + PORT + ", Channel " + CHANNEL
		+ ", Connecting to " + QUEUE_NAME);

		try {
			context = connectionFactory.createContext(); // This is connection + session. The connection is started by default
			destination = context.createQueue("queue:///" + QUEUE_NAME); // Set the producer and consumer destination to be the same... not true in general
			producer = context.createProducer();
			consumer = context.createConsumer(destination); // associate consumer with the queue we put messages onto

			/************IMPORTANT PART******************************/
			MessageListener ml = new MyMessageListener(); // Creates a listener object
			consumer.setMessageListener(ml); // Associates listener object with the consumer
			
			// The message listener will now listen for messages in a separate thread (see MyMessageListener.java file)
			System.out.println("The message listener is running."); // (Because the connection is started by default)
			// The messaging system is now set up
			/********************************************************/

			// The other logic for your program can now be implemented. This app can run whilst the listener listens in the background
			// For our application, the userInterface method runs a loop for the user to interact with the listener by sending and receiving messages
			userInterface(context, connectionFactory, producer, destination);
			
		} catch (Exception e) {
			System.out.println("!! Failure in application main method !!");
			e.printStackTrace();
		}
	}

	private static JmsConnectionFactory createJMSConnectionFactory() {
        JmsFactoryFactory ff;
        JmsConnectionFactory cf;
        try {
            ff = JmsFactoryFactory.getInstance(WMQConstants.WMQ_PROVIDER);
            cf = ff.createConnectionFactory();
        } catch (JMSException jmsex) {
            System.out.println("JMS Exception when trying to create connection factory!");
			jmsex.printStackTrace();
            cf = null;
        }
        return cf;
    }

	private static void setJMSProperties(JmsConnectionFactory cf) {
        try {
            cf.setStringProperty(WMQConstants.WMQ_HOST_NAME, HOST);
            cf.setIntProperty(WMQConstants.WMQ_PORT, PORT);
            cf.setStringProperty(WMQConstants.WMQ_CHANNEL, CHANNEL);
            cf.setIntProperty(WMQConstants.WMQ_CONNECTION_MODE, WMQConstants.WMQ_CM_CLIENT);
            cf.setStringProperty(WMQConstants.WMQ_QUEUE_MANAGER, QMGR);
            cf.setStringProperty(WMQConstants.WMQ_APPLICATIONNAME, "JmsPutGet (JMS)");
            cf.setBooleanProperty(WMQConstants.USER_AUTHENTICATION_MQCSP, true);
            cf.setStringProperty(WMQConstants.USERID, APP_USER);
            cf.setStringProperty(WMQConstants.PASSWORD, APP_PASSWORD);
        } catch (JMSException jmsex) {
			System.out.println("JMS Exception when trying to set JMS properties!");
			jmsex.printStackTrace();
        }
        return;
	}
	
	public static void userInterface(JMSContext context, JmsConnectionFactory connectionFactory, JMSProducer producer, Destination destination) {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		while (true) {
			String command;
			try {
				System.out.print("Ready : ");
				command = br.readLine(); // Takes command line input
				command = command.toLowerCase();

				switch (command) {
					case "start": case "restart":
						context.start(); // Starting the context also starts the message listener
						System.out.println("--Message Listener started.");
						break;
					case "stop":
						context.stop(); // Stopping the context also stops the message listener
						System.out.println("--Message Listener stopped.");
						break;
					case "send":
						sendATextMessage(connectionFactory, producer, destination);
						System.out.println("--Sent text message.");
						break;
					case "exit":
						context.close(); // Also stops the context
						System.out.println("bye...");
						System.exit(0);
					default:
						System.out.println("Help: valid commands are start/restart, stop, send and exit");
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void sendATextMessage(JmsConnectionFactory connectionFactory, JMSProducer producer, Destination destination) {
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			System.out.print("payload : "); // Asks for the message payload as input
			String payload = br.readLine();

			// Need a separate context to create and send the messages because they are received asynchronously
			JMSContext producerContext = connectionFactory.createContext();
			producer = producerContext.createProducer();
			Message m = producerContext.createTextMessage(payload);
			producer.send(destination, m);
			producerContext.close();
		} catch (Exception e) {
			System.out.println("Exception when trying to send a text message!");
			e.printStackTrace();}
	}

}
