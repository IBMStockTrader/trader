/*
       Copyright 2017-2021 IBM Corp All Rights Reserved
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

package com.ibm.hybrid.cloud.sample.stocktrader.trader;

import com.ibm.hybrid.cloud.sample.stocktrader.trader.client.BrokerClient;
import com.ibm.hybrid.cloud.sample.stocktrader.trader.json.Feedback;
import com.ibm.hybrid.cloud.sample.stocktrader.trader.json.Stock;
import com.ibm.hybrid.cloud.sample.stocktrader.trader.json.WatsonInput;

import java.io.IOException;
import java.util.logging.Logger;
import java.net.URLEncoder;

//CDI 1.2
import jakarta.inject.Inject;
import jakarta.enterprise.context.ApplicationScoped;

//JSON-P 1.0 (JSR 353).  The replaces my old usage of IBM's JSON4J (com.ibm.json.java.JSONObject)
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;

//Servlet 4.0
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.HttpConstraint;
import jakarta.servlet.annotation.ServletSecurity;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.RequestDispatcher;

//mpJWT 1.0
import org.eclipse.microprofile.jwt.JsonWebToken;

//mpRestClient 1.0
import org.eclipse.microprofile.rest.client.inject.RestClient;

/**
 * Servlet implementation class SubmitFeedback
 */
@WebServlet(description = "Submit Feedback servlet", urlPatterns = { "/feedback" })
@ServletSecurity(@HttpConstraint(rolesAllowed = { "StockTrader" } ))
@ApplicationScoped
public class SubmitFeedback extends HttpServlet {
	private static final long serialVersionUID = 4815162342L;
	private static final String SUBMIT = "Submit";
	private static Logger logger = Logger.getLogger(SubmitFeedback.class.getName());
	private static Utilities utilities = null;

	private @Inject @RestClient BrokerClient brokerClient;

	private @Inject JsonWebToken jwt;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public SubmitFeedback() {
		super();

		if (utilities == null) utilities = new Utilities(logger);
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/WEB-INF/jsps/submitFeedback.jsp");
        dispatcher.forward(request, response);		
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
					WatsonInput input = new WatsonInput(feedback);

					logger.info("Calling broker/"+owner+"/feedback with following JSON: "+input.toString());
					//JsonObject result = PortfolioServices.submitFeedback(request, owner, text);
					Feedback result = brokerClient.submitFeedback("Bearer "+utilities.getJWT(jwt, request), owner, input);

					logger.info("broker/"+owner+"/feedback returned the following JSON: "+result!=null ? result.toString() : "null");
					String message = result!=null ? result.getMessage() : "null";
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
