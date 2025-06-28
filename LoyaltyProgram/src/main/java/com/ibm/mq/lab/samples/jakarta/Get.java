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

package com.ibm.mq.lab.samples.jakarta;

import java.util.List;
import java.util.logging.Logger;

import jakarta.jms.Message;

import com.ibm.mq.lab.samples.jakarta.utils.AppLogger;
import com.ibm.mq.lab.samples.jakarta.utils.ArgsProcessor;
import com.ibm.mq.lab.samples.jakarta.utils.Connection;
import com.ibm.mq.lab.samples.jakarta.utils.Configuration;
import com.ibm.mq.lab.samples.jakarta.utils.Constants;
import com.ibm.mq.lab.samples.jakarta.utils.MessageInspector;


public class Get {
    private static final Logger logger = new AppLogger().getLogger();
    private static final String module = "Get : ";

    public static void main(String[] args) {
        logger.info(module + "Starting Get messages process");
        logger.info(module + "Loading configuration");

        Configuration config = new Configuration(Constants.DEFAULT_GET_APP_NAME);
        if (!config.isOK()) {
            logger.warning(module + "Unable to run");
            return;
        }

        logger.info(module + "Establishing queue manager connection");
        Connection conn = new Connection(config);

        String program = (new ArgsProcessor()).programFromArgs(args);
        conn.createConsumer(program);

        List<Message> messages = conn.consume();
        processMessages(messages);

        conn.close();
    }

    private static void processMessages(List<Message> messages) {
        logger.info(module + "Processing " + messages.size() + " consumed messages");
        MessageInspector mi = new MessageInspector();
        for (Message msg : messages) {
            mi.inspect(msg);
        }
    }


}
