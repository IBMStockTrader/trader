package com.ibm.hybrid.cloud.sample.portfolio;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

//JSON-P (JSR 353).  The replaces my old usage of IBM's JSON4J (package com.ibm.json.java)
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonStructure;
import javax.servlet.http.HttpServletRequest;

public class PortfolioServices {
//	private static final String PORTFOLIO_SERVICE = "http://localhost:9080/portfolio";
	private static final String PORTFOLIO_SERVICE = "http://portfolio-service:9080/portfolio";

	public static JsonArray getPortfolios() {
		JsonArray portfolios = null;

		try {
			portfolios = (JsonArray) invokeREST("GET", PORTFOLIO_SERVICE);
		} catch (Throwable t) {
			t.printStackTrace();

			//return an empty (but not null) array if anything went wrong
			JsonArrayBuilder builder = Json.createArrayBuilder();
			portfolios = builder.build();
		}

		return portfolios;
	}

	public static JsonObject getPortfolio(String owner) {
		JsonObject portfolio = null;

		try {
			portfolio = (JsonObject) invokeREST("GET", PORTFOLIO_SERVICE+"/"+owner);
		} catch (Throwable t) {
			t.printStackTrace();
		}

		return portfolio;
	}

	public static JsonObject createPortfolio(String owner) {
		JsonObject portfolio = null;

		try {
			portfolio = (JsonObject) invokeREST("POST", PORTFOLIO_SERVICE+"/"+owner);
		} catch (Throwable t) {
			t.printStackTrace();
		}

		return portfolio;
	}

	public static JsonObject updatePortfolio(String owner, String symbol, int shares) {
		JsonObject portfolio = null;

		try {
			String uri = PORTFOLIO_SERVICE+"/"+owner+"?symbol="+symbol+"&shares="+shares;
			portfolio = (JsonObject) invokeREST("PUT", uri);
		} catch (Throwable t) {
			t.printStackTrace();
		}

		return portfolio;
	}

	public static JsonObject deletePortfolio(String owner) {
		JsonObject portfolio = null;

		try {
			portfolio = (JsonObject) invokeREST("DELETE", PORTFOLIO_SERVICE+"/"+owner);
		} catch (Throwable t) {
			t.printStackTrace();
		}

		return portfolio;
	}

	private static JsonStructure invokeREST(String verb, String uri) throws IOException {
		URL url = new URL(uri);

		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod(verb);
		conn.setRequestProperty("Content-Type", "application/json");
		conn.setDoOutput(true);
		InputStream stream = conn.getInputStream();

//		JSONObject json = JSONObject.parse(stream); //JSON4J
		JsonStructure json = Json.createReader(stream).read();

		stream.close();

		return json; //I use JsonStructure here so I can return a JsonObject or a JsonArray
	}

	// Something bizarre is happening in both minikube and in CFC, where the http URL
	// is getting changed to an https one, but it still contains port 80 rather than 443.
	// This doesn't happen when running outside of Kube, or when running in Armada.
	// Might be an artifact of the Ingress proxy (my free Armada account doesn't support
	// Ingress, so I have to use a worker's nodePort instead).
	// TODO: Implementing an ugly hack for now; need to revisit (or open bug report on Ingress)
	static String getRedirectWorkaround(HttpServletRequest request) {
		String workaroundURL = "";

		String requestURL = request.getRequestURL().toString();
		System.out.println(requestURL);
		if (requestURL.startsWith("https:") && requestURL.contains(":80/")) {
			//we got redirected from http to https, but the port number didn't get updated!
			workaroundURL = requestURL.replace(":80/", ":443/");

			//strip off the current servlet path - caller will append new path
			workaroundURL = workaroundURL.replace(request.getServletPath(), "/");

			System.out.println("Correcting "+requestURL+" to "+workaroundURL);
		}
		return workaroundURL;
	}
}
