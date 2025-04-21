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

//JSR 47 Logging
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

//CDI 2.0
import jakarta.inject.Inject;
import jakarta.enterprise.context.ApplicationScoped;

//Servlet 4.0
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.HttpConstraint;
import jakarta.servlet.annotation.ServletSecurity;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.RequestDispatcher;

//mpConfig 1.3
import org.eclipse.microprofile.config.inject.ConfigProperty;

//mpJWT 1.0
import org.eclipse.microprofile.jwt.JsonWebToken;

//mpRestClient 1.0
import org.eclipse.microprofile.rest.client.inject.RestClient;

import org.apache.commons.math3.stat.descriptive.SynchronizedDescriptiveStatistics;

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
	private static final String POST     = "post";
	private static final String TOKEN    = "access_token";
	private static final String JWT      = "jwt";
	private static Logger logger = Logger.getLogger(Summary.class.getName());
	private static Utilities utilities = null;

	private @Inject @ConfigProperty(name = "TEST_MODE", defaultValue = "false") boolean testMode;
	private @Inject @RestClient BrokerClient brokerClient;
	private @Inject JsonWebToken jwt;

	//used in the liveness probe
	public static boolean error = false;
	public static String message = null;

	// New liveness probe by @rtclauss
	private static SynchronizedDescriptiveStatistics last1kCalls;
	public static AtomicBoolean IS_FAILED = new AtomicBoolean(false);
	private static final double FAILURE_THRESHOLD = 0.85;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public Summary() {
		super();
		if(last1kCalls==null){
			last1kCalls = new SynchronizedDescriptiveStatistics(1000);
		}
		if (utilities == null) utilities = new Utilities(logger);
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		if (brokerClient==null) {
			throw new NullPointerException("Injection of BrokerClient failed!");
		}

		try {
			if (Utilities.useOIDC) {
				String method = request.getMethod();
				//normally goGet gets called via a GET, but on redirect from a successful login against an OIDC provider (like KeyCloak),
				//we will get called via a POST, with a form param containing the JWT issued by the OIDC provider, which we'll stash in the session
				//TODO: Consider using a cookie instead of the session, since if there are multiple Trader pods, we'd need distributed session support enabled
				//(or some kind of "sticky session" support to get a given browser always routed to the same pod (though that isn't highly available, of course),
				//such as https://docs.aws.amazon.com/elasticloadbalancing/latest/application/sticky-sessions.html)
				if (POST.equalsIgnoreCase(method)) {
					String token = request.getParameter(TOKEN);
					HttpSession session = request.getSession();
					if (session!=null) {
						logger.info("Placing JWT in the http session");
						session.setAttribute(JWT, token);
						if (logger.isLoggable(Level.FINE)) {
							logger.fine(TOKEN+" = "+token);
							Base64.Decoder decoder = Base64.getUrlDecoder();
							String[] parts = token.split("\\.");
							String header = new String(decoder.decode(parts[0]));
							String payload = new String(decoder.decode(parts[1]));
							logger.fine("access token header = "+header);
							logger.fine("access token body = "+payload);
						}
					}
				}
			} else {
				if (jwt==null) throw new NullPointerException("Injection of JWT failed!");
			}
//			JsonArray portfolios = PortfolioServices.getPortfolios(request);
			Broker[] brokers = testMode ? getHardcodedBrokers() : brokerClient.getBrokers(utilities.getAuthHeader(jwt, request));

			// set brokers for JSP
			request.setAttribute("brokers", brokers);
			last1kCalls.addValue(1.0);
		} catch (Throwable t) {
			utilities.logException(t);
			message = t.getMessage();
			error = true;
			request.setAttribute("message", message);
			request.setAttribute("error", error);
			last1kCalls.addValue(0.0);
		} finally {
			var mean = last1kCalls.getMean();
			logger.finest("Is failing calc mean: "+ mean);
			if (mean < FAILURE_THRESHOLD) {
				logger.warning("Trader is failing liveness threshold");
				IS_FAILED.set(true);
			} else {
				IS_FAILED.set(false);
			}
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
						brokerClient.deleteBroker(utilities.getAuthHeader(jwt, request), owner);
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
}
