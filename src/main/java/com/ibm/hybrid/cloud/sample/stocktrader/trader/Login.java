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

import java.io.IOException;
import java.util.logging.Logger;


//Servlet 4.0
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.RequestDispatcher;

/**
 * Servlet implementation class Login
 */
@WebServlet(description = "Login servlet", urlPatterns = { "/login" })
public class Login extends HttpServlet {
	private static final long serialVersionUID = 4815162342L;
	private static Logger logger = Logger.getLogger(Login.class.getName());
	private static Utilities utilities = null;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public Login() {
		super();

		if (utilities == null) utilities = new Utilities(logger);
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/WEB-INF/jsps/login.jsp");
        dispatcher.forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		boolean success = false;
		String id = request.getParameter("id");
		String password = request.getParameter("password");

		try {
			if (request.getUserPrincipal() != null) request.logout(); //in case there's a left over auth cookie but we ended up here

			request.login(id, password);

			Cookie cookie = new Cookie("user", id); //clear text user id that can be used in Istio routing rules
			response.addCookie(cookie);

			success = true;
			logger.info("Successfully logged in user: "+id);
		} catch (Throwable t) {
			utilities.logException(t);
		}

		String url = "error";
		if (success) url = "summary";

		response.sendRedirect(url);
	}
}
