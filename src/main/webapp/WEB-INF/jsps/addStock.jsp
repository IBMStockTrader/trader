<%@ page language="java" contentType="text/html; charset=UTF-8" session="false"
import="com.ibm.hybrid.cloud.sample.stocktrader.trader.Utilities"%> <%! static
String headerImage = Utilities.getHeaderImage(); static String footerImage =
Utilities.getFooterImage(); %>

<!DOCTYPE html>
<html lang="en">
  <head>
    <title>Stock Trader</title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    <!-- Bootstrap 5 CSS -->
    <link
      href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css"
      rel="stylesheet"
    />
    <!-- Web fonts -->
    <link
      href="https://fonts.googleapis.com/css?family=Roboto:400,500&display=swap"
      rel="stylesheet"
    />
    <!-- Montserrat font for brand -->
    <link
      href="https://fonts.googleapis.com/css?family=Montserrat:700&display=swap"
      rel="stylesheet"
    />
    <!-- Bootstrap Icons for input fields and buttons -->
    <link
      rel="stylesheet"
      href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css"
    />
    <link
      rel="stylesheet"
      type="text/css"
      href="${pageContext.request.contextPath}/css/addStock.css"
    />
  </head>
  <body class="bg-light">
    <%@ include file="/WEB-INF/jsps/partials/navbar.jspf" %>
    <div
      class="container min-vh-100 d-flex flex-column justify-content-center align-items-center"
    >
      <div class="card shadow-sm main-card w-100">
        <div class="card-body p-4">
          <div class="text-center mb-4">
            <img
              src="<%=headerImage%>"
              alt="header image"
              class="header-img mb-3"
            />
            <h1 class="page-heading text-center mb-4">
              Add <span class="brand-main">Stock</span
              ><span class="brand-accent">Trader</span> Stock
            </h1>
          </div>
          <div class="form-inner">
            <form method="post" class="needs-validation" novalidate>
              <div class="row mb-3">
                <div class="col-12 col-md-6 mb-2 mb-md-0">
                  <div
                    class="bg-light rounded p-2 d-flex align-items-center h-100"
                  >
                    <i class="bi bi-person-circle text-primary me-2 fs-4"></i>
                    <span class="fw-semibold">Owner:</span>
                    <span class="badge bg-primary ms-2 info-badge"
                      >${param.owner}</span
                    >
                  </div>
                </div>
                <div class="col-12 col-md-6">
                  <div
                    class="bg-light rounded p-2 d-flex align-items-center h-100"
                  >
                    <i class="bi bi-cash-coin text-success me-2 fs-4"></i>
                    <span class="fw-semibold">Commission:</span>
                    <span class="badge bg-success ms-2 info-badge"
                      >${commission}</span
                    >
                  </div>
                </div>
              </div>
              <div class="mb-3">
                <label for="symbol" class="form-label"
                  ><b>Stock Symbol:</b></label
                >
                <div class="input-group">
                  <span class="input-group-text"
                    ><i class="bi bi-upc-scan" aria-hidden="true"></i
                  ></span>
                  <input
                    type="text"
                    class="form-control"
                    id="symbol"
                    name="symbol"
                    required
                  />
                </div>
                <div class="invalid-feedback">Please enter a stock symbol.</div>
              </div>
              <div class="mb-3">
                <label for="shares" class="form-label"
                  ><b>Number of Shares:</b></label
                >
                <div class="input-group">
                  <span class="input-group-text"
                    ><i class="bi bi-hash" aria-hidden="true"></i
                  ></span>
                  <input
                    type="number"
                    class="form-control"
                    id="shares"
                    name="shares"
                    min="1"
                    required
                  />
                </div>
                <div class="invalid-feedback">
                  Please enter a valid number of shares (1 or more).
                </div>
              </div>
              <div class="mb-3">
                <label class="form-label"><b>Action:</b></label>
                <div>
                  <div class="form-check form-check-inline">
                    <input
                      class="form-check-input"
                      type="radio"
                      name="action"
                      id="buyRadio"
                      value="Buy"
                      checked
                    />
                    <label class="form-check-label" for="buyRadio">Buy</label>
                  </div>
                  <div class="form-check form-check-inline">
                    <input
                      class="form-check-input"
                      type="radio"
                      name="action"
                      id="sellRadio"
                      value="Sell"
                    />
                    <label class="form-check-label" for="sellRadio">Sell</label>
                  </div>
                </div>
              </div>
              <div class="d-grid gap-2">
                <button
                  type="submit"
                  name="submit"
                  value="Submit"
                  class="btn btn-primary"
                  id="actionButton"
                >
                  <i class="bi bi-plus-circle me-2" aria-hidden="true"></i>Buy
                  Stock
                </button>
                <button
                  type="button"
                  class="btn btn-outline-secondary"
                  onclick="window.history.back();"
                >
                  Cancel
                </button>
              </div>
            </form>
          </div>
        </div>
        <div class="card-footer text-center bg-white border-0">
          <a href="https://github.com/IBMStockTrader">
            <img
              src="<%=footerImage%>"
              alt="footer image"
              class="img-fluid mt-3"
            />
          </a>
        </div>
      </div>
    </div>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <script src="${pageContext.request.contextPath}/js/addStock.js"></script>
  </body>
</html>
