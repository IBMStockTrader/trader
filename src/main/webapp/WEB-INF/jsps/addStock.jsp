<%@ page language="java" contentType="text/html; charset=UTF-8" session="false"%>
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
		  <table>
		    <tr>
		      <th scope="row"><b>Owner:</b></th>
		      <td>${param.owner}</td>
		    </tr>
		    <tr>
		      <th scope="row"><b>Commission:</b></td>
		      <td>${commission}</td>
		    </tr>
		    <tr>
		      <th scope="row"><b>Stock Symbol:</b></td>
		      <td><input type="text" name="symbol"></td>
		    </tr>
		    <tr>
		      <th scope="row"><b>Number of Shares:</b></td>
		      <td><input type="text" name="shares"></td>
		    </tr>
		    <tr role="presentation">
		      <td><input type="radio" name="action" value="Buy" checked> Buy</td>
		      <td><input type="radio" name="action" value="Sell"> Sell</td>
		    </tr>
		  </table>
		  <br/>
		  <input type="submit" name="submit" value="Submit" style="font-family: sans-serif; font-size: 16px;"/>
		  <input type="submit" name="submit" value="Cancel" style="font-family: sans-serif; font-size: 16px;"/>
		</form>
    
    <br/>
    <a href="https://github.com/IBMStockTrader">
      <img src="footer.jpg" alt="footer image"/>
    </a>
  </body>
</html>