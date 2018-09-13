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
import java.util.logging.Logger;

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
 * Servlet implementation class AddStock
 */
@WebServlet(description = "Add Stock servlet", urlPatterns = { "/addStock" })
@ServletSecurity(@HttpConstraint(rolesAllowed = { "StockTrader" } ))
@RequestScoped
public class AddStock extends HttpServlet {
	private static final long serialVersionUID = 4815162342L;
	private static Logger logger = Logger.getLogger(AddStock.class.getName());
	private NumberFormat currency = null;

	private @Inject @RestClient PortfolioClient portfolioClient;
	private @Inject @ConfigProperty(name = "JWT_AUDIENCE") String jwtAudience;
	private @Inject @ConfigProperty(name = "JWT_ISSUER") String jwtIssuer;

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
		writer.append("      <table>");
		writer.append("        <tr>");
		writer.append("          <td><b>Owner:</b></td>");
		writer.append("          <td>"+owner+"</td>");
		writer.append("        </tr>");
		writer.append("        <tr>");
		writer.append("          <td><b>Commission:</b></td>");
		writer.append("          <td>"+commission+"</td>");
		writer.append("        </tr>");
		writer.append("        <tr>");
		writer.append("          <td><b>Stock Symbol:</b></td>");
		writer.append("          <td><input type=\"text\" name=\"symbol\"></td>");
		writer.append("        </tr>");
		writer.append("        <tr>");
		writer.append("          <td><b>Number of Shares:</b></td>");
		writer.append("          <td><input type=\"text\" name=\"shares\"></td>");
		writer.append("        </tr>");
		writer.append("      </table>");
		writer.append("      <br/>");
		writer.append("      <input type=\"submit\" name=\"submit\" value=\"Submit\" style=\"font-family: sans-serif; font-size: 16px;\"/>");
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
		String symbol = request.getParameter("symbol");
		String shareString = request.getParameter("shares");

		if ((shareString!=null) && !shareString.equals("")) {
			int shares = Integer.parseInt(shareString);
			//PortfolioServices.updatePortfolio(request, owner, symbol, shares);
			portfolioClient.updatePortfolio("Bearer "+jwt.getRawToken(), owner, symbol, shares);
		}

		response.sendRedirect("summary");
	}

	private String getCommission(HttpServletRequest request, String owner) {
		String formattedCommission = "<b>Free!</b>";
		try {
			logger.info("Getting commission");
			//JsonObject portfolio = PortfolioServices.getPortfolio(request, owner);
			JsonObject portfolio = portfolioClient.getPortfolio("Bearer "+jwt.getRawToken(), owner);
			double commission = portfolio.getJsonNumber("nextCommission").doubleValue();
			if (commission!=0.0) formattedCommission = "$"+currency.format(commission);
			logger.info("Got commission: "+formattedCommission);
		} catch (Throwable t) {
			Login.logException(t);
		}
		return formattedCommission;
	}
}
