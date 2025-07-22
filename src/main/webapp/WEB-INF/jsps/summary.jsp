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
    <!-- Montserrat font for brand -->
    <link href="https://fonts.googleapis.com/css?family=Montserrat:700&display=swap" rel="stylesheet">
    <!-- Bootstrap Icons for table and buttons -->
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css">
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
      .table-hover tbody tr:hover {
        background-color: #f6f9fc;
      }
      .brand-main {
        color: #222;
      }
      .brand-accent {
        color: #0d6efd;
        margin-left: 2px;
      }
    </style>
  </head>
  <body class="bg-light">
    <%@ include file="/WEB-INF/jsps/partials/navbar.jspf" %>
    <div class="container min-vh-100 d-flex flex-column justify-content-center align-items-center">
      <div class="card shadow-sm summary-card w-100">
        <div class="card-body">
          <div class="mb-3 text-center">
            <h1 class="page-heading mb-2">
              <i class="bi bi-bar-chart-fill text-primary me-2" aria-hidden="true"></i>
              <span class="brand-main">Stock</span><span class="brand-accent">Trader</span> Portfolio Summary
            </h1>
          </div>
          <img src="<%=headerImage%>" alt="header image" class="summary-header-img mb-3"/>
          <div class="mb-4"></div>
          <% if(request.getAttribute("error") != null && ((Boolean)request.getAttribute("error")).booleanValue() == true) { %>
            <div class="alert alert-danger" role="alert">
              Error communicating with the Broker microservice: ${message}<br/>
              Please consult the <i>trader</i>, <i>broker</i> and <i>portfolio</i> pod logs for more details, or ask your administator for help.
            </div>
          <% } else { %>
          <form method="post">
            <div class="border rounded p-3 mb-4 bg-white shadow-sm">
              <% List<Broker> brokers = (List<Broker>)request.getAttribute("brokers"); %>
              <% boolean noPortfolios = (brokers != null && brokers.isEmpty()); %>
              <style>
                .portfolio-actions-heading {
                  font-family: 'Montserrat', Arial, sans-serif;
                  font-size: 1.3rem;
                  font-weight: 700;
                  letter-spacing: 1.5px;
                  background: linear-gradient(90deg, #0d6efd 60%, #6610f2 100%);
                  -webkit-background-clip: text;
                  -webkit-text-fill-color: transparent;
                  background-clip: text;
                  text-fill-color: transparent;
                  display: inline-block;
                }
                .portfolio-actions-divider {
                  border: none;
                  border-top: 2px solid #e3e6ea;
                  margin: 0.5rem 0 1.2rem 0;
                }
              </style>
              <div class="mb-2">
                <span class="portfolio-actions-heading">
                  <i class="bi bi-ui-checks-grid me-2"></i>Portfolio Actions
                </span>
                <hr class="portfolio-actions-divider"/>
              </div>
              <div class="form-check mb-2">
                <input class="form-check-input" type="radio" name="action" value="retrieve" id="actionRetrieve" <%= noPortfolios ? "disabled" : "checked" %>>
                <label class="form-check-label<%= noPortfolios ? " text-muted" : "" %> fw-semibold fs-6" for="actionRetrieve">
                  <i class="bi bi-search me-1 text-primary"></i>Retrieve selected portfolio
                </label>
              </div>
              <% if(request.isUserInRole("StockTrader")) { %>
                <div class="form-check mb-2">
                  <input class="form-check-input" type="radio" name="action" value="create" id="actionCreate" <%= noPortfolios ? "checked" : "" %>>
                  <label class="form-check-label fw-semibold fs-6" for="actionCreate">
                    <i class="bi bi-plus-circle me-1 text-success"></i>Create a new portfolio
                  </label>
                </div>
                <div class="form-check mb-2">
                  <input class="form-check-input" type="radio" name="action" value="update" id="actionUpdate" <%= noPortfolios ? "disabled" : "" %>>
                  <label class="form-check-label<%= noPortfolios ? " text-muted" : "" %> fw-semibold fs-6" for="actionUpdate">
                    <i class="bi bi-arrow-repeat me-1 text-info"></i>Update selected portfolio (buy/sell stock)
                  </label>
                </div>
                <div class="form-check">
                  <input class="form-check-input" type="radio" name="action" value="delete" id="actionDelete" <%= noPortfolios ? "disabled" : "" %>>
                  <label class="form-check-label<%= noPortfolios ? " text-muted" : "" %> fw-semibold fs-6" for="actionDelete">
                    <i class="bi bi-trash me-1 text-danger"></i>Delete selected portfolio
                  </label>
                </div>
              <% } %>
              <% if (noPortfolios) { %>
                <div class="alert alert-info mt-2">You don’t have any portfolios yet. Create one to get started!</div>
              <% } %>
            </div>
            <% if (brokers != null && !brokers.isEmpty()) { %>
              <div class="table-responsive mb-3">
                <table class="table table-bordered align-middle table-hover text-center">
                  <thead class="table-light">
                    <tr>
                      <th scope="col"></th>
                      <th scope="col"><i class="bi bi-person" aria-hidden="true"></i> Owner</th>
                      <th scope="col"><i class="bi bi-cash-stack" aria-hidden="true"></i> Total</th>
                      <th scope="col"><i class="bi bi-award" aria-hidden="true"></i> Loyalty Level</th>
                    </tr>
                  </thead>
                  <tbody>
                    <% for (int index=0; index<brokers.size(); index++) { 
                      Broker broker = brokers.get(index);
                      String owner = broker.getOwner();
                      Utilities.logToS3(owner, broker);
                      String loyalty = broker.getLoyalty();
                      String badgeClass = "bg-light text-dark border";
                      if (loyalty != null) {
                        if (loyalty.equalsIgnoreCase("Platinum")) badgeClass = "bg-primary";
                        else if (loyalty.equalsIgnoreCase("Gold")) badgeClass = "bg-warning text-dark";
                        else if (loyalty.equalsIgnoreCase("Silver")) badgeClass = "bg-secondary";
                        else if (loyalty.equalsIgnoreCase("Bronze")) badgeClass = "bg-light text-dark border";
                      }
                    %>
                      <tr>
                        <td><input class="form-check-input" type="radio" name="owner" value="<%=owner%>" <%= ((index ==0)?" checked ": " ") %>></td>
                        <td><%=owner%></td>
                        <td>$<%=currency.format(broker.getTotal())%></td>
                        <td><span class="badge <%=badgeClass%>"><%=loyalty%></span></td>
                      </tr>
                    <% } %>
                  </tbody>
                </table>
              </div>
            <% } %>
            <div class="d-flex gap-2">
              <button type="submit" name="submit" value="Submit" class="btn btn-primary">
                <i class="bi bi-arrow-right-circle me-2" aria-hidden="true"></i>Submit
              </button>
              <button type="submit" name="submit" value="Log Out" class="btn btn-outline-secondary">
                <i class="bi bi-box-arrow-right me-2" aria-hidden="true"></i>Log Out
              </button>
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
