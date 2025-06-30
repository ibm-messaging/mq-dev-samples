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

import java.util.Enumeration;
import java.util.logging.Logger;

import jakarta.jms.Message;
import jakarta.jms.ObjectMessage;
import jakarta.jms.StreamMessage;
import jakarta.jms.TextMessage;
import jakarta.jms.BytesMessage;
import jakarta.jms.MapMessage;
import jakarta.jms.JMSException;

public class MessageInspector {

    private static final Logger logger = new AppLogger().getLogger();
    private static final String module = "MessageInspector : ";

    Message message;
    
    public MessageInspector() {}

    public void inspect(Message msg) {
        message = msg;

        try {
            showMessageType();
            showMessageHeaders();
            showProperties();
            showMessageBody();
        } catch (Exception ex) {
            JmsExceptionHelper.recordFailure(logger,ex);
        }

    }

    private void showMessageType() {
        if (null != message) {
            logger.info(module + "Message is of type : " + message.getClass());
            if (message instanceof TextMessage) {
                logger.info(module + "Message matches TextMessage");
            } else if (message instanceof BytesMessage) {
                logger.info(module + "Message matches BytesMessage");
            } else if (message instanceof MapMessage) {
                logger.info(module + "Message matches MapMessage");
            } else if (message instanceof StreamMessage) {
                logger.info(module + "Message matches StreamMessage");
            } else if (message instanceof ObjectMessage) {
                logger.info(module + "Message matches ObjectMessage");
            }  
        }      
    }

    private void showMessageHeaders() throws JMSException {
        if (null != message) {
            logger.info("Message Header Fields ");
            logger.info("  Destination : " + message.getJMSDestination());
            logger.info("  Delivery mode : " + message.getJMSDeliveryMode());
            logger.info("  Message ID : " + message.getJMSMessageID());
            logger.info("  Correlation ID : " + message.getJMSCorrelationID());
            logger.info("  Timestamp : " + message.getJMSTimestamp());
            logger.info("  Redelivered : " + message.getJMSRedelivered());
            logger.info("  Expiration : " + message.getJMSExpiration());
            logger.info("  Priority : " + message.getJMSPriority());
            logger.info("  Type : " + message.getJMSType());
            logger.info("  ReplyTo : " + message.getJMSReplyTo());
          }
    }

    private void showProperties() {
        if (null != message) {
            try {
                Enumeration props = message.getPropertyNames();
                if (null == props) {
                logger.info(module + "No properties found");
                } else {
                logger.info("Properties : ");
                    while (props.hasMoreElements()) {
                        String p = (String) props.nextElement();
                        switch (p) {
                        case Constants.LOYALTY_PROGRAM:
                            logger.info("  " + p + " : " + message.getStringProperty(p));
                            break;
                        default:
                            // logger.info("  " + p);
                            break;
                        }
                    }
                }
            } catch (JMSException e) {
                logger.info("No properties found");
            }
        }
    }

    private void showMessageBody() throws JMSException {
        if (null != message && message instanceof TextMessage) {
            TextMessage textMessage = (TextMessage) message;
            logger.info("Received message: " + textMessage.getText());
        }  
    }

}
