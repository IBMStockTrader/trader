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
    <table border="0" cellpadding="5">
      <tr><td>Owner:</td><td><input type="text" name="owner"></td></tr>
      <tr><td>Cash Account initial balance:</td><td><input type="number" name="balance" step="0.01" min="0" value="10000.00"></td></tr>
      <tr><td>Cash Account currency:</td><td><select name="currency">
        <option value="AUD">Australian Dollar</option>
        <option value="BGN">Bulgarian Lev</option>
        <option value="BRL">Brazilian Real</option>
        <option value="CAD">Canadian Dollar</option>
        <option value="CHF">Swiss Franc</option>
        <option value="CNY">Chinese Renminbi Yuan</option>
        <option value="DKK">Danish Krone</option>
        <option value="EUR">Euro</option>
        <option value="GBP">British Pound</option>
        <option value="HKD">Hong Kong Dollar</option>
        <option value="HUF">Hungarian Forint</option>
        <option value="IDR">Indonesian Rupiah</option>
        <option value="ILS">Israeli New Sheqel</option>
        <option value="INR">Indian Rupee</option>
        <option value="ISK">Icelandic Króna</option>
        <option value="JPY">Japanese Yen</option>
        <option value="KRW">South Korean Won</option>
        <option value="MXN">Mexican Peso</option>
        <option value="MYR">Malaysian Ringgit</option>
        <option value="NOK">Norwegian Krone</option>
        <option value="NZD">New Zealand Dollar</option>
        <option value="PHP">Philippine Peso</option>
        <option value="PLN">Polish Złoty</option>
        <option value="RON">Romanian Leu</option>
        <option value="SEK">Swedish Krona</option>
        <option value="SGD">Singapore Dollar</option>
        <option value="THB">Thai Baht</option>
        <option value="TRY">Turkish Lira</option>
        <option value="USD" selected>United States Dollar</option>
        <option value="ZAR">South African Rand</option>
      </select></td></tr></table>
      <br/>
      <input type="submit" name="submit" value="Submit" style="font-family: sans-serif; font-size: 16px;"/>
    </form>      
    <br/>
    <a href="https://github.com/IBMStockTrader">
      <img src="footer.jpg" alt="footer image"/>
    </a>
  </body>
</html>
