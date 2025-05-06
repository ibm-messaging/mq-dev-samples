/*
* (c) Copyright IBM Corporation 2025
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

package com.ibm.mq.samples.jakarta;

// Use these imports for building with Jakarta Messaging
import jakarta.jms.Destination;
import jakarta.jms.JMSConsumer;
import jakarta.jms.JMSProducer;
import jakarta.jms.JMSContext;
import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.TextMessage;
import jakarta.jms.JMSRuntimeException;
import java.io.Console;

import com.ibm.msg.client.jakarta.jms.JmsConnectionFactory;
import com.ibm.msg.client.jakarta.jms.JmsFactoryFactory;
import com.ibm.msg.client.jakarta.wmq.WMQConstants;
import com.ibm.mq.constants.MQConstants;


/**
 * A minimal and simple application for Intereactive Point-to-point messaging.
 *
 * Application makes use of fixed literals, any customisations will require
 * re-compilation of this source file. Application assumes that the named queue
 * is empty prior to a run.
 *
 * Notes:
 *
 * API type: Jakarta API (JMS v3.0, simplified domain)
 *
 * Messaging domain: Point-to-point
 *
 * Provider type: IBM MQ
 *
 * Connection mode: Client connection
 *
 * JNDI in use: No
 *
 */
public class JakartaPutGetInteractive {

	// Java console colour settings to make Put and Get easier to follow
	private static final String BACKGROUND_BLACK = "\u001B[40m";
	private static final String BACKGROUND_WHITE = "\u001B[47m";
	private static final String TEXT_BLACK = "\u001B[30m";
	private static final String TEXT_WHITE = "\u001B[37m";
	private static final String DEFAULT_TEXT = "\u001B[0m";

	// Constants for very simple console logging method
	private static final int MSG = 0;
	private static final int INFO = 1;
	private static final int WARNING = 2;
	private static final int ERROR = 3;

	// Boolean flags to coordinate Jakarta Put and Get operations
	private static Boolean doGet = true;
	private static Boolean doPut = true;
	// Set doTls true for TLS, TLS is off (false) by default
	private static Boolean doTls = false;

	// System exit status value (assume unset value to be 1)
	private static int status = 1;

	/*
	 * Set your Jakarta Connection Factory Variables HERE
	 */
	// Create variables for the connection to MQ
	private static String HOST = "_YOUR_HOSTNAME_"; // Host name or IP address
	private static int PORT = 1414; // Listener port for your queue manager
	private static String CHANNEL = "DEV.APP.SVRCONN"; // Channel name
	private static String QMGR = "QM1"; // Queue manager name
	private static String APP_USER = "app"; // User name that application uses to connect to MQ
	private static String APP_PASSWORD = "_APP_PASSWORD_"; // Password that the application uses to connect to MQ
	private static String QUEUE_NAME = "DEV.QUEUE.1"; // Queue that the application uses to put and get messages to and from
	/*
	 * Note: you should not need to edit below this line for the workshop
	 */

	/**
	 * Main method
	 *
	 * @param args
	 */
	public static void main(String[] args) {

		parseArgs(args);

		// Jakarta object variables
		JMSContext context = null;
		Destination destination = null;
		JMSProducer producer = null;
		JMSConsumer consumer = null;

		try {
			// Create a connection factory
			JmsFactoryFactory ff = JmsFactoryFactory.getInstance(WMQConstants.JAKARTA_WMQ_PROVIDER);
			JmsConnectionFactory cf = ff.createConnectionFactory();

			// Set the properties
			cf.setStringProperty(WMQConstants.WMQ_HOST_NAME, HOST);
			cf.setIntProperty(WMQConstants.WMQ_PORT, PORT);
			cf.setStringProperty(WMQConstants.WMQ_CHANNEL, CHANNEL);
			cf.setIntProperty(WMQConstants.WMQ_CONNECTION_MODE, WMQConstants.WMQ_CM_CLIENT);
			cf.setStringProperty(WMQConstants.WMQ_QUEUE_MANAGER, QMGR);
			cf.setStringProperty(WMQConstants.WMQ_APPLICATIONNAME, "JakartaPutGet (Jakarta)");
			cf.setBooleanProperty(WMQConstants.USER_AUTHENTICATION_MQCSP, true);
			cf.setStringProperty(WMQConstants.USERID, APP_USER);
			cf.setStringProperty(WMQConstants.PASSWORD, APP_PASSWORD);
			if (doTls) {
				cf.setStringProperty(WMQConstants.WMQ_SSL_CIPHER_SUITE, "*TLS12ORHIGHER");
				cf.setIntProperty(MQConstants.CERTIFICATE_VALIDATION_POLICY, MQConstants.MQ_CERT_VAL_POLICY_NONE);

			}

			// Create Jakarta objects
			context = cf.createContext();
			destination = context.createQueue("queue:///" + QUEUE_NAME);

			if (doPut) {
				long uniqueNumber = System.currentTimeMillis() % 1000;
				TextMessage message = context.createTextMessage("Your lucky number today is " + uniqueNumber);

				producer = context.createProducer();
				producer.send(destination, message);
				logOutput(INFO, "Sent message:\n" + message);
				if (doGet) {
					Console c = System.console();
					logOutput(MSG, BACKGROUND_WHITE + TEXT_BLACK + "Ready to receive a message?" + DEFAULT_TEXT);
					logOutput(MSG, BACKGROUND_BLACK + TEXT_WHITE + "Press the Enter key to continue ..."
							+ DEFAULT_TEXT);
					c.readLine();
				}
			}

			if (doGet) {
				int waitTime = 15000; // in ms or 15 seconds
				consumer = context.createConsumer(destination); // autoclosable
				String receivedMessage = null;
				if (!doPut) { // if this is a get only operation then display message
					TextMessage message = (TextMessage) consumer.receive(waitTime);
					if (message != null) {
						logOutput(INFO, "Received message:\n" + message);
						receivedMessage = message.getText();
					}
				} else {
					receivedMessage = consumer.receiveBody(String.class, waitTime);
				}
				if (receivedMessage != null) {
					logOutput(INFO, "\nReceived message payload:\n" + receivedMessage);
				} else {
					logOutput(INFO, "No Message Received Within " + waitTime + "ms");
				}
			}

			context.close();
			recordSuccess();
		} catch (JMSException jmsex) {
			recordFailure(jmsex);
		}

		System.exit(status);

	} // end main()

	/**
	 * Record this run as successful.
	 */
	private static void recordSuccess() {
		logOutput(MSG, "SUCCESS");
		status = 0;
		return;
	}

	/**
	 * Record this run as failure.
	 *
	 * @param ex
	 */
	private static void recordFailure(Exception ex) {
		if (ex != null) {
			if (ex instanceof JMSException) {
				processJMSException((JMSException) ex);
			} else {
				System.out.println(ex);
			}
		}
		System.out.println("FAILURE");
		status = -1;
		return;
	}

	/**
	 * Process a JMSException and any associated inner exceptions.
	 *
	 * @param jmsex
	 */
	private static void processJMSException(JMSException jmsex) {
		System.out.println(jmsex);
		Throwable innerException = jmsex.getLinkedException();
		if (innerException != null) {
			System.out.println("Inner exception(s):");
		}
		while (innerException != null) {
			System.out.println(innerException);
			innerException = innerException.getCause();
		}
		return;
	}

	/**
	 * Log output to console in a consistent way.
	 *
	 * @param level   the log level to output
	 * @param message the message to log
	 */
	private static void logOutput(int level, String message) {
		String eyeCatcher = "#### ";
		String tag = "";
		switch (level) {
			case 0:
				tag = "  ";
				break;
			case 1:
				tag = eyeCatcher + "INFO: ";
				break;
			case 2:
				tag = eyeCatcher + "WARNING: ";
				break;
			case 3:
				tag = eyeCatcher + "ERROR: ";
				break;
			default:
				tag = "";
		}
		//Print output to console
		System.out.println(tag + message);
	}

	/**
	 * Helper method to parse the args[] from the command line.
	 *
	 * @param args   the list of argument passed to the main method
	 * 
	 */
	private static void parseArgs(String args[]) {
		System.out.println(""); // Presentation padding on console
		// Sanity check main() arguments and warn user
		if (args.length > 0) {
			logOutput(WARNING,
					"You have provided arguments to the Java main() function. JVM arguments (such as -Djavax.net.ssl.trustStore) must be passed before the main class or .jar you wish to run.\n\n");

			// Parse command line arguments, assuming name value pairs
			String option = null;
			String value = null;
			for (int i = 0; i < args.length; i += 2) { // iterate 2: name,value
				option = args[i].toLowerCase(); // force lower case

				// Check the option and cast the value as needed
				switch (option) {
					case "-host": // hostname
						HOST = getValue(option, args, i);
						logOutput(INFO, "Host [" + HOST + "]");
						break;
					case "-p": // port number
						try {
							PORT = Integer.parseInt(getValue(option, args, i));
						} catch (NumberFormatException nfe) {
							status = -1;
							logOutput(ERROR, "Unabale to parse value \"" + value + "\" specified for option \""
									+ option + "\"");
							System.exit(status);
						}
						logOutput(INFO, "Port [" + PORT + "]");
						break;
					case "-c": // channel name
						CHANNEL = getValue(option, args, i);
						logOutput(INFO, "Channel [" + CHANNEL + "]");
						break;
					case "-qm":
						QMGR = getValue(option, args, i);
						logOutput(INFO, "Queue Manager [" + QMGR + "]");
						break;
					case "-u":
						APP_USER = getValue(option, args, i);
						logOutput(INFO, "App User [" + APP_USER + "]");
						break;
					case "-pw":
						APP_PASSWORD = getValue(option, args, i);
						logOutput(INFO, "Password [" + "******" + "]");
						break;
					case "-q":
						QUEUE_NAME = getValue(option, args, i);
						logOutput(INFO, "Queue [" + QUEUE_NAME + "]");
						break;
					case "-t":
						i--; // no value for boolean flags, so realign
						doTls = true;
						logOutput(INFO, "TLS enabled");
						break;
					case "-put":
						i--; // no value for boolean flags, so realign
						doGet = false;
						logOutput(INFO, "put only mode");
						break;
					case "-get":
						i--; // no value for boolean flags, so realign
						doPut = false;
						logOutput(INFO, "get only mode");
						break;
					default:
						status = -1;
						if (!(option.equals("-h") || option.equals("-help"))) {
							status = 0;
							logOutput(ERROR, "Invalid option: \"" + option + "\"");
						}
						logOutput(INFO, "Usage:");
						logOutput(MSG, "Welcome to " + JakartaPutGetInteractive.class.getSimpleName());
						logOutput(MSG, "A JMS Put and Get utility for the IBM MQ Developer Essential Workshop.");
						logOutput(MSG,
								"The default bahavior is to generate a random number and add this number to a new message payload. The message is then produced and then consumed from the specified queue. The following command line options are available. ");
						logOutput(MSG, ""); // Padding
						logOutput(MSG, "Options:");
						logOutput(MSG, "-help | -h		Displays this message.");
						logOutput(MSG, "-host <hostname>	Specifiy hostname");
						logOutput(MSG, "-p <port_number>	Specifiy port number");
						logOutput(MSG, "-c <channel>		Specifiy channel");
						logOutput(MSG, "-qm <qm_name>		Specifiy queue manager name");
						logOutput(MSG, "-u <app_user>		Specifiy App User name");
						logOutput(MSG, "-pw <password>	Specifiy hostname");
						logOutput(MSG, "-q <queue_name>	Specifiy queue name");
						logOutput(MSG, "-put 			Put only mode");
						logOutput(MSG, "-get 			Get only mode");
						logOutput(MSG, "-t 			TLS mode");
						logOutput(MSG, "");
						System.exit(status);
				}
			}

			if (!doPut && !doGet) {
				// Both -put and -get must have been set as options
				doPut = true;
				doGet = true;
			}
		}
	}

	/**
	 * Get the next value in the args[] array i.e., the option value.
	 *
	 * @param option current option in scope
	 * @param args   the list of argument passed to the main method
	 * @param index  current position in the args list
	 * @return the value set for the current option in scope
	 */
	private static String getValue(String option, String[] args, int index) {
		String theValue = null;
		if (args.length > index + 1) {
			theValue = args[index + 1];
		} else {
			logOutput(ERROR, "No value provided for option \"" + option + "\"");
			System.exit(status);
		}
		return theValue;
	}
}
