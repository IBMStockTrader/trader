/*
       Copyright 2017-2021 IBM Corp All Rights Reserved
       Copyright 2022-2025 Kyndryl, All Rights Reserved

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

import jakarta.json.bind.annotation.JsonbProperty;

/** JSON-B POJO class representing a Sentiment JSON object from the Sentiment Analysis API */
public class Sentiment {
    private String symbol;
    private double positive;
    private double negative;
    private double neutral;
    private double netSentiment;
    private String dominantSentiment;
    private String timestamp;
    private int sourcesAnalyzed;

    public Sentiment() { //default constructor
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public double getPositive() {
        return positive;
    }

    public void setPositive(double positive) {
        this.positive = positive;
    }

    public double getNegative() {
        return negative;
    }

    public void setNegative(double negative) {
        this.negative = negative;
    }

    public double getNeutral() {
        return neutral;
    }

    public void setNeutral(double neutral) {
        this.neutral = neutral;
    }

    @JsonbProperty("net_sentiment")
    public double getNetSentiment() {
        return netSentiment;
    }

    @JsonbProperty("net_sentiment")
    public void setNetSentiment(double netSentiment) {
        this.netSentiment = netSentiment;
    }

    @JsonbProperty("dominant_sentiment")
    public String getDominantSentiment() {
        return dominantSentiment;
    }

    @JsonbProperty("dominant_sentiment")
    public void setDominantSentiment(String dominantSentiment) {
        this.dominantSentiment = dominantSentiment;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    @JsonbProperty("sources_analyzed")
    public int getSourcesAnalyzed() {
        return sourcesAnalyzed;
    }

    @JsonbProperty("sources_analyzed")
    public void setSourcesAnalyzed(int sourcesAnalyzed) {
        this.sourcesAnalyzed = sourcesAnalyzed;
    }
}

