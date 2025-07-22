<%@ page language="java" contentType="text/html; charset=UTF-8" session="false" 
import="java.text.*,java.math.RoundingMode,com.ibm.hybrid.cloud.sample.stocktrader.trader.Utilities,com.ibm.hybrid.cloud.sample.stocktrader.trader.json.*,jakarta.json.*,java.util.*"%>

<%!
static String headerImage  = Utilities.getHeaderImage();
static String footerImage  = Utilities.getFooterImage();

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
    <!-- Bootstrap 5 CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- Web fonts -->
    <link href="https://fonts.googleapis.com/css?family=Roboto:400,500&display=swap" rel="stylesheet">
    <!-- Montserrat font for brand -->
    <link href="https://fonts.googleapis.com/css?family=Montserrat:700&display=swap" rel="stylesheet">
    <style>
      body, h1, h2, h3, h4, h5, h6, label, input, button, .form-label, .form-control {
        font-family: 'Roboto', Arial, Helvetica, system-ui, sans-serif;
      }
      .main-card {
        max-width: 700px;
        margin: 2rem auto;
        padding: 2rem 2rem 1.5rem 2rem;
      }
      .header-img {
        max-width: 100%;
        height: auto;
        display: block;
        margin-left: auto;
        margin-right: auto;
      }
      .form-inner {
        max-width: 500px;
        margin-left: auto;
        margin-right: auto;
      }
    </style>
  </head>
  <body class="bg-light">
    <%@ include file="/WEB-INF/jsps/partials/navbar.jspf" %>
    <div class="container min-vh-100 d-flex flex-column justify-content-center align-items-center">
      <div class="card shadow-sm main-card w-100">
        <div class="card-body p-4">
          <div class="text-center mb-4">
            <img src="<%=headerImage%>" alt="header image" class="header-img mb-3"/>
          </div>
          <div class="form-inner">
            <form method="post" class="needs-validation" novalidate>
              <div class="mb-3 text-center">
                Stock Portfolio for <b>${param.owner}</b>:
              </div>
              <% 
              Broker broker = ((Broker)request.getAttribute("broker"));
              if(broker == null ) {
                out.println("<div class='alert alert-danger'>No broker data</div>");
              }
              else {
              %>
              <div class="table-responsive mb-3">
                <table class="table table-bordered align-middle">
                  <thead class="table-light">
                    <tr>
                      <th scope="col">Symbol</th>
                      <th scope="col">Shares</th>
                      <th scope="col">Price</th>
                      <th scope="col">Date Quoted</th>
                      <th scope="col">Total</th>
                      <th scope="col">Commission</th>
                    </tr>
                  </thead>
                  <tbody>
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
                  </tbody>
                </table>
              </div>
              <div class="table-responsive mb-3">
                <table class="table table-bordered align-middle">
                  <tbody>
                    <tr>
                      <th scope="row">Portfolio Value:</th>
                      <td><b>$<%=currency.format(broker.getTotal())%></b></td>
                    </tr>
                    <tr>
                      <th scope="row">Loyalty Level:</th>
                      <td><b>${broker.loyalty}</b></td>
                    </tr>
                    <tr>
                      <th scope="row">Account Balance:</th>
                      <td><b>$<%=currency.format(broker.getBalance())%></b></td>
                    </tr>
                    <tr>
                      <th scope="row">Cash Account Balance:</th>
                      <td><b><%=broker.getCashAccountCurrency()%> <%=currency.format(broker.getCashAccountBalance())%></b></td>
                    </tr>
                    <tr>
                      <th scope="row">Total Commissions Paid:</th>
                      <td><b>$<%=currency.format(broker.getCommissions())%></b></td>
                    </tr>
                    <tr>
                      <th scope="row">Free Trades Available:</th>
                      <td><b>${broker.free}</b></td>
                    </tr>
                    <tr>
                      <th scope="row">Sentiment:</th>
                      <td><b>${broker.sentiment}</b></td>
                    </tr>
                    <tr>
                      <th scope="row">Return On Investment:</th>
                      <td><b>${returnOnInvestment}</b></td>
                    </tr>
                  </tbody>
                </table>
              </div>
              <div class="d-grid gap-2 d-md-flex justify-content-md-center">
                <button type="submit" name="submit" value="OK" class="btn btn-primary">OK</button>
                <button type="submit" name="submit" value="Buy/Sell Stock" class="btn btn-success">Buy/Sell Stock</button>
                <button type="submit" name="submit" value="Submit Feedback" class="btn btn-outline-secondary">Submit Feedback</button>
              </div>
              <% } //else %>
            </form>
          </div>
        </div>
        <div class="card-footer text-center bg-white border-0">
          <a href="https://github.com/IBMStockTrader">
            <img src="<%=footerImage%>" alt="footer image" class="img-fluid mt-3"/>
          </a>
        </div>
      </div>
    </div>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <script>
      // --- Bootstrap client-side validation (for future extensibility) ---
      (() => {
        "use strict";
        const forms = document.querySelectorAll("form");
        Array.from(forms).forEach((form) => {
          form.addEventListener(
            "submit",
            (event) => {
              if (!form.checkValidity()) {
                event.preventDefault();
                event.stopPropagation();
              }
              form.classList.add("was-validated");
            },
            false
          );
        });
      })();
    </script>
  </body>
</html>
