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

public class LoyaltyMessage {
    private String message;
    private String loyalty;

    public LoyaltyMessage(String m, String l) {
        message = m;
        loyalty = l;
    }

    public String message() {
        return message;
    }

    public String loyalty() {
        return loyalty;
    }
}
