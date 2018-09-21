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
//JSR 47 Logging
import java.util.logging.Logger;

//CDI 1.2
import javax.inject.Inject;
import javax.enterprise.context.RequestScoped;

//JSON-P 1.0
import javax.json.JsonArray;
import javax.json.JsonObject;

//Servlet 3.1
import javax.servlet.ServletException;
import javax.servlet.annotation.HttpConstraint;
import javax.servlet.annotation.ServletSecurity;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

//mpConfig 1.2
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.jwt.JsonWebToken;
//mpRestClient 1.0
import org.eclipse.microprofile.rest.client.inject.RestClient;


/**
 * Servlet implementation class Summary
 */
@WebServlet(description = "Portfolio summary servlet", urlPatterns = { "/summary" })
@ServletSecurity(@HttpConstraint(rolesAllowed = { "StockTrader", "StockViewer" } ))
@RequestScoped
public class Summary extends HttpServlet {
	private static final long serialVersionUID = 4815162342L;
	private static final String EDITOR   = "StockTrader";
	private static final String LOGOUT   = "Log Out";
	private static final String CREATE   = "create";
	private static final String RETRIEVE = "retrieve";
	private static final String UPDATE   = "update";
	private static final String DELETE   = "delete";
	private static Logger logger = Logger.getLogger(Summary.class.getName());
	private NumberFormat currency = null;

	private @Inject @RestClient PortfolioClient portfolioClient;
	private @Inject @ConfigProperty(name = "JWT_AUDIENCE") String jwtAudience;
	private @Inject @ConfigProperty(name = "JWT_ISSUER") String jwtIssuer;

	private @Inject JsonWebToken jwt;

    /**
     * @see HttpServlet#HttpServlet()
     */
	public Summary() {
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
		boolean editor = request.isUserInRole(EDITOR);
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
		if (editor) {
			writer.append("      <input type=\"radio\" name=\"action\" value=\""+CREATE+"\"> Create a new portfolio<br>");
		}
			writer.append("      <input type=\"radio\" name=\"action\" value=\""+RETRIEVE+"\" checked> Retrieve selected portfolio<br>");
		if (editor) {
			writer.append("      <input type=\"radio\" name=\"action\" value=\""+UPDATE+"\"> Update selected portfolio (add stock)<br>");
			writer.append("      <input type=\"radio\" name=\"action\" value=\""+DELETE+"\"> Delete selected portfolio<br>");
		}
		writer.append("      <br/>");
		writer.append("      <table border=\"1\" cellpadding=\"5\">");
		writer.append("        <tr>");
		writer.append("          <th></th>");
		writer.append("          <th>Owner</th>");
		writer.append("          <th>Total</th>");
		writer.append("          <th>Loyalty Level</th>");
		writer.append("        </tr>");
		writer.append(getTableRows(request));
		writer.append("      </table>");
		writer.append("      <br/>");
		writer.append("      <input type=\"submit\" name=\"submit\" value=\"Submit\" style=\"font-family: sans-serif; font-size: 16px;\"/>");
		writer.append("      <input type=\"submit\" name=\"submit\" value=\"Log Out\" style=\"font-family: sans-serif; font-size: 16px;\"/>");
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
		String submit = request.getParameter("submit");

		if (submit != null) {
			if (submit.equals(LOGOUT)) {
				request.logout();

				HttpSession session = request.getSession();
				if (session != null) session.invalidate();

				response.sendRedirect("login");
			} else {
				String action = request.getParameter("action");
				String owner = request.getParameter("owner");

				if (action != null) {

					if (action.equals(CREATE)) {
						response.sendRedirect("addPortfolio"); //send control to the AddPortfolio servlet
					} else if (action.equals(RETRIEVE)) {
						response.sendRedirect("viewPortfolio?owner="+owner); //send control to the ViewPortfolio servlet
					} else if (action.equals(UPDATE)) {
						response.sendRedirect("addStock?owner="+owner); //send control to the AddStock servlet
					} else if (action.equals(DELETE)) {
//						PortfolioServices.deletePortfolio(request, owner);
						portfolioClient.deletePortfolio("Bearer "+jwt.getRawToken(), owner);
						doGet(request, response); //refresh the Summary servlet
					} else {
						doGet(request, response); //something went wrong - just refresh the Summary servlet
					}
				} else {
					doGet(request, response); //something went wrong - just refresh the Summary servlet
				}
			}
		} else {
			doGet(request, response); //something went wrong - just refresh the Summary servlet
		}
	}

	private String getTableRows(HttpServletRequest request) {
		StringBuffer rows = new StringBuffer();

		if (portfolioClient==null) {
			logger.warning("Injection of PortfolioClient failed!");
		}

		if (jwtAudience==null) {
			logger.warning("Injection of ConfigProperty JWT_AUDIENCE failed!");
		}

//		JsonArray portfolios = PortfolioServices.getPortfolios(request);
		JsonArray portfolios = portfolioClient.getPortfolios("Bearer "+jwt.getRawToken());

		for (int index=0; index<portfolios.size(); index++) {
			JsonObject portfolio = (JsonObject) portfolios.get(index);

			String owner = portfolio.getString("owner");
			double total = portfolio.getJsonNumber("total").doubleValue();
			String loyaltyLevel = portfolio.getString("loyalty");

			rows.append("        <tr>");
			rows.append("          <td><input type=\"radio\" name=\"owner\" value=\""+owner+"\"");
			if (index == 0) {
				rows.append(" checked");
			}
			rows.append("></td>");

			rows.append("          <td>"+owner+"</td>");
			rows.append("          <td>$"+currency.format(total)+"</td>");
			rows.append("          <td>"+loyaltyLevel+"</td>");
			rows.append("        </tr>");
		}

		return rows.toString();
	}
}
