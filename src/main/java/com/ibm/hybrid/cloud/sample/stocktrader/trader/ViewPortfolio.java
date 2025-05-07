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
import com.ibm.hybrid.cloud.sample.stocktrader.trader.json.Broker;

import java.io.IOException;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.Iterator;
import java.util.logging.Logger;

//CDI 1.2
import io.opentelemetry.instrumentation.annotations.WithSpan;
import jakarta.inject.Inject;
import jakarta.enterprise.context.ApplicationScoped;

//JSON-P 1.0 (JSR 353).  The replaces my old usage of IBM's JSON4J (com.ibm.json.java.JSONObject)
import jakarta.json.JsonObject;

//Servlet 3.1
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
 * Servlet implementation class ViewPortfolio
 */
@WebServlet(description = "View Portfolio servlet", urlPatterns = { "/viewPortfolio" })
@ServletSecurity(@HttpConstraint(rolesAllowed = { "StockTrader", "StockViewer" } ))
@ApplicationScoped
public class ViewPortfolio extends HttpServlet {
	private static final long serialVersionUID = 4815162342L;
	private static final double ERROR = -1;
	private static final String ERROR_STRING = "Error";
	private static final String TRADE_STOCK = "Buy/Sell Stock";
	private static final String FEEDBACK = "Submit Feedback";

	private static Logger logger = Logger.getLogger(ViewPortfolio.class.getName());

	private static Utilities utilities = null;
	private static NumberFormat currency = null;

	private @Inject @RestClient BrokerClient brokerClient;

	private @Inject JsonWebToken jwt;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public ViewPortfolio() {
		super();

		if (utilities == null) {
			currency = NumberFormat.getNumberInstance();
			currency.setMinimumFractionDigits(2);
			currency.setMaximumFractionDigits(2);
			currency.setRoundingMode(RoundingMode.HALF_UP);

			utilities = new Utilities(logger);
		}
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	@WithSpan
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String owner = request.getParameter("owner");

		//JsonObject portfolio = PortfolioServices.getPortfolio(request, owner);
		Broker broker = brokerClient.getBroker("Bearer "+utilities.getJWT(jwt, request), owner);

		JsonObject stocks = null;
		String returnOnInvestment = "Unknown";

		try {
			stocks = broker.getStocks();
			//request.setAttribute("rows", getTableRows(stocks));			
			request.setAttribute("broker", broker);
		} catch (NullPointerException npe) {
			utilities.logException(npe);
		}

		try {
			returnOnInvestment = brokerClient.getReturnOnInvestment("Bearer "+utilities.getJWT(jwt, request), owner);
			request.setAttribute("returnOnInvestment", returnOnInvestment);
		} catch (Throwable t) {
			logger.info("Unable to obtain return on investment for "+owner);
			utilities.logException(t);
		}

		RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/WEB-INF/jsps/viewPortfolio.jsp");
        dispatcher.forward(request, response);			
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	@WithSpan
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String owner = request.getParameter("owner");
		String submit = request.getParameter("submit");

		if (submit != null) {
			if (submit.equals(TRADE_STOCK)) {
				response.sendRedirect("addStock?owner="+owner+"&source=viewPortfolio"); //send control to the AddStock servlet
			} else if (submit.equals(FEEDBACK)) {
				response.sendRedirect("feedback?owner="+owner); //send control to the Feedback servlet
			} else {
				response.sendRedirect("summary"); //send control to the Summary servlet
			}
		}
	}

	@WithSpan
	private String getTableRows(JsonObject stocks) {
		StringBuffer rows = new StringBuffer();

		if (stocks != null) {
			Iterator<String> keys = stocks.keySet().iterator();

			while (keys.hasNext()) {
				String key = keys.next();
				JsonObject stock = stocks.getJsonObject(key);

				String symbol = stock.getString("symbol");
				int shares = stock.getInt("shares");
				double price = stock.getJsonNumber("price").doubleValue();
				String date = stock.getString("date");
				double total = stock.getJsonNumber("total").doubleValue();
				double commission = stock.getJsonNumber("commission").doubleValue();

				String formattedPrice = "$"+currency.format(price);
				String formattedTotal = "$"+currency.format(total);
				String formattedCommission = "$"+currency.format(commission);

				if (price == ERROR) {
					formattedPrice = ERROR_STRING;
					formattedTotal = ERROR_STRING;
					formattedCommission = ERROR_STRING;
				}


			}
		}

		return rows.toString();
	}
}
