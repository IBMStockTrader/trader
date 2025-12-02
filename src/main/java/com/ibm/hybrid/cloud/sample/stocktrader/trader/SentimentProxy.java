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
import com.ibm.hybrid.cloud.sample.stocktrader.trader.json.Sentiment;

import java.io.IOException;
import java.util.logging.Logger;

import jakarta.inject.Inject;
import jakarta.servlet.annotation.HttpConstraint;
import jakarta.servlet.annotation.ServletSecurity;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

//mpJWT 1.0
import org.eclipse.microprofile.jwt.JsonWebToken;

//mpRestClient 1.0
import org.eclipse.microprofile.rest.client.inject.RestClient;

/**
 * Servlet implementation class SentimentProxy
 * 
 * Proxies sentiment API calls from the browser to the broker service.
 * The broker service then calls the sentiment microservice.
 * Flow: Browser → Trader (this servlet) → Broker → Sentiment API
 */
@WebServlet(description = "Sentiment API Proxy servlet via Broker", urlPatterns = { "/sentiment/*" })
@ServletSecurity(@HttpConstraint(rolesAllowed = { "StockTrader" }))
public class SentimentProxy extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(SentimentProxy.class.getName());

	private static Utilities utilities = null;

	private @Inject @RestClient BrokerClient brokerClient;
	private @Inject JsonWebToken jwt;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public SentimentProxy() {
		super();

		if (utilities == null) {
			utilities = new Utilities(logger);
		}
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		// Extract symbol from path: /trader/sentiment/AAPL -> AAPL
		String pathInfo = request.getPathInfo();
		if (pathInfo == null || pathInfo.length() <= 1) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.setContentType("application/json");
			response.getWriter().write("{\"error\":\"Stock symbol is required\"}");
			return;
		}

		// Remove leading slash
		String symbol = pathInfo.substring(1);

		logger.fine("Proxying sentiment request for " + symbol + " via broker service");

		try {
			// Call broker service to get sentiment (broker will call sentiment API)
			Sentiment sentiment = brokerClient.getSentiment(
				utilities.getAuthHeader(jwt, request), 
				symbol
			);

			// Set CORS headers to allow browser access
			response.setHeader("Access-Control-Allow-Origin", "*");
			response.setHeader("Access-Control-Allow-Methods", "GET, OPTIONS");
			response.setHeader("Access-Control-Allow-Headers", "Content-Type");

			response.setContentType("application/json");

			if (sentiment != null) {
				// Convert Sentiment object to JSON
				response.setStatus(HttpServletResponse.SC_OK);
				String dominantSentiment = sentiment.getDominantSentiment();
				int sourcesAnalyzed = sentiment.getSourcesAnalyzed();
				logger.info("SentimentProxy: Received sentiment for " + symbol + 
					" - dominant: " + dominantSentiment + 
					", sources: " + sourcesAnalyzed +
					", positive: " + sentiment.getPositive() +
					", negative: " + sentiment.getNegative() +
					", neutral: " + sentiment.getNeutral());
				response.getWriter().write("{" +
					"\"symbol\":\"" + sentiment.getSymbol() + "\"," +
					"\"positive\":" + sentiment.getPositive() + "," +
					"\"negative\":" + sentiment.getNegative() + "," +
					"\"neutral\":" + sentiment.getNeutral() + "," +
					"\"net_sentiment\":" + sentiment.getNetSentiment() + "," +
					"\"dominant_sentiment\":\"" + (dominantSentiment != null ? dominantSentiment : "null") + "\"," +
					"\"timestamp\":\"" + (sentiment.getTimestamp() != null ? sentiment.getTimestamp() : "") + "\"," +
					"\"sources_analyzed\":" + sourcesAnalyzed +
					"}");
				logger.fine("Successfully proxied sentiment response for " + symbol);
			} else {
				// Sentiment service not available or returned null
				response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
				response.getWriter().write("{\"error\":\"Sentiment service is not available\"}");
				logger.warning("Sentiment service returned null for " + symbol);
			}
		} catch (Exception e) {
			logger.warning("Error proxying sentiment request for " + symbol + ": " + e.getMessage());
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.setContentType("application/json");
			response.getWriter().write("{\"error\":\"Failed to fetch sentiment: " + e.getMessage() + "\"}");
		}
	}

	/**
	 * Handle OPTIONS request for CORS preflight
	 */
	protected void doOptions(HttpServletRequest request, HttpServletResponse response) {
		response.setHeader("Access-Control-Allow-Origin", "*");
		response.setHeader("Access-Control-Allow-Methods", "GET, OPTIONS");
		response.setHeader("Access-Control-Allow-Headers", "Content-Type");
		response.setStatus(HttpServletResponse.SC_OK);
	}
}

