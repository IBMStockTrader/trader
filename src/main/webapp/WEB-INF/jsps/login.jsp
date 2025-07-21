<%@ page language="java" contentType="text/html; charset=UTF-8" session="false"
import="com.ibm.hybrid.cloud.sample.stocktrader.trader.Utilities"%>

<%!
static String headerImage  = Utilities.getHeaderImage();
static String footerImage  = Utilities.getFooterImage();
static String loginMessage = Utilities.getLoginMessage();
%>

<!DOCTYPE html>
<html lang="en">
  <head>
    <title>Stock Trader</title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
  </head>
  <body>
    <img src="<%=headerImage%>" alt="header image"/>
    <p/>
    <%=loginMessage%>
    <form method="post"/>
      <table>
        <tr>
          <th scope="row">Username:</th>
          <td><input type="text" name="id"></td>
        </tr>
        <tr>
          <th scope="row">Password:</th>
          <td><input type="password" name="password"></td>
        </tr>
      </table>
      <br/>
      <input type="submit" name="submit" value="Submit" style="font-family: sans-serif; font-size: 16px; background-color: red;">
    </form>
    <br/>
    <a href="https://github.com/IBMStockTrader">
      <img src="<%=footerImage%>" alt="footer image"/>
    </a>
  </body>
</html>
