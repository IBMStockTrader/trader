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
		<p/>
		<i>Please share your feedback on this tool!</i>
		<p/>
		<form method="post"/>
		  <textarea name="feedback" rows="10" cols="70"></textarea>
		  <p/>
		  <input type="submit" name="submit" value="Submit" style="font-family: sans-serif; font-size: 16px;" />
		  <input type="submit" name="submit" value="Cancel" style="font-family: sans-serif; font-size: 16px;" />
		</form>    
    <br/>
    <a href="https://github.com/IBMStockTrader">
      <img src="<%=footerImage%>" alt="footer image"/>
    </a>
  </body>
</html>