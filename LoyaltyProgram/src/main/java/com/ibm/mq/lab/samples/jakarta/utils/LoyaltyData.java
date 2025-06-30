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
import java.util.Collections;
import java.util.List;

import jakarta.jms.TextMessage;

public class LoyaltyData {
    private String template;
    private String program;
    private int limit;

    public LoyaltyData(String t, String p, int l) {
        template = t;
        program = p;
        limit = l;
    }

    public String template() {
        return template;
    }

    public String program() {
        return program;
    }

    public int limit() {
        return limit;
    }

    public static List<LoyaltyMessage> generate() {
        List<LoyaltyMessage> messages = new ArrayList<LoyaltyMessage>();

        LoyaltyData[] programs = generateLD();

        for (LoyaltyData prog : programs) {               
            for (int i = 1; i <= prog.limit(); i++) {
                String msg = prog.template() + " " + i + ".";
                messages.add(new LoyaltyMessage(msg, prog.program()));
            }
        }  

        Collections.shuffle(messages);
        return messages;
    }

    private static LoyaltyData[] generateLD() {
        return new LoyaltyData[] { 
            new LoyaltyData(Constants.GOLD_MESSAGE_TEMPLATE, 
                                Constants.LOYALTY_GOLD,
                                Constants.GOLD_LIMIT)
            ,new LoyaltyData(Constants.SILVER_MESSAGE_TEMPLATE, 
                                Constants.LOYALTY_SILVER,
                                Constants.SILVER_LIMIT)
            ,new LoyaltyData(Constants.BRONZE_MESSAGE_TEMPLATE, 
                                Constants.LOYALTY_BRONZE,
                                Constants.BRONZE_LIMIT)
        };
    }

}

