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
import com.ibm.hybrid.cloud.sample.stocktrader.trader.json.Broker;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

//CDI 1.2
import javax.inject.Inject;
import javax.enterprise.context.ApplicationScoped;

//Servlet 3.1
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
import com.ibm.websphere.security.openidconnect.PropagationHelper;

//mpRestClient 1.0
import org.eclipse.microprofile.rest.client.inject.RestClient;

/**
 * Servlet implementation class AddStock
 */
@WebServlet(description = "Add Stock servlet", urlPatterns = { "/addStock" })
@ServletSecurity(@HttpConstraint(rolesAllowed = { "StockTrader" } ))
@ApplicationScoped
public class AddStock extends HttpServlet {
	private static final long serialVersionUID = 4815162342L;
	private static final String BUY = "Buy";
	private static final String SELL = "Sell";
	private static final String SUBMIT = "Submit";

	private static Logger logger = Logger.getLogger(AddStock.class.getName());

	private NumberFormat currency = null;

	private @Inject @RestClient BrokerClient brokerClient;

	private @Inject JsonWebToken jwt;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public AddStock() {
		super();

		currency = NumberFormat.getNumberInstance();
		currency.setMinimumFractionDigits(2);
		currency.setMaximumFractionDigits(2);
		currency.setRoundingMode(RoundingMode.HALF_UP);
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String owner = request.getParameter("owner");

		String commission = getCommission(request, owner);

		request.setAttribute("commission", commission);

		RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/WEB-INF/jsps/addStock.jsp");
        dispatcher.forward(request, response);		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String owner = request.getParameter("owner");
		String symbol = request.getParameter("symbol");
		String shareString = request.getParameter("shares");
		String action = request.getParameter("action");
		String source = request.getParameter("source");
		if (source == null) source = "summary";

		String submit = request.getParameter("submit");
		if ((submit!=null) && submit.equals(SUBMIT)) { //don't do if they chose Cancel
			if ((shareString!=null) && !shareString.equals("")) {
				int shares = Integer.parseInt(shareString);
				if (action.equalsIgnoreCase(SELL)) shares *= -1; //selling means buying a negative number of shares

				//PortfolioServices.updatePortfolio(request, owner, symbol, shares);
				brokerClient.updateBroker("Bearer "+getJWT(), owner, symbol, shares);
			}
		}

		if (source.equalsIgnoreCase("viewPortfolio")) source += "?owner="+owner;
		response.sendRedirect(source);
	}

	private String getCommission(HttpServletRequest request, String owner) {
		String formattedCommission = "<b>Free!</b>";
		try {
			logger.info("Getting commission");
			//JsonObject portfolio = PortfolioServices.getPortfolio(request, owner);
			Broker broker = brokerClient.getBroker("Bearer "+getJWT(), owner);
			double commission = broker.getNextCommission();
			if (commission!=0.0) formattedCommission = "$"+currency.format(commission);
			logger.info("Got commission: "+formattedCommission);
		} catch (Throwable t) {
			logException(t);
		}
		return formattedCommission;
	}

	private void logException(Throwable t) {
		logger.warning(t.getClass().getName()+": "+t.getMessage());

		//only log the stack trace if the level has been set to at least FINE
		if (logger.isLoggable(Level.FINE)) {
			StringWriter writer = new StringWriter();
			t.printStackTrace(new PrintWriter(writer));
			logger.fine(writer.toString());
		}
	}

	private String getJWT() {
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
}
