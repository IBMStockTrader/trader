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
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.Iterator;

//CDI 1.2
import javax.inject.Inject;
import javax.enterprise.context.RequestScoped;

//JSON-P 1.0 (JSR 353).  The replaces my old usage of IBM's JSON4J (com.ibm.json.java.JSONObject)
import javax.json.JsonObject;

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
 * Servlet implementation class ViewPortfolio
 */
@WebServlet(description = "View Portfolio servlet", urlPatterns = { "/viewPortfolio" })
@ServletSecurity(@HttpConstraint(rolesAllowed = { "StockTrader", "StockViewer" } ))
@RequestScoped
public class ViewPortfolio extends HttpServlet {
	private static final long serialVersionUID = 4815162342L;
	private static final double ERROR = -1;
	private static final String ERROR_STRING = "Error";
	private static final String FEEDBACK = "Submit Feedback";
	private NumberFormat currency = null;

	private @Inject @RestClient PortfolioClient portfolioClient;
	private @Inject @ConfigProperty(name = "JWT_AUDIENCE") String jwtAudience;
	private @Inject @ConfigProperty(name = "JWT_ISSUER") String jwtIssuer;

	private @Inject JsonWebToken jwt;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public ViewPortfolio() {
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

		//JsonObject portfolio = PortfolioServices.getPortfolio(request, owner);
		JsonObject portfolio = portfolioClient.getPortfolio("Bearer "+jwt.getRawToken(), owner);

		double overallTotal = 0.0;
		String loyaltyLevel = null;
		double balance = 0.0;
		double commissions = 0.0;
		int free = 0;
		String sentiment = null;
		JsonObject stocks = null;

		try {
			overallTotal = portfolio.getJsonNumber("total").doubleValue();
			loyaltyLevel = portfolio.getString("loyalty");
			balance = portfolio.getJsonNumber("balance").doubleValue();
			commissions = portfolio.getJsonNumber("commissions").doubleValue();
			free = portfolio.getInt("free");
			sentiment = portfolio.getString("sentiment");
			stocks = portfolio.getJsonObject("stocks");
		} catch (NullPointerException npe) {
			npe.printStackTrace();
		}

		Writer writer = response.getWriter();
		writer.append("<!DOCTYPE html>");
		writer.append("<html>");
		writer.append("  <head>");
		writer.append("    <title>Stock Trader</title>");
		writer.append("    <meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">");
		writer.append("  </head>");
		writer.append("  <body>");
		writer.append("    <img src=\"header.jpg\" width=\"534\" height=\"200\"/>");
		writer.append("    <br/>");
		writer.append("    <br/>");
		writer.append("    <form method=\"post\"/>");
		writer.append("      Stock Portfolio for <b>"+owner+"</b>: <br/>");
		writer.append("      <br/>");
		writer.append("      <table border=\"1\" cellpadding=\"5\">");
		writer.append("        <tr>");
		writer.append("          <th>Symbol</th>");
		writer.append("          <th>Shares</th>");
		writer.append("          <th>Price</th>");
		writer.append("          <th>Date Quoted</th>");
		writer.append("          <th>Total</th>");
		writer.append("          <th>Commission</th>");
		writer.append("        </tr>");
		writer.append(getTableRows(stocks));
		writer.append("      </table>");
		writer.append("      <br/>");
		writer.append("      <table>");
		writer.append("        <tr>");
		writer.append("          <td>Total Portfolio Value:</td>");
		writer.append("          <td><b>$"+currency.format(overallTotal)+"</b></td>");
		writer.append("        </tr>");
		writer.append("        <tr>");
		writer.append("          <td>Loyalty Level:</td>");
		writer.append("          <td><b>"+loyaltyLevel+"</b></td>");
		writer.append("        </tr>");
		writer.append("        <tr>");
		writer.append("          <td>Account Balance:</td>");
		writer.append("          <td><b>$"+currency.format(balance)+"</b></td>");
		writer.append("        </tr>");
		writer.append("        <tr>");
		writer.append("          <td>Total commissions paid:</td>");
		writer.append("          <td><b>$"+currency.format(commissions)+"</b></td>");
		writer.append("        </tr>");
		writer.append("        <tr>");
		writer.append("          <td>Free Trades Available:</td>");
		writer.append("          <td><b>"+free+"</b></td>");
		writer.append("        </tr>");
		writer.append("        <tr>");
		writer.append("          <td>Sentiment:</td>");
		writer.append("          <td><b>"+sentiment+"</b></td>");
		writer.append("        </tr>");
		writer.append("      </table>");
		writer.append("      <br/>");
		writer.append("      <input type=\"submit\" name=\"submit\" value=\"OK\" style=\"font-family: sans-serif; font-size: 16px;\"/>");
		writer.append("      <input type=\"submit\" name=\"submit\" value=\"Submit Feedback\" style=\"font-family: sans-serif; font-size: 16px;\"/>");
		writer.append("    </form>");
		writer.append("    <br/>");
		writer.append("    <a href=\"https://github.com/IBMStockTrader/\">");
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
			if (submit.equals(FEEDBACK)) {
				response.sendRedirect("feedback?owner="+owner); //send control to the Feedback servlet
			} else {
				response.sendRedirect("summary"); //send control to the Summary servlet
			}
		}
	}

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
	
				rows.append("        <tr>");
				rows.append("          <td>"+symbol+"</td>");
				rows.append("          <td>"+shares+"</td>");
				rows.append("          <td>"+formattedPrice+"</td>");
				rows.append("          <td>"+date+"</td>");
				rows.append("          <td>"+formattedTotal+"</td>");
				rows.append("          <td>"+formattedCommission+"</td>");
				rows.append("        </tr>");
			}
		}

		return rows.toString();
	}
}
