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

public class EnvProcessor {
    private static final Logger logger = new AppLogger().getLogger();
    private static final String module = "EnvProcessor : ";

    public static final String ENV_FILE = Constants.ENV_FILE;
    public static final String DEFAULT_ENV_FILE = Constants.DEFAULT_ENV_FILE;   
    
    private boolean haveConfiguration = false;
    private JSONArray mqEndPoints;

    public EnvProcessor() {
        logger.finest(module + "Determining environment configuration");
        JSONObject mqEnvSettings = null;
        mqEndPoints = null;  

        File file = getEnvFile();
        if (null == file) {
            logger.warning(module + "No Environment settings file found");
            return;
        }

        try {
            String content = new String(Files.readAllBytes(Paths.get(file.toURI())));
            mqEnvSettings = new JSONObject(content);

            logger.finest( module + "File read");

            if (mqEnvSettings != null) {
              logger.finest(module + "JSON Data Found");
              mqEndPoints = (JSONArray) mqEnvSettings.getJSONArray(Constants.MQ_ENDPOINTS);
            }

            if (mqEndPoints == null || mqEndPoints.isEmpty()) {
                logger.warning(module + "No Endpoints found in .json file");
                return;
            } 

            haveConfiguration = true;

        } catch (IOException e) {
            logger.warning(module + "Error processing env.json file");
            logger.warning(e.getMessage());
        } catch (JSONException e) {
            logger.warning(module + "Error parsing env.json file");
            logger.warning(e.getMessage());         
        }
    } 

    public boolean isOK() {
        return haveConfiguration;
    }

    public String getConnectionString() {
        List<String> coll = new ArrayList<String>();

        for (Object o : mqEndPoints) {
            JSONObject jo = (JSONObject) o;
            String s = (String) jo.get(Constants.HOST) + "(" + (String) jo.get(Constants.PORT) + ")";
            coll.add(s);
        }

        String connString = String.join(",", coll);
        logger.finest(module + "Connection string will be " + connString);

        return connString;
    }

    public String getEnvValue(String key) {
        return getEnvValue(key, 0);
    }

    public String getEnvValueOrDefault(String key, String defaultValue) {
        String value = getEnvValue(key, 0);

        return (null == value || value.trim().isEmpty()) 
                        ? defaultValue
                        : value;
    }

    private String getEnvValue(String key, int index) {
        JSONObject mqAppEnv = null;
        String value = System.getProperty(key);

        try {
            if ((value == null || value.isEmpty()) &&
                        mqEndPoints != null &&
                        ! mqEndPoints.isEmpty()) {
                mqAppEnv = (JSONObject) mqEndPoints.get(index);
                value = (String) mqAppEnv.get(key);
            }
        } catch (JSONException e) {
          logger.warning(module + "Error looking for json key " + key);
          logger.warning(e.getMessage());         
        }

        if (! key.contains("PASSWORD")) {
          logger.info(module + "returning " + value + " for key " + key);
        }
        return value;
    }

    private File getEnvFile() {
        File file = null;
        // Allow system setting to override env file location and name
        String valueEnvFile = System.getProperty(ENV_FILE);
        logger.info(module + ENV_FILE + " is set to " + valueEnvFile);

        if (null != valueEnvFile) {
        } else {
            valueEnvFile = DEFAULT_ENV_FILE;
        }

        logger.info(module + "Looking for environment file " + valueEnvFile);
        
        file = new File(valueEnvFile);
        if (! file.exists()){
            logger.warning(module + "Environment settings " + valueEnvFile + " file not found");
            logger.warning(module + "Application does not know how to connect to Queue Manager");
            file = null;
        }  

        return file;
    }
}
