package com.ibm.hybrid.cloud.sample.portfolio;

import java.io.IOException;
import java.io.Writer;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.Iterator;

import javax.json.JsonObject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class ViewPortfolio
 */
@WebServlet(description = "View Portfolio servlet", urlPatterns = { "/viewPortfolio" })
public class ViewPortfolio extends HttpServlet {
	private static final long serialVersionUID = 4815162342L;
	private NumberFormat currency = null;
       
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

		JsonObject portfolio = PortfolioServices.getPortfolio(owner);

		double overallTotal = portfolio.getJsonNumber("total").doubleValue();
		String loyaltyLevel = portfolio.getString("loyalty");
		JsonObject stocks = portfolio.getJsonObject("stocks");

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
		writer.append("      Stock Portfolio for "+owner+": <br/>");
		writer.append("      <br/>");
		writer.append("      <table border=\"1\" cellpadding=\"5\">");
		writer.append("        <tr>");
		writer.append("          <th>Symbol</th>");
		writer.append("          <th>Shares</th>");
		writer.append("          <th>Price</th>");
		writer.append("          <th>Date Quoted</th>");
		writer.append("          <th>Total</th>");
		writer.append("        </tr>");
		writer.append(getTableRows(stocks));
		writer.append("      </table>");
		writer.append("      <br/>");
		writer.append("      Total Portfolio Value: $"+currency.format(overallTotal)+" <br/>");
		writer.append("      <br/>");
		writer.append("      Loyalty Level: "+loyaltyLevel+" <br/>");
		writer.append("      <br/>");
		writer.append("      <input type=\"submit\" name=\"submit\" value=\"OK\" style=\"font-family: sans-serif; font-size: 16px;\">");
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
		//In minikube and CFC, the port number is wrong for the https redirect.
		//This will fix that if needed - otherwise, it just returns an empty string
		//so that we can still use relative paths
		String prefix = PortfolioServices.getRedirectWorkaround(request);

		response.sendRedirect(prefix+"summary"); //send control to the Summary servlet
	}

	private String getTableRows(JsonObject stocks) {
		StringBuffer rows = new StringBuffer();

		Iterator<String> keys = stocks.keySet().iterator();

		while (keys.hasNext()) {
			String key = keys.next();
			JsonObject stock = stocks.getJsonObject(key);

			String symbol = stock.getString("symbol");
			int shares = stock.getInt("shares");
			double price = stock.getJsonNumber("price").doubleValue();
			String date = stock.getString("date");
			double total = stock.getJsonNumber("total").doubleValue();

			rows.append("        <tr>");
			rows.append("          <td>"+symbol+"</td>");
			rows.append("          <td>"+shares+"</td>");
			rows.append("          <td>$"+currency.format(price)+"</td>");
			rows.append("          <td>"+date+"</td>");
			rows.append("          <td>$"+currency.format(total)+"</td>");
			rows.append("        </tr>");
		}

		return rows.toString();
	}
}
