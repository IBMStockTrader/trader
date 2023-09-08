/*
       Copyright 2017-2021 IBM Corp All Rights Reserved

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

import java.io.IOException;
import java.util.logging.Logger;

//CDI 1.2
import javax.inject.Inject;
import javax.enterprise.context.ApplicationScoped;

//Servlet 4.0
import javax.servlet.ServletException;
import javax.servlet.annotation.HttpConstraint;
import javax.servlet.annotation.ServletSecurity;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.RequestDispatcher;

//mpJWT 1.0
import org.eclipse.microprofile.jwt.JsonWebToken;

//mpRestClient 1.0
import org.eclipse.microprofile.rest.client.inject.RestClient;

/**
 * Servlet implementation class AddPortfolio
 */
@WebServlet(description = "Add Portfolio servlet", urlPatterns = { "/addPortfolio" })
@ServletSecurity(@HttpConstraint(rolesAllowed = { "StockTrader" } ))
@ApplicationScoped
public class AddPortfolio extends HttpServlet {
	private static final long serialVersionUID = 4815162342L;
	private static Logger logger = Logger.getLogger(AddPortfolio.class.getName());
	private static Utilities utilities = null;

	private @Inject @RestClient BrokerClient brokerClient;

	private @Inject JsonWebToken jwt;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public AddPortfolio() {
		super();

		if (utilities == null) utilities = new Utilities(logger);
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/WEB-INF/jsps/addPortfolio.jsp");
		dispatcher.forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String owner = request.getParameter("owner");
		double balance = Double.parseDouble(request.getParameter("balance"));
		String currency = request.getParameter("currency");

		if ((owner!=null) && !owner.equals("")) try {
			logger.info("Redirecting to Summary servlet.");

			//PortfolioServices.createPortfolio(request, owner);
			brokerClient.createBroker("Bearer "+utilities.getJWT(jwt, request), owner, balance, currency);

			response.sendRedirect("summary"); //send control to the Summary servlet
		} catch (Throwable t) {
			logger.warning("Error creating portfolio: "+t.getMessage());

			String message = "Error creating portfolio.  Please check the <i>trader</i>, <i>broker</i> and <i>portfolio</i> pod logs for details.";

			//send control to the Display Message servlet
			response.sendRedirect("message?message="+message);
		}
	}
}
