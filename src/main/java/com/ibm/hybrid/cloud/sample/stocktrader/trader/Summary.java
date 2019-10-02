/*
       Copyright 2017-2019 IBM Corp All Rights Reserved

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

import com.ibm.hybrid.cloud.sample.stocktrader.trader.client.PortfolioClient;
import com.ibm.hybrid.cloud.sample.stocktrader.trader.json.Portfolio;

import java.io.IOException;
import java.io.Writer;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.HashMap;

//JSR 47 Logging
import java.util.logging.Logger;

//CDI 2.0
import javax.inject.Inject;
import javax.enterprise.context.RequestScoped;

//Servlet 4.0
import javax.servlet.ServletException;
import javax.servlet.annotation.HttpConstraint;
import javax.servlet.annotation.ServletSecurity;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

//mpConfig 1.3
import org.eclipse.microprofile.config.inject.ConfigProperty;

//mpMetrics 2.0
import org.eclipse.microprofile.metrics.annotation.Gauge;
import org.eclipse.microprofile.metrics.Metadata;
import org.eclipse.microprofile.metrics.MetricRegistry;
import org.eclipse.microprofile.metrics.MetricType;
import org.eclipse.microprofile.metrics.MetricUnits;
import org.eclipse.microprofile.metrics.Tag;

//mpJWT 1.0
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
	private static final String BASIC    = "basic";
	private static final String BRONZE   = "bronze";
	private static final String SILVER   = "silver";
	private static final String GOLD     = "gold";
	private static final String PLATINUM = "platinum";
	private static final String UNKNOWN  = "unknown";
	private static final String DOLLARS  = "USD";
	private static Logger logger = Logger.getLogger(Summary.class.getName());
	private static HashMap<String, Double> totals = new HashMap<String, Double>();
	private static HashMap<String, org.eclipse.microprofile.metrics.Gauge> gauges = new HashMap<String, org.eclipse.microprofile.metrics.Gauge>();
	private NumberFormat currency = null;
	private int basic=0, bronze=0, silver=0, gold=0, platinum=0, unknown=0; //loyalty level counts

	private @Inject @ConfigProperty(name = "TEST_MODE", defaultValue = "false") boolean testMode;
	private @Inject @RestClient PortfolioClient portfolioClient;
	private @Inject JsonWebToken jwt;
	private @Inject MetricRegistry metricRegistry;

	//used in the liveness probe
	public static boolean error = false;
	public static String message = null;

	// Override Portfolio Client URL if config map is configured to provide URL
	static {
		String mpUrlPropName = PortfolioClient.class.getName() + "/mp-rest/url";
		String portfolioURL = System.getenv("PORTFOLIO_URL");
		if ((portfolioURL != null) && !portfolioURL.isEmpty()) {
			logger.info("Using Portfolio URL from config map: " + portfolioURL);
			System.setProperty(mpUrlPropName, portfolioURL);
		} else {
			logger.info("Portfolio URL not found from env var from config map, so defaulting to value in jvm.options: " + System.getProperty(mpUrlPropName));
		}
	}

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
		String rows = null;
		try {
			rows = getTableRows(request);
		} catch (Throwable t) {
			message = t.getMessage();
			logger.warning(message);
			error = true;
		}
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
		if (error) {
			writer.append("    Error communicating with the Portfolio microservice: \""+message+"\"");
			writer.append("    <p/>");
			writer.append("    Please consult the <i>trader</i> and <i>portfolio</i> pod logs for more details, or ask your administator for help.");
			writer.append("    <p/>");
		} else {
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
			writer.append(rows);
			writer.append("      </table>");
			writer.append("      <br/>");
			writer.append("      <input type=\"submit\" name=\"submit\" value=\"Submit\" style=\"font-family: sans-serif; font-size: 16px;\"/>");
			writer.append("      <input type=\"submit\" name=\"submit\" value=\"Log Out\" style=\"font-family: sans-serif; font-size: 16px;\"/>");
			writer.append("    </form>");
		}
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
			throw new NullPointerException("Injection of PortfolioClient failed!");
		}

		if (jwt==null) {
			throw new NullPointerException("Injection of JWT failed!");
		}

//		JsonArray portfolios = PortfolioServices.getPortfolios(request);
		Portfolio[] portfolios = testMode ? getHardcodedPortfolios() : portfolioClient.getPortfolios("Bearer "+jwt.getRawToken());

		basic=0; bronze=0; silver=0; gold=0; platinum=0; unknown=0; //reset loyalty level counts
		metricRegistry.remove("portfolio_value");
		for (int index=0; index<portfolios.length; index++) {
			Portfolio portfolio = portfolios[index];

			String owner = portfolio.getOwner();
			double total = portfolio.getTotal();
			String loyaltyLevel = portfolio.getLoyalty();

			setPortfolioMetric(owner, total);
			if (loyaltyLevel!=null) {
				if (loyaltyLevel.equalsIgnoreCase(BASIC)) basic++;
				else if (loyaltyLevel.equalsIgnoreCase(BRONZE)) bronze++;
				else if (loyaltyLevel.equalsIgnoreCase(SILVER)) silver++;
				else if (loyaltyLevel.equalsIgnoreCase(GOLD)) gold++;
				else if (loyaltyLevel.equalsIgnoreCase(PLATINUM)) platinum++;
				else unknown++;
			}

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

	Portfolio[] getHardcodedPortfolios() {
		Portfolio john = new Portfolio("John");
		john.setTotal(1234.56);
		john.setLoyalty("Basic");
		Portfolio karri = new Portfolio("Karri");
		karri.setTotal(12345.67);
		karri.setLoyalty("Bronze");
		Portfolio ryan = new Portfolio("Ryan");
		ryan.setTotal(23456.78);
		ryan.setLoyalty("Bronze");
		Portfolio greg = new Portfolio("Greg");
		greg.setTotal(98765.43);
		greg.setLoyalty("Silver");
		Portfolio eric = new Portfolio("Eric");
		eric.setTotal(123456.78);
		eric.setLoyalty("Gold");
		Portfolio kyle = new Portfolio("Kyle");
		kyle.setLoyalty("Basic");
		Portfolio[] portfolios = { john, karri, ryan, greg, eric, kyle };
		return portfolios;
	}

	void setPortfolioMetric(String owner, double total) {
		totals.put(owner, total);
		if (gauges.get(owner)==null) try { //gauge not yet registered for this portfolio
			org.eclipse.microprofile.metrics.Gauge<Double> gauge = () -> { return totals.get(owner); };

			Metadata metadata = Metadata.builder().withName("portfolio_value").withType(MetricType.GAUGE).withUnit(DOLLARS).build();
	
			metricRegistry.register(metadata, gauge, new Tag("owner", owner)); //registry injected via CDI

			gauges.put(owner, gauge);
		} catch (Throwable t) {
			logger.warning(t.getMessage());
		}
	}

	@Gauge(name="portfolio_loyalty", tags="level=basic", displayName="Basic", unit=MetricUnits.NONE)
	public int getBasic() {
		return basic;
	}

	@Gauge(name="portfolio_loyalty", tags="level=bronze", displayName="Bronze", unit=MetricUnits.NONE)
	public int getBronze() {
		return bronze;
	}

	@Gauge(name="portfolio_loyalty", tags="level=silver", displayName="Silver", unit=MetricUnits.NONE)
	public int getSilver() {
		return silver;
	}

	@Gauge(name="portfolio_loyalty", tags="level=gold", displayName="Gold", unit=MetricUnits.NONE)
	public int getGold() {
		return gold;
	}

	@Gauge(name="portfolio_loyalty", tags="level=platinum", displayName="Platinum", unit=MetricUnits.NONE)
	public int getPlatinum() {
		return platinum;
	}

	@Gauge(name="portfolio_loyalty", tags="level=unknown", displayName="Unknown", unit=MetricUnits.NONE)
	public int getUnknown() {
		return unknown;
	}
}
