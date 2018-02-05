/*
       Copyright 2017 IBM Corp All Rights Reserved

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

package com.ibm.hybrid.cloud.sample.portfolio;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Enumeration;

//JSON-P (JSR 353).  The replaces my old usage of IBM's JSON4J (package com.ibm.json.java)
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonStructure;
import javax.servlet.http.HttpServletRequest;
import com.ibm.websphere.security.jwt.InvalidBuilderException;
import com.ibm.websphere.security.jwt.JwtBuilder;
import com.ibm.websphere.security.jwt.JwtToken;

public class PortfolioServices {
	private static final String PORTFOLIO_SERVICE = "http://portfolio-service:9080/portfolio";

	public static JsonArray getPortfolios(HttpServletRequest request) {
		JsonArray portfolios = null;

		try {
			portfolios = (JsonArray) invokeREST(request, "GET", PORTFOLIO_SERVICE);
		} catch (Throwable t) {
			t.printStackTrace();

			//return an empty (but not null) array if anything went wrong
			JsonArrayBuilder builder = Json.createArrayBuilder();
			portfolios = builder.build();
		}

		return portfolios;
	}

	public static JsonObject getPortfolio(HttpServletRequest request, String owner) {
		JsonObject portfolio = null;

		try {
			portfolio = (JsonObject) invokeREST(request, "GET", PORTFOLIO_SERVICE+"/"+owner);
		} catch (Throwable t) {
			t.printStackTrace();
		}

		return portfolio;
	}

	public static JsonObject createPortfolio(HttpServletRequest request, String owner) {
		JsonObject portfolio = null;

		try {
			portfolio = (JsonObject) invokeREST(request, "POST", PORTFOLIO_SERVICE+"/"+owner);
		} catch (Throwable t) {
			t.printStackTrace();
		}

		return portfolio;
	}

	public static JsonObject updatePortfolio(HttpServletRequest request, String owner, String symbol, int shares) {
		JsonObject portfolio = null;

		try {
			String uri = PORTFOLIO_SERVICE+"/"+owner+"?symbol="+symbol+"&shares="+shares;
			portfolio = (JsonObject) invokeREST(request, "PUT", uri);
		} catch (Throwable t) {
			t.printStackTrace();
		}

		return portfolio;
	}

	public static JsonObject deletePortfolio(HttpServletRequest request, String owner) {
		JsonObject portfolio = null;

		try {
			portfolio = (JsonObject) invokeREST(request, "DELETE", PORTFOLIO_SERVICE+"/"+owner);
		} catch (Throwable t) {
			t.printStackTrace();
		}

		return portfolio;
	}

	private static JsonStructure invokeREST(HttpServletRequest request, String verb, String uri) throws IOException {
    
        String jwtToken = createJWT(request);
		URL url = new URL(uri);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();

		copyFromRequest(conn, request); //forward headers (including cookies) from inbound request

		conn.setRequestMethod(verb);
		conn.setRequestProperty("Content-Type", "application/json");
        // add the JWT token to the authorization header. 
        conn.setRequestProperty("Authorization", "Bearer "+ jwtToken);
		conn.setDoOutput(true);
		InputStream stream = conn.getInputStream();

//		JSONObject json = JSONObject.parse(stream); //JSON4J
		JsonStructure json = Json.createReader(stream).read();

		stream.close();

		return json; //I use JsonStructure here so I can return a JsonObject or a JsonArray
	}
    
    /**
    * Create Json Web Token.
    * return: the base64 encoded and signed token. 
    */
    private static String createJWT(HttpServletRequest request){
        String jwtTokenString = null;		
		try {
            String userName = request.getUserPrincipal().getName();
            System.out.println("*** debug: userName: "+ userName);
			if (userName == null) userName = "null";
			// Put the user info into a JWT Token
			// create() uses default settings.  
			// For other settings, specify a JWTBuilder element in server.xml
			// and call create(builder id)
			JwtBuilder builder = com.ibm.websphere.security.jwt.JwtBuilder.create();
			builder.subject(userName);
			builder.claim("upn", userName);
			//builder.claim("groups", groups);
            //convention is the issuer is the url, but for demo portability a fixed value is used.
			//builder.claim("iss", request.getRequestURL().toString());
            builder.claim("iss", "http://jwtdemo.ibm.com");
			JwtToken theToken = builder.buildJwt();			
			jwtTokenString = theToken.compact();
		} catch (Exception e) {			
			e.printStackTrace();
			throw new RuntimeException(e);
		}
        System.out.println("*** debug: JWT: "+ jwtTokenString);
        return jwtTokenString;
    }
    
   

	//forward headers (including cookies) from inbound request
	private static void copyFromRequest(HttpURLConnection conn, HttpServletRequest request) {
		Enumeration<String> headers = request.getHeaderNames();
		if (headers != null) {
			while (headers.hasMoreElements()) {
				String headerName = headers.nextElement(); //"Authorization" and "Cookie" are especially important headers
				String headerValue = request.getHeader(headerName);
				conn.setRequestProperty(headerName, headerValue); //odd it's called request property here, rather than header...
			}
		}
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
