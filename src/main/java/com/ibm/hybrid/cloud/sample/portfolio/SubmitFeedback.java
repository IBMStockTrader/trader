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
import java.io.Writer;
import java.util.logging.Logger;
import java.net.URLEncoder;

//CDI 1.2
import javax.inject.Inject;
import javax.enterprise.context.RequestScoped;

//JSON-P 1.0 (JSR 353).  The replaces my old usage of IBM's JSON4J (com.ibm.json.java.JSONObject)
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

//Servlet 3.1
import javax.servlet.ServletException;
import javax.servlet.annotation.HttpConstraint;
import javax.servlet.annotation.ServletSecurity;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

//mpConfig 1.2
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.jwt.JsonWebToken;
//mpRestClient 1.0
import org.eclipse.microprofile.rest.client.inject.RestClient;

/**
 * Servlet implementation class SubmitFeedback
 */
@WebServlet(description = "Submit Feedback servlet", urlPatterns = { "/feedback" })
@ServletSecurity(@HttpConstraint(rolesAllowed = { "StockTrader" } ))
@RequestScoped
public class SubmitFeedback extends HttpServlet {
	private static final long serialVersionUID = 4815162342L;
	private static final String SUBMIT = "Submit";
	private static Logger logger = Logger.getLogger(SubmitFeedback.class.getName());

	private @Inject @RestClient PortfolioClient portfolioClient;
	private @Inject @ConfigProperty(name = "JWT_AUDIENCE") String jwtAudience;
	private @Inject @ConfigProperty(name = "JWT_ISSUER") String jwtIssuer;

	private @Inject JsonWebToken jwt;

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Writer writer = response.getWriter();
		writer.append("<!DOCTYPE html>");
		writer.append("<html>");
		writer.append("  <head>");
		writer.append("    <title>Stock Trader</title>");
		writer.append("    <meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" />");
		writer.append("  </head>");
		writer.append("  <body>");
		writer.append("    <img src=\"header.jpg\" width=\"534\" height=\"200\"/>");
		writer.append("    <p/>");
		writer.append("    <i>Please share your feedback on this tool!</i>");
		writer.append("    <p/>");
		writer.append("    <form method=\"post\"/>");
		writer.append("      <textarea name=\"feedback\" rows=\"10\" cols=\"70\"></textarea>");
		writer.append("      <p/>");
		writer.append("      <input type=\"submit\" name=\"submit\" value=\"Submit\" style=\"font-family: sans-serif; font-size: 16px;\" />");
		writer.append("      <input type=\"submit\" name=\"submit\" value=\"Cancel\" style=\"font-family: sans-serif; font-size: 16px;\" />");
		writer.append("    </form>");
		writer.append("    <br/>");
		writer.append("    <a href=\"https://github.com/IBMStockTrader\">");
		writer.append("      <img src=\"footer.jpg\"/>");
		writer.append("    </a>");
		writer.append("  </body>");
		writer.append("</html>");
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String owner = request.getParameter("owner");
		String submit = request.getParameter("submit");

		if (submit != null) {
			if (submit.equals(SUBMIT)) {
				String feedback = request.getParameter("feedback");
				if ((feedback!=null) && !feedback.equals("") && (owner!=null) && !owner.equals("")) {
					JsonObjectBuilder builder = Json.createObjectBuilder();
					builder.add("text", feedback);
					JsonObject text = builder.build();

					logger.info("Calling portfolio/"+owner+"/feedback with following JSON: "+text.toString());
					//JsonObject result = PortfolioServices.submitFeedback(request, owner, text);
					JsonObject result = portfolioClient.submitFeedback("Bearer "+jwt.getRawToken(), owner, text);

					logger.info("portfolio/"+owner+"/feedback returned the following JSON: "+result!=null ? result.toString() : "null");
					String message = result!=null ? result.getString("message") : "null";
					String encodedMessage = URLEncoder.encode(message, "UTF-8");
					response.sendRedirect("message?owner="+owner+"&message="+encodedMessage); //send control to the DisplayMessage servlet
				} else {
					logger.info("No feedback submitted");
					response.sendRedirect("viewPortfolio?owner="+owner); //send control to the ViewPortfolio servlet
				}
			} else { //they hit Cancel instead
				logger.info("Feedback submission canceled");
				response.sendRedirect("viewPortfolio?owner="+owner); //send control to the ViewPortfolio servlet
			}
		}
	}
}
