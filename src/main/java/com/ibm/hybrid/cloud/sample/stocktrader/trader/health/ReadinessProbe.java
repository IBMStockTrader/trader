/*
       Copyright 2019-2021 IBM Corp All Rights Reserved
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

package com.ibm.hybrid.cloud.sample.stocktrader.trader.health;

import com.ibm.hybrid.cloud.sample.stocktrader.trader.Utilities;

//Logging (JSR 47)
import java.util.logging.Logger;

//CDI 2.0
import jakarta.enterprise.context.ApplicationScoped;

//mpHealth 1.0
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.HealthCheckResponseBuilder;
import org.eclipse.microprofile.health.Readiness;


@Readiness
@ApplicationScoped
/** Use mpHealth for readiness probe */
public class ReadinessProbe implements HealthCheck {
    private static Logger logger = Logger.getLogger(ReadinessProbe.class.getName());
    private static String jwtAudience = System.getenv("JWT_AUDIENCE");
    private static String jwtIssuer = System.getenv("JWT_ISSUER");
	private static Utilities utilities = null;

	public ReadinessProbe() {
		if (utilities == null) utilities = new Utilities(logger);
	}
 

	//mpHealth probe
	public HealthCheckResponse call() {
		HealthCheckResponse response = null;
		String message = "Ready";
		try {
			HealthCheckResponseBuilder builder = HealthCheckResponse.named("Trader");

			if ((jwtAudience==null) || (jwtIssuer==null)) { //can't run without these env vars
				builder = builder.down();
				message = "JWT environment variables not set!";
				logger.warning("Returning NOT ready!");
			} else {
				builder = builder.up();
				logger.fine("Returning ready!");
			}
	
			builder = builder.withData("message", message);

			response = builder.build(); 
		} catch (Throwable t) {
			logger.warning("Exception occurred during health check: "+t.getMessage());
			utilities.logException(t);
			throw t;
		}

		return response;
	}
}
