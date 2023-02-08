<%@ page language="java" contentType="text/html; charset=UTF-8" session="false"%>
<!DOCTYPE html >
<html lang="en">
  <head>
    <title>Stock Trader</title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
  </head>
  <body>
    <img src="header.jpg" width="534" height="200" alt="header image"/>
    <p/>
    <i>This account will receive a free <b>$50</b> balance for commissions!</i>
    <p/>
    <form method="post"/>
      Owner: <input type="text" name="owner"><br/>
      <br/>
      Cash Account initial balance: <input type="number" name="balance"><br/>
      <br/>
      Cash Account currency: <input type="text" name="currency"><br/>
      <br/>
      <input type="submit" name="submit" value="Submit" style="font-family: sans-serif; font-size: 16px;"/>
    </form>      
    <br/>
    <a href="https://github.com/IBMStockTrader">
      <img src="footer.jpg" alt="footer image"/>
    </a>
  </body>
</html>
