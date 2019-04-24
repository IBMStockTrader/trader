/*
       Copyright 2017-2019 IBM Corp All Rights Reserved

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */

package com.ibm.hybrid.cloud.sample.stocktrader.trader.json;

/** JSON-B POJO class representing a Feedback JSON object */
public class Feedback {
    private String message;
    private int free;
    private String sentiment;


    public Feedback() { //default constructor
    }

    public Feedback(String initialMessage) { //primary key constructor
        setMessage(initialMessage);
    }

    public Feedback(String initialMessage, int initialFree, String initialSentiment) {
        setMessage(initialMessage);
        setFree(initialFree);
        setSentiment(initialSentiment);
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String newMessage) {
        message = newMessage;
    }

    public int getFree() {
        return free;
    }

    public void setFree(int newFree) {
        free = newFree;
    }

    public String getSentiment() {
        return sentiment;
    }

    public void setSentiment(String newSentiment) {
        sentiment = newSentiment;
    }

    public String toString() {
        return "{\"message\": \""+message+"\", \"free\": "+free+", \"sentiment\": \""+sentiment+"\"}";
    }
}
