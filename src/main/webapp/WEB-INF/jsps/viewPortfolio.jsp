<%@ page language="java" contentType="text/html; charset=UTF-8" session="false" 
import="java.text.*,java.math.RoundingMode,com.ibm.hybrid.cloud.sample.stocktrader.trader.json.*,javax.json.*,java.util.*"%>

<%!
static NumberFormat currency = NumberFormat.getNumberInstance();
static {

  currency.setMinimumFractionDigits(2);
  currency.setMaximumFractionDigits(2);
  currency.setRoundingMode(RoundingMode.HALF_UP);
} 
%>

<!DOCTYPE html >
<html lang="en">
  <head>
    <title>Stock Trader</title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
  </head>
  <body>
    <img src="header.jpg" width="534" height="200" alt="header image"/>
    <br/>
    <br/>
		<form method="post"/>
		  Stock Portfolio for <b>${param.owner}</b>: <br/>
      <br/>
      <% 
      Broker broker = ((Broker)request.getAttribute("broker"));
      if(broker == null ) {
        out.println("No broker data");
      }
      else {
      %>
		  <table border="1" cellpadding="5">
		    <tr>
		      <th>Symbol</th>
		      <th>Shares</th>
		      <th>Price</th>
		      <th>Date Quoted</th>
		      <th>Total</th>
		      <th>Commission</th>
        </tr>
        ${rows}
        <%
        JsonObject stocks = broker.getStocks(); 
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
    
            if (price == -1) {
              formattedPrice = "Error";
              formattedTotal = "Error";
              formattedCommission = "Error";
            }
    %>
            <tr>
              <td><%=symbol%></td>
              <td><%=shares%></td>
              <td><%=formattedPrice%></td>
              <td><%=date%></td>
              <td><%=formattedTotal%></td>
              <td><%=formattedCommission%></td>
            </tr>
            <%
          }
        }
    

        %>
		  </table>
		  <br/>
		  <table>
		    <tr>
		      <td>Total Portfolio Value:</td>
		      <td><b>$<%=currency.format(broker.getTotal())%></b></td>
		    </tr>
		    <tr>
		      <td>Loyalty Level:</td>
		      <td><b>${broker.loyalty}</b></td>
		    </tr>
		    <tr>
		      <td>Account Balance:</td>
		      <td><b>$<%=currency.format(broker.getBalance())%></b></td>
		    </tr>
		    <tr>
		      <td>Total Commissions Paid:</td>
		      <td><b>$<%=currency.format(broker.getCommissions())%></b></td>
		    </tr>
		    <tr>
		      <td>Free Trades Available:</td>
		      <td><b>${broker.free}</b></td>
		    </tr>
		    <tr>
		      <td>Sentiment:</td>
		      <td><b>${broker.sentiment}</b></td>
		    </tr>
		    <tr>
		      <td>Return On Investment:</td>
		      <td><b>${returnOnInvestment}</b></td>
		    </tr>
		  </table>
		  <br/>
		  <input type="submit" name="submit" value="OK" style="font-family: sans-serif; font-size: 16px;"/>
		  <input type="submit" name="submit" value="Buy/Sell Stock" style="font-family: sans-serif; font-size: 16px;"/>
		  <input type="submit" name="submit" value="Submit Feedback" style="font-family: sans-serif; font-size: 16px;"/>
		</form>
    <% } //else %>
    <br/>
    <a href="https://github.com/IBMStockTrader">
      <img src="footer.jpg" alt="footer image"/>
    </a>
  </body>
</html>