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
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <!-- Bootstrap 5 CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- Web fonts -->
    <link href="https://fonts.googleapis.com/css?family=Roboto:400,500&display=swap" rel="stylesheet">
    <!-- Montserrat font for brand -->
    <link href="https://fonts.googleapis.com/css?family=Montserrat:700&display=swap" rel="stylesheet">
    <!-- Bootstrap Icons for table and buttons -->
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css">
    <!-- Common CSS -->
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/common.css">
    <!-- Custom CSS for summary page -->
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/summary.css">
  </head>
  <body class="bg-light">
    <%@ include file="/WEB-INF/jsps/partials/navbar.jspf" %>
    <div class="container min-vh-100 d-flex flex-column justify-content-center align-items-center">
      <div class="card shadow-sm summary-card w-100">
        <div class="card-body">
          <div class="mb-3 text-center">
            <img src="<%=headerImage%>" alt="header image" class="summary-header-img mb-3"/>
            <h1 class="page-heading mb-2">
              <i class="bi bi-bar-chart-fill text-primary me-2" aria-hidden="true"></i>
              <span class="brand-main">Stock</span><span class="brand-accent">Trader</span> Portfolio Summary
            </h1>
          </div>
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
                        else if (loyalty.equalsIgnoreCase("Bronze")) badgeClass = "bg-bronze";
                        else if (loyalty.equalsIgnoreCase("Basic")) badgeClass = "bg-light text-dark border";
                      }
                    %>
                      <tr class="portfolio-row" data-owner="<%=owner%>" style="cursor: pointer;">
                        <td><%=owner%></td>
                        <td class="text-end">$<%=currency.format(broker.getTotal())%></td>
                        <td><span class="badge <%=badgeClass%>"><%=loyalty%></span></td>
                        <td class="actions-cell">
                          <!-- Desktop: Show all buttons inline -->
                          <div class="d-none d-md-block">
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
                              <button type="button" class="btn btn-delete btn-sm" title="Delete Portfolio"
                                data-owner="<%=owner%>" onclick="showDeleteModal(this)">
                                <i class="bi bi-trash"></i>
                              </button>
                              <button type="submit" name="submit" value="Submit" style="display:none;"></button>
                            </form>
                          </div>
                          
                          <!-- Mobile: Show dropdown menu -->
                          <div class="dropdown d-md-none">
                            <button class="btn btn-outline-secondary btn-sm dropdown-toggle" type="button" data-bs-toggle="dropdown" aria-expanded="false">
                              <i class="bi bi-three-dots-vertical"></i>
                            </button>
                            <ul class="dropdown-menu dropdown-menu-end">
                              <li>
                                <form method="post" class="dropdown-item-form">
                                  <input type="hidden" name="owner" value="<%=owner%>"/>
                                  <input type="hidden" name="action" value="retrieve"/>
                                  <button type="submit" name="submit" value="Submit" class="dropdown-item">
                                    <i class="bi bi-eye me-2"></i>View Portfolio
                                  </button>
                                </form>
                              </li>
                              <li>
                                <form method="post" class="dropdown-item-form">
                                  <input type="hidden" name="owner" value="<%=owner%>"/>
                                  <input type="hidden" name="action" value="update"/>
                                  <button type="submit" name="submit" value="Submit" class="dropdown-item">
                                    <i class="bi bi-pencil me-2"></i>Edit Portfolio
                                  </button>
                                </form>
                              </li>
                              <li><hr class="dropdown-divider"></li>
                              <li>
                                <form method="post" class="dropdown-item-form">
                                  <input type="hidden" name="owner" value="<%=owner%>"/>
                                  <input type="hidden" name="action" value="delete"/>
                                  <button type="button" class="dropdown-item text-danger" data-owner="<%=owner%>" onclick="showDeleteModal(this)">
                                    <i class="bi bi-trash me-2"></i>Delete Portfolio
                                  </button>
                                  <button type="submit" name="submit" value="Submit" style="display:none;"></button>
                                </form>
                              </li>
                            </ul>
                          </div>
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
            <img src="<%=footerImage%>" alt="footer image" class="footer-img"/>
          </a>
        </div>
      </div>
    </div>
    <!-- Delete Confirmation Modal -->
    <div class="modal fade" id="deleteConfirmModal" tabindex="-1" aria-labelledby="deleteConfirmModalLabel" aria-hidden="true">
      <div class="modal-dialog modal-dialog-centered modal-sm">
        <div class="modal-content">
          <div class="modal-header bg-danger text-white">
            <h5 class="modal-title" id="deleteConfirmModalLabel">
              <i class="bi bi-exclamation-triangle-fill me-2"></i>Confirm Delete
            </h5>
            <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal" aria-label="Close"></button>
          </div>
          <div class="modal-body text-center">
            <p class="mb-0">Are you sure you want to <b>delete</b> this portfolio?</p>
          </div>
          <div class="modal-footer justify-content-center">
            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
            <button type="button" class="btn btn-danger" id="confirmDeleteBtn">
              <i class="bi bi-trash me-1"></i>Delete
            </button>
          </div>
        </div>
      </div>
    </div>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <script src="${pageContext.request.contextPath}/js/summary.js"></script>
  </body>
</html>
