<%@ page language="java" contentType="text/html; charset=UTF-8" session="false"
import="com.ibm.hybrid.cloud.sample.stocktrader.trader.Utilities"%>

<%!
static String headerImage  = Utilities.getHeaderImage();
static String footerImage  = Utilities.getFooterImage();
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
    <form method="post"/>
    An error occurred during login.  Please try again.
    <br/>
    <br/>
      <input type="submit" name="submit" value="Try again" style="font-family: sans-serif; font-size: 16px;">
    </form>
    <br/>
    <a href="https://github.com/IBMStockTrader">
      <img src="<%=footerImage%>" alt="footer image"/>
    </a>
  </body>
</html>