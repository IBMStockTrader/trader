<%@ page language="java" contentType="text/html; charset=UTF-8" session="false" 
import="java.text.*,java.util.List,java.math.RoundingMode,com.ibm.hybrid.cloud.sample.stocktrader.trader.Utilities,com.ibm.hybrid.cloud.sample.stocktrader.trader.json.*"%>

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
    <!-- Bootstrap 5 CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- Web fonts -->
    <link href="https://fonts.googleapis.com/css?family=Roboto:400,500&display=swap" rel="stylesheet">
    <style>
      body, h1, h2, h3, h4, h5, h6, label, input, button, .form-label, .form-control {
        font-family: 'Roboto', Arial, Helvetica, system-ui, sans-serif;
      }
      .summary-card {
        max-width: 700px;
        margin: 2rem auto;
        padding: 2rem 2rem 1.5rem 2rem;
      }
      .summary-header-img {
        max-width: 100%;
        height: auto;
        display: block;
        margin-left: auto;
        margin-right: auto;
      }
      .summary-footer-img {
        max-width: 300px;
        height: auto;
        margin-top: 2rem;
      }
    </style>
  </head>
  <body class="bg-light">
    <div class="container min-vh-100 d-flex flex-column justify-content-center align-items-center">
      <div class="card shadow-sm summary-card w-100">
        <div class="card-body">
          <img src="<%=headerImage%>" alt="header image" class="summary-header-img mb-3"/>
          <div class="mb-4"></div>
          <% if(request.getAttribute("error") != null && ((Boolean)request.getAttribute("error")).booleanValue() == true) { %>
            <div class="alert alert-danger" role="alert">
              Error communicating with the Broker microservice: ${message}<br/>
              Please consult the <i>trader</i>, <i>broker</i> and <i>portfolio</i> pod logs for more details, or ask your administator for help.
            </div>
          <% } else { %>
          <form method="post">
            <div class="mb-3">
              <div class="form-check">
                <input class="form-check-input" type="radio" name="action" value="retrieve" id="actionRetrieve" checked>
                <label class="form-check-label" for="actionRetrieve">Retrieve selected portfolio</label>
              </div>
              <% if(request.isUserInRole("StockTrader")) { %>
                <div class="form-check">
                  <input class="form-check-input" type="radio" name="action" value="create" id="actionCreate">
                  <label class="form-check-label" for="actionCreate">Create a new portfolio</label>
                </div>
                <div class="form-check">
                  <input class="form-check-input" type="radio" name="action" value="update" id="actionUpdate">
                  <label class="form-check-label" for="actionUpdate">Update selected portfolio (buy/sell stock)</label>
                </div>
                <div class="form-check">
                  <input class="form-check-input" type="radio" name="action" value="delete" id="actionDelete">
                  <label class="form-check-label" for="actionDelete">Delete selected portfolio</label>
                </div>
              <% } %>
            </div>
            <div class="table-responsive mb-3">
              <table class="table table-bordered align-middle">
                <thead class="table-light">
                  <tr>
                    <th scope="col"></th>
                    <th scope="col">Owner</th>
                    <th scope="col">Total</th>
                    <th scope="col">Loyalty Level</th>
                  </tr>
                </thead>
                <tbody>
                  <% List<Broker> brokers = (List<Broker>)request.getAttribute("brokers");
                    if(brokers == null) { %>
                      <tr><td colspan="4" class="text-danger">Error communicating with the Broker microservice: ${message}</td></tr>
                  <% } else {
                    for (int index=0; index<brokers.size(); index++) { 
                      Broker broker = brokers.get(index);
                      String owner = broker.getOwner();
                      Utilities.logToS3(owner, broker);
                  %>
                    <tr>
                      <td><input class="form-check-input" type="radio" name="owner" value="<%=owner%>" <%= ((index ==0)?" checked ": " ") %>></td>
                      <td><%=owner%></td>
                      <td>$<%=currency.format(broker.getTotal())%></td>
                      <td><%=broker.getLoyalty()%></td>
                    </tr>
                  <% } } %>
                </tbody>
              </table>
            </div>
            <div class="d-flex gap-2">
              <button type="submit" name="submit" value="Submit" class="btn btn-primary">Submit</button>
              <button type="submit" name="submit" value="Log Out" class="btn btn-outline-secondary">Log Out</button>
            </div>
          </form>
          <% } %>
        </div>
        <div class="card-footer text-center bg-white border-0">
          <a href="https://github.com/IBMStockTrader">
            <img src="<%=footerImage%>" alt="footer image" class="summary-footer-img"/>
          </a>
        </div>
      </div>
    </div>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
  </body>
</html>
