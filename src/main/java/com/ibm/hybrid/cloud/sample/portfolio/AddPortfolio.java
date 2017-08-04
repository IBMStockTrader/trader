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

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class AddPortfolio
 */
@WebServlet(description = "Add Portfolio servlet", urlPatterns = { "/addPortfolio" })
public class AddPortfolio extends HttpServlet {
	private static final long serialVersionUID = 4815162342L;
    
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Writer writer = response.getWriter();
		writer.append("<!DOCTYPE html>");
		writer.append("<html>");
		writer.append("  <head>");
		writer.append("    <title>Stock Portfolio</title>");
		writer.append("    <meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">");
		writer.append("  </head>");
		writer.append("  <body>");
		writer.append("    <img src=\"header.jpg\" width=\"534\" height=\"200\"/>");
		writer.append("    <br/>");
		writer.append("    <form method=\"post\"/>");
		writer.append("      Owner: <input type=\"text\" name=\"owner\"><br/>");
		writer.append("      <br/>");
		writer.append("      <input type=\"submit\" name=\"submit\" value=\"Submit\" style=\"font-family: sans-serif; font-size: 16px;\">");
		writer.append("    </form>");
		writer.append("    <br/>");
		writer.append("    <a href=\"http://ibm.com/bluemix\">");
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

		if ((owner!=null) && !owner.equals("")) {
			PortfolioServices.createPortfolio(owner);
		}

		//In minikube and CFC, the port number is wrong for the https redirect.
		//This will fix that if needed - otherwise, it just returns an empty string
		//so that we can still use relative paths
		String prefix = PortfolioServices.getRedirectWorkaround(request);

		response.sendRedirect(prefix+"summary"); //send control to the Summary servlet
	}
}
