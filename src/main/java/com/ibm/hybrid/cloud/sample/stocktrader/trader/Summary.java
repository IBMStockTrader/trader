/*
       Copyright 2017-2022 IBM Corp All Rights Reserved

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
import java.util.HashMap;

//JSR 47 Logging
import java.util.logging.Logger;

//CDI 2.0
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
import javax.servlet.http.HttpSession;
import javax.servlet.RequestDispatcher;

//mpConfig 1.3
import org.eclipse.microprofile.config.inject.ConfigProperty;

//mpJWT 1.0
import org.eclipse.microprofile.jwt.JsonWebToken;

//mpMetrics 2.0
import org.eclipse.microprofile.metrics.annotation.Gauge;
import org.eclipse.microprofile.metrics.Metadata;
import org.eclipse.microprofile.metrics.MetricRegistry;
import org.eclipse.microprofile.metrics.MetricType;
import org.eclipse.microprofile.metrics.MetricUnits;
import org.eclipse.microprofile.metrics.Tag;

//mpRestClient 1.0
import org.eclipse.microprofile.rest.client.inject.RestClient;


/**
 * Servlet implementation class Summary
 */
@WebServlet(description = "Broker summary servlet", urlPatterns = { "/summary" })
@ServletSecurity(@HttpConstraint(rolesAllowed = { "StockTrader", "StockViewer" } ))
@ApplicationScoped
public class Summary extends HttpServlet {
	private static final long serialVersionUID = 4815162342L;
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
	private static Utilities utilities = null;
	private int basic=0, bronze=0, silver=0, gold=0, platinum=0, unknown=0; //loyalty level counts

	private @Inject @ConfigProperty(name = "TEST_MODE", defaultValue = "false") boolean testMode;
	private @Inject @RestClient BrokerClient brokerClient;
	private @Inject JsonWebToken jwt;
	private @Inject MetricRegistry metricRegistry;

	//used in the liveness probe
	public static boolean error = false;
	public static String message = null;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public Summary() {
		super();

		if (utilities == null) utilities = new Utilities(logger);
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		String rows = null;

		try {
			rows = getTableRows(request);
			request.setAttribute("rows", rows);
		} catch (Throwable t) {
			utilities.logException(t);
			message = t.getMessage();
			error = true;
			request.setAttribute("message", message);
			request.setAttribute("error", error);
		}

		RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/WEB-INF/jsps/summary.jsp");
        dispatcher.forward(request, response);

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
						response.sendRedirect("addStock?owner="+owner+"&source=summary"); //send control to the AddStock servlet
					} else if (action.equals(DELETE)) {
//						PortfolioServices.deletePortfolio(request, owner);
						brokerClient.deleteBroker("Bearer "+utilities.getJWT(jwt), owner);
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

		if (brokerClient==null) {
			throw new NullPointerException("Injection of BrokerClient failed!");
		}

		if (jwt==null) {
			throw new NullPointerException("Injection of JWT failed!");
		}

//		JsonArray portfolios = PortfolioServices.getPortfolios(request);
		Broker[] brokers = testMode ? getHardcodedBrokers() : brokerClient.getBrokers("Bearer "+utilities.getJWT(jwt));
		
		// set brokers for JSP
		request.setAttribute("brokers", brokers);

		basic=0; bronze=0; silver=0; gold=0; platinum=0; unknown=0; //reset loyalty level counts
		metricRegistry.remove("portfolio_value");
		for (int index=0; index<brokers.length; index++) {
			Broker broker = brokers[index];
			String owner = broker.getOwner();

			utilities.logToS3(owner, broker);

			double total = broker.getTotal();
			String loyaltyLevel = broker.getLoyalty();

			setBrokerMetric(owner, total);
			if (loyaltyLevel!=null) {
				if (loyaltyLevel.equalsIgnoreCase(BASIC)) basic++;
				else if (loyaltyLevel.equalsIgnoreCase(BRONZE)) bronze++;
				else if (loyaltyLevel.equalsIgnoreCase(SILVER)) silver++;
				else if (loyaltyLevel.equalsIgnoreCase(GOLD)) gold++;
				else if (loyaltyLevel.equalsIgnoreCase(PLATINUM)) platinum++;
				else unknown++;
			}

    	}

		return rows.toString();
	}

	Broker[] getHardcodedBrokers() {
		Broker john = new Broker("John");
		john.setTotal(1234.56);
		john.setLoyalty("Basic");
		Broker karri = new Broker("Karri");
		karri.setTotal(12345.67);
		karri.setLoyalty("Bronze");
		Broker ryan = new Broker("Ryan");
		ryan.setTotal(23456.78);
		ryan.setLoyalty("Bronze");
		Broker raunak = new Broker("Raunak");
		raunak.setTotal(98765.43);
		raunak.setLoyalty("Silver");
		Broker greg = new Broker("Greg");
		greg.setTotal(123456.78);
		greg.setLoyalty("Gold");
		Broker eric = new Broker("Eric");
		eric.setTotal(1234567.89);
		eric.setLoyalty("Platinum");
		Broker[] brokers = { john, karri, ryan, raunak, greg, eric };
		return brokers;
	}

	void setBrokerMetric(String owner, double total) {
		totals.put(owner, total);
		if (gauges.get(owner)==null) try { //gauge not yet registered for this portfolio
			org.eclipse.microprofile.metrics.Gauge<Double> gauge = () -> { return totals.get(owner); };

			Metadata metadata = Metadata.builder().withName("broker_value").withType(MetricType.GAUGE).withUnit(DOLLARS).build();

			metricRegistry.register(metadata, gauge, new Tag("owner", owner)); //registry injected via CDI

			gauges.put(owner, gauge);
		} catch (Throwable t) {
			logger.warning(t.getMessage());
		}
	}

	@Gauge(name="broker_loyalty", tags="level=basic", displayName="Basic", unit=MetricUnits.NONE)
	public int getBasic() {
		return basic;
	}

	@Gauge(name="broker_loyalty", tags="level=bronze", displayName="Bronze", unit=MetricUnits.NONE)
	public int getBronze() {
		return bronze;
	}

	@Gauge(name="broker_loyalty", tags="level=silver", displayName="Silver", unit=MetricUnits.NONE)
	public int getSilver() {
		return silver;
	}

	@Gauge(name="broker_loyalty", tags="level=gold", displayName="Gold", unit=MetricUnits.NONE)
	public int getGold() {
		return gold;
	}

	@Gauge(name="broker_loyalty", tags="level=platinum", displayName="Platinum", unit=MetricUnits.NONE)
	public int getPlatinum() {
		return platinum;
	}

	@Gauge(name="broker_loyalty", tags="level=unknown", displayName="Unknown", unit=MetricUnits.NONE)
	public int getUnknown() {
		return unknown;
	}
}
