/*
       Copyright 2022 IBM Corp All Rights Reserved

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

package com.ibm.hybrid.cloud.sample.stocktrader.trader;

import com.ibm.hybrid.cloud.sample.stocktrader.trader.client.BrokerClient;

import com.ibm.cloud.objectstorage.ClientConfiguration;
import com.ibm.cloud.objectstorage.auth.AWSCredentials;
import com.ibm.cloud.objectstorage.auth.AWSStaticCredentialsProvider;
import com.ibm.cloud.objectstorage.client.builder.AwsClientBuilder.EndpointConfiguration;
import com.ibm.cloud.objectstorage.oauth.BasicIBMOAuthCredentials;

//AWS S3 (wrapper for IBM Cloud Object Storage buckets)
import com.ibm.cloud.objectstorage.services.s3.AmazonS3;
import com.ibm.cloud.objectstorage.services.s3.AmazonS3ClientBuilder;

//Having to use a proprietary WebSphere Liberty class here - ugh!
import com.ibm.websphere.security.openidconnect.PropagationHelper;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

//mpJWT 1.0
import org.eclipse.microprofile.jwt.JsonWebToken;


public class Utilities {
	private static Logger logger = Logger.getLogger(Utilities.class.getName());

	private static boolean useS3 = false;
	private static AmazonS3 s3 = null;
	private static String s3Bucket = null;

	// Override Broker Client URL if config map is configured to provide URL
	static {
		useS3 = Boolean.parseBoolean(System.getenv("S3_ENABLED"));
		logger.info("useS3: "+useS3);

		String mpUrlPropName = BrokerClient.class.getName() + "/mp-rest/url";
		String brokerURL = System.getenv("BROKER_URL");
		if ((brokerURL != null) && !brokerURL.isEmpty()) {
			logger.info("Using Broker URL from config map: " + brokerURL);
			System.setProperty(mpUrlPropName, brokerURL);
		} else {
			logger.info("Broker URL not found from env var from config map, so defaulting to value in jvm.options: "
			 + System.getProperty(mpUrlPropName));
		}
	}

	public Utilities(Logger callerLogger) {
		logger = callerLogger;
	}

    String getJWT(JsonWebToken jwt) {
		String token;
		if ("Bearer".equals(PropagationHelper.getAccessTokenType())) {
			token = PropagationHelper.getIdToken().getAccessToken();
			logger.fine("Retrieved JWT provided through oidcClientConnect feature");
		} else {
			token = jwt.getRawToken();
			logger.fine("Retrieved JWT provided through CDI injected JsonWebToken");
		}
		return token;
	}

	void logToS3(String key, Object document) {
		if (useS3) try {
			if (s3 == null) {
				logger.info("Initializing S3");
				String region = System.getenv("S3_REGION");
				String apiKey = System.getenv("S3_API_KEY");
				String serviceInstanceId = System.getenv("S3_SERVICE_INSTANCE_ID");
				String endpointUrl = System.getenv("S3_ENDPOINT_URL");
				String location = System.getenv("S3_LOCATION");

				AWSCredentials credentials = new BasicIBMOAuthCredentials(apiKey, serviceInstanceId);
				ClientConfiguration clientConfig = new ClientConfiguration().withRequestTimeout(5000).withTcpKeepAlive(true);
				s3 = AmazonS3ClientBuilder.standard()
					.withCredentials(new AWSStaticCredentialsProvider(credentials))
					.withEndpointConfiguration(new EndpointConfiguration(endpointUrl, location))
					.withPathStyleAccessEnabled(true)
					.withClientConfiguration(clientConfig)
					.build();

				s3Bucket = System.getenv("S3_BUCKET");
				if (!s3.doesBucketExistV2(s3Bucket)) {
					logger.info("Creating S3 bucket: "+s3Bucket);
					s3.createBucket(s3Bucket);
				}
			}

			logger.fine("Putting object in S3 bucket for "+key);
			s3.putObject(s3Bucket, key, document.toString());
		} catch (Throwable t) {
			logException(t);
		}
	}

	public void logException(Throwable t) {
		logger.warning(t.getClass().getName()+": "+t.getMessage());

		//only log the stack trace if the level has been set to at least INFO
		if (logger.isLoggable(Level.INFO)) {
			StringWriter writer = new StringWriter();
			t.printStackTrace(new PrintWriter(writer));
			logger.info(writer.toString());
		}
	}
}
