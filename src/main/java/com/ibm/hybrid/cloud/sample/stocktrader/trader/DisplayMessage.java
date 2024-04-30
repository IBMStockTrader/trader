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
import java.io.Writer;
import java.net.URLDecoder;
import java.util.logging.Logger;

//Servlet 3.1
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.HttpConstraint;
import jakarta.servlet.annotation.ServletSecurity;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.RequestDispatcher;

/**
 * Servlet implementation class DisplayMessage
 */
@WebServlet(description = "Display Message servlet", urlPatterns = { "/message" })
@ServletSecurity(@HttpConstraint(rolesAllowed = { "StockTrader" } ))
public class DisplayMessage extends HttpServlet {
	private static final long serialVersionUID = 4815162342L;
	private static Logger logger = Logger.getLogger(DisplayMessage.class.getName());
    
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String encodedMessage = request.getParameter("message");
		if(encodedMessage == null) encodedMessage = "";
		String message = URLDecoder.decode(encodedMessage, "UTF-8");
		request.setAttribute("message", message);

		RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/WEB-INF/jsps/message.jsp");
        dispatcher.forward(request, response);			
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String owner = request.getParameter("owner");

		if ((owner==null) || owner.equals("")) {
			logger.info("Redirecting to summary servlet");
			response.sendRedirect("summary"); //send control to the Summary servlet
		} else {
			logger.info("Redirecting to viewPortfolio servlet");
			response.sendRedirect("viewPortfolio?owner="+owner); //send control to the ViewPortfolio servlet
		}
	}
}
