/*
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
//Stopped doing this - see https://github.com/OpenLiberty/open-liberty/issues/11225
//import com.ibm.websphere.security.openidconnect.PropagationHelper;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.security.Principal;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Base64;
import java.util.HashMap;

//Servlet 4.0
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

//mpJWT 1.0
import org.eclipse.microprofile.jwt.JsonWebToken;


public class Utilities {
	private static Logger logger = Logger.getLogger(Utilities.class.getName());

	public static boolean useBasicAuth = false;
	public static boolean useOIDC = false;

	private static HashMap<String,String> basicAuthCredentials = new HashMap<String,String>();

	private static String whiteLabelHeaderImage = "header.jpg";
	private static String whiteLabelFooterImage = "footer.jpg";
	private static String whiteLabelLoginMessage = "Login to <span class=\"brand-main\">Stock</span><span class=\"brand-accent\">Trader</span>";

	private static boolean useS3 = false;
	private static boolean sentimentEnabled = false;
	private static String sentimentDashboardUrl = null;
	private static AmazonS3 s3 = null;
	private static String s3Bucket = null;

	private static final String JWT  = "jwt";
	private static final String OIDC = "oidc";
	private static final String BASIC_AUTH = "none";


	// Override Broker Client URL if config map is configured to provide URL
	static {
		String authType = System.getenv("AUTH_TYPE");
		logger.info("authType: "+authType);
		useOIDC = OIDC.equals(authType);
		useBasicAuth = BASIC_AUTH.equals(authType);

		String headerImageFromEnv = System.getenv("WHITE_LABEL_HEADER_IMAGE");
		if (headerImageFromEnv != null) whiteLabelHeaderImage = headerImageFromEnv;
		String footerImageFromEnv = System.getenv("WHITE_LABEL_FOOTER_IMAGE");
		if (footerImageFromEnv != null) whiteLabelFooterImage = footerImageFromEnv;
		String loginMessageFromEnv = System.getenv("WHITE_LABEL_LOGIN_MESSAGE");
		if (loginMessageFromEnv != null) whiteLabelLoginMessage = loginMessageFromEnv;

		useS3 = Boolean.parseBoolean(System.getenv("S3_ENABLED"));
		logger.info("useS3: "+useS3);

		sentimentEnabled = Boolean.parseBoolean(System.getenv("SENTIMENT_ENABLED"));
		logger.info("Sentiment enabled: " + sentimentEnabled);
		
		String sentimentDashboardUrlFromEnv = System.getenv("SENTIMENT_DASHBOARD_URL");
		if (sentimentDashboardUrlFromEnv != null && !sentimentDashboardUrlFromEnv.isEmpty()) {
			sentimentDashboardUrl = sentimentDashboardUrlFromEnv;
			logger.info("Sentiment dashboard URL: " + sentimentDashboardUrl);
		}

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

	void addBasicAuthCredentials(String user, String password) {
        String credentials = user + ":" + password;
        String authString = "Basic " + Base64.getEncoder().encodeToString(credentials.getBytes());
		basicAuthCredentials.put(user, authString);
	}

	String getAuthHeader(JsonWebToken jwt, HttpServletRequest request) {
		String authHeader = null;
		if (useBasicAuth) {
			Principal principal = request.getUserPrincipal();
			if (principal!=null) {
				String user = principal.getName();
				authHeader = basicAuthCredentials.get(user);
				if (authHeader==null) logger.warning("No credentials found for user: "+user);
			} else {
				logger.warning("No identity associated with request!");
			}
		} else {
			authHeader = "Bearer "+getJWT(jwt, request);
		}
		logger.fine("authHeader = "+authHeader);
		return authHeader;
	}

	String getJWT(JsonWebToken jwt, HttpServletRequest request) {
		String token = null;

//  The below gets the JWT issued by Liberty itself (via the jwtSSO feature), not the JWT from your OIDC provider (such as KeyCloak)
//  See https://github.com/OpenLiberty/open-liberty/issues/11225 for details
//		if ("Bearer".equals(PropagationHelper.getAccessTokenType())) {
//			token = PropagationHelper.getIdToken().getAccessToken();
//			logger.fine("Retrieved JWT provided through oidcClientConnect feature");
		if (useOIDC) {
			HttpSession session = request.getSession(); //When multiple Trader pods exist, need to enable distributed session support
			if (session!=null) {
				token = (String) session.getAttribute(JWT); //Summary.doGet puts this here when redirected to after KeyCloak login
				if (token!= null) {
					logger.fine("Retrieved JWT from the session");
				} else {
					logger.warning("Unable to retrieve JWT from the session");
				}
			} else {
				logger.warning("Session was null");
			}
		} else {
			token = jwt.getRawToken();
			logger.fine("Retrieved JWT provided through CDI injected JsonWebToken");
		}
		return token;
	}

	public static String getHeaderImage() {
		return whiteLabelHeaderImage;
	}

	public static String getFooterImage() {
		return whiteLabelFooterImage;
	}

	public static String getLoginMessage() {
		return whiteLabelLoginMessage;
	}

	public static boolean getSentimentEnabled() {
		return sentimentEnabled;
	}

	public static String getSentimentDashboardUrl() {
		return sentimentDashboardUrl;
	}

	public static void logToS3(String key, Object document) {
		if (useS3) try {
			if (s3 == null) {
				logger.info("Initializing S3");
				//Using System.getenv because can't use CDI injection of ConfigProperty from a static method
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

	public static void logException(Throwable t) {
		logger.warning(t.getClass().getName()+": "+t.getMessage());

		//only log the stack trace if the level has been set to at least INFO
		if (logger.isLoggable(Level.INFO)) {
			StringWriter writer = new StringWriter();
			t.printStackTrace(new PrintWriter(writer));
			logger.info(writer.toString());
		}
	}
}
