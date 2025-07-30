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
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <!-- Bootstrap 5 CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- Web fonts -->
    <link href="https://fonts.googleapis.com/css?family=Roboto:400,500&display=swap" rel="stylesheet">
    <!-- Montserrat font for brand -->
    <link href="https://fonts.googleapis.com/css?family=Montserrat:700&display=swap" rel="stylesheet">
    <!-- Bootstrap Icons for table and buttons -->
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css">
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/common.css">
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/viewPortfolio.css">
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
                <h1 class="page-heading mb-2">
                  <i class="bi bi-graph-up-arrow text-success me-2" aria-hidden="true"></i>
                  <span class="brand-main">Stock</span><span class="brand-accent">Trader</span> Portfolio
                </h1>
                <div class="fw-semibold">for <span class="text-primary">${param.owner}</span></div>
              </div>
              <% 
              Broker broker = ((Broker)request.getAttribute("broker"));
              if(broker == null ) {
                out.println("<div class='alert alert-danger'>No broker data</div>");
              }
              else {
              %>
              <div class="table-responsive mb-3">
                <table class="table table-bordered align-middle table-hover">
                  <thead class="table-light">
                    <tr>
                      <th scope="col"><i class="bi bi-upc-scan me-1" aria-hidden="true"></i>Symbol</th>
                      <th scope="col"><i class="bi bi-hash me-1" aria-hidden="true"></i>Shares</th>
                      <th scope="col"><i class="bi bi-currency-dollar me-1" aria-hidden="true"></i>Price</th>
                      <th scope="col"><i class="bi bi-calendar-date me-1" aria-hidden="true"></i>Date Quoted</th>
                      <th scope="col"><i class="bi bi-cash-stack me-1" aria-hidden="true"></i>Total</th>
                      <th scope="col"><i class="bi bi-receipt me-1" aria-hidden="true"></i>Commission</th>
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
                      <th scope="row"><i class="bi bi-cash-coin me-1" aria-hidden="true"></i>Portfolio Value:</th>
                      <td><b>$<%=currency.format(broker.getTotal())%></b></td>
                    </tr>
                    <tr>
                      <th scope="row"><i class="bi bi-award me-1" aria-hidden="true"></i>Loyalty Level:</th>
                      <td>
                        <b>
                          <span class="badge 
                            <% String loyalty = broker.getLoyalty(); String badgeClass = "bg-light text-dark border";
                              if (loyalty != null) {
                                if (loyalty.equalsIgnoreCase("Platinum")) badgeClass = "bg-primary";
                                else if (loyalty.equalsIgnoreCase("Gold")) badgeClass = "bg-warning text-dark";
                                else if (loyalty.equalsIgnoreCase("Silver")) badgeClass = "bg-secondary";
                                else if (loyalty.equalsIgnoreCase("Bronze")) badgeClass = "bg-light text-dark border";
                              }
                            %><%=badgeClass%>">
                            <%=broker.getLoyalty()%>
                          </span>
                        </b>
                      </td>
                    </tr>
                    <tr>
                      <th scope="row"><i class="bi bi-wallet2 me-1" aria-hidden="true"></i>Account Balance:</th>
                      <td><b>$<%=currency.format(broker.getBalance())%></b></td>
                    </tr>
                    <tr>
                      <th scope="row"><i class="bi bi-bank me-1" aria-hidden="true"></i>Cash Account Balance:</th>
                      <td><b><%=broker.getCashAccountCurrency()%> <%=currency.format(broker.getCashAccountBalance())%></b></td>
                    </tr>
                    <tr>
                      <th scope="row"><i class="bi bi-receipt me-1" aria-hidden="true"></i>Total Commissions Paid:</th>
                      <td><b>$<%=currency.format(broker.getCommissions())%></b></td>
                    </tr>
                    <tr>
                      <th scope="row"><i class="bi bi-gift me-1" aria-hidden="true"></i>Free Trades Available:</th>
                      <td><b>${broker.free}</b></td>
                    </tr>
                    <tr>
                      <th scope="row"><i class="bi bi-emoji-smile me-1" aria-hidden="true"></i>Sentiment:</th>
                      <td><b>${broker.sentiment}</b></td>
                    </tr>
                    <tr>
                      <th scope="row"><i class="bi bi-graph-up-arrow me-1" aria-hidden="true"></i>Return On Investment:</th>
                      <td><b>${returnOnInvestment}</b></td>
                    </tr>
                  </tbody>
                </table>
              </div>
              <div class="d-grid gap-2 d-md-flex justify-content-md-center">
                <button type="submit" name="submit" value="OK" class="btn btn-primary">
                  <i class="bi bi-arrow-left-circle me-2" aria-hidden="true"></i>OK
                </button>
                <button type="submit" name="submit" value="Buy/Sell Stock" class="btn btn-success">
                  <i class="bi bi-currency-exchange me-2" aria-hidden="true"></i>Buy/Sell Stock
                </button>
                <button type="submit" name="submit" value="Submit Feedback" class="btn btn-outline-secondary">
                  <i class="bi bi-chat-dots me-2" aria-hidden="true"></i>Submit Feedback
                </button>
              </div>
              <% } //else %>
            </form>
          </div>
        </div>
        <div class="card-footer text-center bg-white border-0">
          <a href="https://github.com/IBMStockTrader">
            <img src="<%=footerImage%>" alt="footer image" class="footer-img"/>
          </a>
        </div>
      </div>
    </div>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <script src="${pageContext.request.contextPath}/js/viewPortfolio.js"></script>
  </body>
</html>
