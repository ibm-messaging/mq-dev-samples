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

package com.ibm.mq.lab.samples.jakarta.utils;

import java.util.List;
import java.util.ArrayList;
import java.util.logging.Logger;

import jakarta.jms.Destination;
import jakarta.jms.JMSConsumer;
import jakarta.jms.JMSContext;
import jakarta.jms.JMSException;
import jakarta.jms.JMSRuntimeException;
import jakarta.jms.JMSProducer;
import jakarta.jms.TextMessage;
import jakarta.jms.Message;

import com.ibm.msg.client.jakarta.jms.JmsConnectionFactory;
import com.ibm.msg.client.jakarta.jms.JmsFactoryFactory;
import com.ibm.msg.client.jakarta.wmq.WMQConstants;

import com.ibm.mq.jakarta.jms.MQDestination;


public class Connection {
    private static final Logger logger = new AppLogger().getLogger();
    private static final String module = "Connection : ";

    private JMSContext context = null;
    private Destination destination = null;
    private JMSProducer producer = null;
    private JMSConsumer consumer = null;

    public Connection(Configuration config) {
        destination = null;
        
        logger.info(module + "Creating connection factory");
        JmsConnectionFactory connectionFactory = createConnectionFactory();
        setJMSProperties(connectionFactory, config);

        logger.info(module + "Creating context");
        context = connectionFactory.createContext();

        destination = context.createQueue("queue:///" + config.getQueue());
        // Set targetClient to be non JMS, so no JMS headers are transmitted.
        // setTargetClient(destination);
        // ** Soheel todo when working with JMS headers, try without.
    }

    public void createProducer() {
        logger.info(module + "Creating Producer");
        producer = context.createProducer();
    }

    public void createConsumer(String program) {
        logger.info(module + "Creating Consumer");
        if (null == program || program.trim().isEmpty()) {
            consumer = context.createConsumer(destination);
        } else {
            String selector = Constants.LOYALTY_PROGRAM + " = '" + program + "'";
            consumer = context.createConsumer(destination, selector);
        }
    }

    public void send(List<LoyaltyMessage> messages) {
        logger.info(module + "Sending messages");
        try {
            if (null != producer) {
                for (LoyaltyMessage msg : messages) {  
                    TextMessage textMessage = context.createTextMessage(msg.message());             
                    textMessage.setStringProperty(Constants.LOYALTY_PROGRAM, msg.loyalty());
                    producer.send(destination, textMessage);
                } 
            }
        } catch (JMSException jmsex) {
            recordFailure(jmsex);
        }
    }

    public List<Message> consume() {
        boolean continueProcessing = true;
        List<Message> messages = new ArrayList<Message>();
 
        try {
            while (continueProcessing) {
                Message receivedMessage = consumer.receive(Constants.GET_TIME_OUT);

                if (receivedMessage == null) {
                    logger.info(module + "No more messages received from this endpoint");
                     continueProcessing = false;
                } else {
                    logger.info(module + "Message retrieved");
                    messages.add(receivedMessage);
                    if (messages.size() > Constants.MESSAGE_LIMIT) {
                        continueProcessing = false;
                    }
                }
            }
        } catch (JMSRuntimeException jmsex) {
            recordFailure(jmsex);
            continueProcessing = false;
        }

        return messages;
    }

    public void close () {
        if (null != context) {
            producer = null;
            consumer = null;
            context.close();
            context = null;
        }
    }

    private JmsConnectionFactory createConnectionFactory() {
        JmsFactoryFactory ff;
        JmsConnectionFactory cf;
        try {
            ff = JmsFactoryFactory.getInstance(WMQConstants.JAKARTA_WMQ_PROVIDER);
            cf = ff.createConnectionFactory();
        } catch (JMSException jmsex) {
            recordFailure(jmsex);
            cf = null;
        }
        return cf;
    }

    private void setJMSProperties(JmsConnectionFactory cf, Configuration config) {
        logger.info(module + "Configuring connection factory");
        try {
            cf.setStringProperty(WMQConstants.WMQ_CONNECTION_NAME_LIST, config.getConnectionString());
            cf.setStringProperty(WMQConstants.WMQ_CHANNEL, config.getChannel());
            cf.setIntProperty(WMQConstants.WMQ_CONNECTION_MODE, WMQConstants.WMQ_CM_CLIENT);
            cf.setStringProperty(WMQConstants.WMQ_QUEUE_MANAGER, config.getQMGR());
            cf.setStringProperty(WMQConstants.WMQ_APPLICATIONNAME, config.getAppName());
            cf.setBooleanProperty(WMQConstants.USER_AUTHENTICATION_MQCSP, true);
            
            cf.setStringProperty(WMQConstants.USERID, config.getAppUser());
            cf.setStringProperty(WMQConstants.PASSWORD, config.getAppPassword());

        } catch (JMSException jmsex) {
            recordFailure(jmsex);
        }
    }

    private  void setTargetClient(Destination destination) {
        try {
            MQDestination mqDestination = (MQDestination) destination;
            mqDestination.setTargetClient(WMQConstants.WMQ_CLIENT_NONJMS_MQ);
        } catch (JMSException jmsex) {
          logger.warning("Unable to set target destination to non JMS");
        }
      }


    private void recordFailure(Exception ex) {
        JmsExceptionHelper.recordFailure(logger,ex);
        return;
    }


}
