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

// 
 
package com.ibm.mq.samples.jms;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

public class DemoMessageListener implements MessageListener {

	public void onMessage(Message message) {
		System.out.println("## entry onMessage");
		
		if (message instanceof TextMessage){ // The message is sent as a Message object, so we must determine its type
            TextMessage textMessage = (TextMessage) message; // Casts to TextMessage object
            try {
                System.out.println("-- MyMessageListener received message with payload: " + textMessage.getText());
            } catch (JMSException jmse) {
				System.out.println("JMS Exception in MyMessageListener class!");
				System.out.println(jmse.getLinkedException());
            }
        } else {
            System.out.println("-- Message received was not of type TextMessage.\n");
		}
		
		System.out.println("##exit onMessage"); // So we know when onMessage has finished execution
	}

}