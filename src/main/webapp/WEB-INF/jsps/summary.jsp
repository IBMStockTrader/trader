<%@ page language="java" contentType="text/html; charset=UTF-8" session="false" 
import="java.text.*,java.math.RoundingMode,com.ibm.hybrid.cloud.sample.stocktrader.trader.Utilities,com.ibm.hybrid.cloud.sample.stocktrader.trader.json.*"%>

<%!
static String headerImage  = Utilities.getHeaderImage();
static String footerImage  = Utilities.getFooterImage();
static String loginMessage = Utilities.getLoginMessage();

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
    <img src="<%=headerImage%>" alt="header image"/>
    <br/>
    <br/>
    <%
      if(request.getAttribute("error") != null && ((Boolean)request.getAttribute("error")).booleanValue() == true) {
    %>
      Error communicating with the Broker microservice: ${message}
      <p/>
      Please consult the <i>trader</i>, <i>broker</i> and <i>portfolio</i> pod logs for more details, or ask your administator for help.
      <p/>
    <%
      } else {
    %>
    <form method="post"/>
    <input type="radio" name="action" value="retrieve" checked> Retrieve selected portfolio<br>

    <% if(request.isUserInRole("StockTrader")) { %>
      <input type="radio" name="action" value="create"> Create a new portfolio<br>
      <input type="radio" name="action" value="update"> Update selected portfolio (buy/sell stock)<br>
      <input type="radio" name="action" value="delete"> Delete selected portfolio<br>
    <% } %>

    <br/>
    <table border="1" cellpadding="5">
       <tr>
       <th scope="col"></th>
       <th scope="col">Owner</th>
       <th scope="col">Total</th>
       <th scope="col">Loyalty Level</th>
       </tr>
<%
List<Broker> brokers = (List<Broker>)request.getAttribute("brokers");

if(brokers == null) {
%>
  Error communicating with the Broker microservice: ${message}
  <p/>
  Please consult the <i>trader</i>, <i>broker</i> and <i>portfolio</i> pod logs for more details, or ask your administator for help.
  <p/>
<% 
} else {

  for (int index=0; index<brokers.size(); index++) { 
    Broker broker = brokers.get(index);
    String owner = broker.getOwner();
    Utilities.logToS3(owner, broker);
%>
    <tr>
      <td><input type="radio" name="owner" value="<%=owner%>" <%= ((index ==0)?" checked ": " ") %>></td>
      <td><%=owner%></td>
      <td>$<%=currency.format(broker.getTotal())%></td>
      <td><%=broker.getLoyalty()%></td>
    </tr>
<% 
  } 
}
%>
    </table>
    <br/>
    <input type="submit" name="submit" value="Submit" style="font-family: sans-serif; font-size: 16px;"/>
    <input type="submit" name="submit" value="Log Out" style="font-family: sans-serif; font-size: 16px;"/>
  </form>
    <%
      }

    %>

    <br/>
    <a href="https://github.com/IBMStockTrader">
      <img src="<%=footerImage%>" alt="footer image"/>
    </a>
  </body>
</html>
