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

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;

public class Configuration {
    private static final Logger logger = new AppLogger().getLogger();
    private static final String module = "Configuration : ";

    private final EnvProcessor env = new EnvProcessor();

    // Create variables for the connection to MQ
    private String ConnectionString; 
    private String CHANNEL; 
    private String QMGR; 
    private String APP_USER; 
    private String APP_PASSWORD; 
    private String APP_NAME; 
    private String QUEUE_NAME; 
    
    public Configuration(String app_name) {
        logger.finest(module + "Determining configuration");
        if (! notEmpty(app_name)) {
            app_name = Constants.DEFAULT_APP_NAME;
        }
        load(app_name);
    } 

    public String getConnectionString() {
        return ConnectionString;
    }

    public String getChannel() {
        return CHANNEL;
    }

    public String getQMGR() {
        return QMGR;
    }

    public String getAppName() {
        return APP_NAME;
    }

    public String getAppUser() {
        return APP_USER;
    }

    public String getAppPassword() {
        return APP_PASSWORD;
    }

    public String getQueue() {
        return QUEUE_NAME;
    }

    public boolean isOK() {
        return env.isOK() && haveAllConfig();
    }

    private boolean haveAllConfig() {
        return notEmpty(ConnectionString) 
                && notEmpty(CHANNEL)
                && notEmpty(QMGR)
                && notEmpty(APP_USER)
                && notEmpty(APP_PASSWORD)
                && notEmpty(QUEUE_NAME);
    }

    private boolean notEmpty(String field) {
        return (field != null && ! field.trim().isEmpty());
    }

    private void load(String app_name) {
        ConnectionString = env.getConnectionString();
        CHANNEL = env.getEnvValue(Constants.CHANNEL);
        QMGR = env.getEnvValue(Constants.QMGR);
        APP_USER = env.getEnvValue(Constants.APP_USER);
        APP_PASSWORD = env.getEnvValue(Constants.APP_PASSWORD);

        APP_NAME = env.getEnvValueOrDefault(Constants.APP_NAME, app_name);
        QUEUE_NAME = env.getEnvValue(Constants.QUEUE_NAME);
    }

}
