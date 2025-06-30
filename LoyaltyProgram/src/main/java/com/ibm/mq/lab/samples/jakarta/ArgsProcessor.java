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

public class ArgsProcessor {

    public ArgsProcessor() {}

    public String programFromArgs (String[] args) {
        String program = "";
        if (null != args) {
            for (String a : args) {
                program = programCheck(a);
                if (!program.trim().isEmpty()) {
                    break;
                }
            }
        }
        return program;
    }

    public String programCheck(String candidate) {
        String program = "";

        String value = candidate.toLowerCase();

        if (value.equals(Constants.LOYALTY_GOLD.toLowerCase())) {
            program = Constants.LOYALTY_GOLD;
        } else if (value.equals(Constants.LOYALTY_SILVER.toLowerCase())) {
            program = Constants.LOYALTY_SILVER;
        } else if (value.equals(Constants.LOYALTY_BRONZE.toLowerCase())) {
            program = Constants.LOYALTY_BRONZE;
        } 

        return program;
    }

}
