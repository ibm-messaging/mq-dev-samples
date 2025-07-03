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

public class Constants {
    public static final String HOST = "MQ_HOST";
    public static final String PORT = "MQ_PORT";

    public static final String CHANNEL = "CHANNEL";
    public static final String QMGR = "QMGR";
    public static final String QUEUE_NAME = "QUEUE_NAME";

    public static final String APP_USER = "APP_USER";
    public static final String APP_PASSWORD = "APP_PASSWORD";

    public static final String APP_NAME = "APP_NAME";
    public static final String DEFAULT_APP_NAME = "Jakarta Messaging Sample App";
    public static final String DEFAULT_PUT_APP_NAME = "Put Sample App";
    public static final String DEFAULT_GET_APP_NAME = "Get Sample App";

    public static final String ENV_FILE = "EnvFile";
    public static final String DEFAULT_ENV_FILE =  "./env.json"; 

    public static final String MQ_ENDPOINTS = "MQ_ENDPOINTS";

    public static final long GET_TIME_OUT = 5000;  // 5 Seconds
    public static final int MESSAGE_LIMIT = 100;

    public static final String LOYALTY_PROGRAM = "LoyaltyProgram";
    public static final String LOYALTY_GOLD = "Gold";
    public static final String LOYALTY_SILVER = "Silver";
    public static final String LOYALTY_BRONZE = "Bronze";

    public static final int GOLD_LIMIT = 4;
    public static final int SILVER_LIMIT = 8;
    public static final int BRONZE_LIMIT = 12;

    public static final String GOLD_MESSAGE_TEMPLATE = LOYALTY_GOLD + " " + LOYALTY_PROGRAM + " message ";
    public static final String SILVER_MESSAGE_TEMPLATE = LOYALTY_SILVER + " " + LOYALTY_PROGRAM + " message ";
    public static final String BRONZE_MESSAGE_TEMPLATE = LOYALTY_BRONZE + " " + LOYALTY_PROGRAM + " message ";

}
