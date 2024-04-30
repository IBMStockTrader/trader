/*
       Copyright 2017-2021 IBM Corp, All Rights Reserved
       Copyright 2022-2024 Kyndryl, All Rights Reserved

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

package com.ibm.hybrid.cloud.sample.stocktrader.trader.test;

import static org.junit.Assert.assertTrue;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Invocation;
import jakarta.ws.rs.core.Response;

import org.junit.Test;

public class HomePageIT {

    private String port = System.getProperty("http.port");
    private String warContext = System.getProperty("war.name");

    private String url = "http://localhost:" + port + "/" + warContext + "/login";
    private static final int MAX_RETRY_COUNT = 5;
    private static final int SLEEP_TIMEOUT = 3000;

    @Test
    public void testHomeEndpoint() throws Exception {
        
        System.out.println("Testing endpoint " + url );
        int responseCode = makeRequest(url);
        for(int i = 0; (responseCode != 200) && (i < MAX_RETRY_COUNT); i++) {
          System.out.println("Response code : " + responseCode + ", retrying ... (" + i + " of " + MAX_RETRY_COUNT + ")");
          Thread.sleep(SLEEP_TIMEOUT);
          responseCode = makeRequest(url);
        }
        assertTrue("Incorrect response code: " + responseCode, responseCode == 200);
    }

    private int makeRequest(String urlToTest) {
      Client client = ClientBuilder.newClient();
      Invocation.Builder invoBuild = client.target(urlToTest).request();
      Response response = invoBuild.get();
      int responseCode = response.getStatus();
      response.close();
      return responseCode;
    }
}
