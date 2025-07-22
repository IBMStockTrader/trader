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
      /* Softer filled action buttons for table */
      .btn-view {
        background-color: #2196f3;
        color: #fff;
        border: none;
      }
      .btn-view:hover, .btn-view:focus {
        background-color: #1565c0;
        color: #fff;
      }
      .btn-update {
        background-color: #009688; /* Teal */
        color: #fff;
        border: none;
      }
      .btn-update:hover, .btn-update:focus {
        background-color: #00796b;
        color: #fff;
      }
      .btn-delete {
        background-color: #e53935;
        color: #fff;
        border: none;
      }
      .btn-delete:hover, .btn-delete:focus {
        background-color: #ab000d;
        color: #fff;
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
            <% List<Broker> brokers = (List<Broker>)request.getAttribute("brokers"); %>
            <% boolean noPortfolios = (brokers != null && brokers.isEmpty()); %>
            <% if(request.isUserInRole("StockTrader")) { %>
              <div class="mb-3 text-end">
                <form method="post" style="display:inline;">
                  <input type="hidden" name="action" value="create"/>
                  <button type="submit" name="submit" value="Submit" class="btn btn-success">
                    <i class="bi bi-plus-circle me-1"></i> Create Portfolio
                  </button>
                </form>
              </div>
            <% } %>
            <% if (noPortfolios) { %>
              <div class="alert alert-info mt-2">You don’t have any portfolios yet. Create one to get started!</div>
            <% } %>
            <% if (brokers != null && !brokers.isEmpty()) { %>
              <div class="table-responsive mb-3">
                <table class="table table-bordered align-middle table-hover text-center">
                  <thead class="table-light">
                    <tr>
                      <th scope="col"><i class="bi bi-person" aria-hidden="true"></i> Owner</th>
                      <th scope="col"><i class="bi bi-cash-stack" aria-hidden="true"></i> Total</th>
                      <th scope="col"><i class="bi bi-award" aria-hidden="true"></i> Loyalty Level</th>
                      <th scope="col" class="text-center">Actions</th>
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
                        <td><%=owner%></td>
                        <td>$<%=currency.format(broker.getTotal())%></td>
                        <td><span class="badge <%=badgeClass%>"><%=loyalty%></span></td>
                        <td>
                          <form method="post" style="display:inline;">
                            <input type="hidden" name="owner" value="<%=owner%>"/>
                            <input type="hidden" name="action" value="retrieve"/>
                            <button type="submit" name="submit" value="Submit" class="btn btn-view btn-sm me-1" title="View Portfolio">
                              <i class="bi bi-eye"></i>
                            </button>
                          </form>
                          <form method="post" style="display:inline;">
                            <input type="hidden" name="owner" value="<%=owner%>"/>
                            <input type="hidden" name="action" value="update"/>
                            <button type="submit" name="submit" value="Submit" class="btn btn-update btn-sm me-1" title="Update Portfolio">
                              <i class="bi bi-pencil"></i>
                            </button>
                          </form>
                          <form method="post" style="display:inline;">
                            <input type="hidden" name="owner" value="<%=owner%>"/>
                            <input type="hidden" name="action" value="delete"/>
                            <button type="submit" name="submit" value="Submit" class="btn btn-delete btn-sm" title="Delete Portfolio" onclick="return confirm('Are you sure you want to delete this portfolio?');">
                              <i class="bi bi-trash"></i>
                            </button>
                          </form>
                        </td>
                      </tr>
                    <% } %>
                  </tbody>
                </table>
              </div>
            <% } %>
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
    <script>
      window.addEventListener('pageshow', function() {
        if (document.activeElement && document.activeElement.tagName === 'BUTTON') {
          document.activeElement.blur();
        }
      });
    </script>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
  </body>
</html>
