<%@ page language="java" contentType="text/html; charset=UTF-8" session="false"
import="com.ibm.hybrid.cloud.sample.stocktrader.trader.Utilities"%> <%! static
String headerImage = Utilities.getHeaderImage(); static String footerImage =
Utilities.getFooterImage(); %>

<!DOCTYPE html>
<html lang="en">
  <head>
    <title>Stock Trader</title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
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
      href="${pageContext.request.contextPath}/css/common.css"
    />
    <link
      rel="stylesheet"
      type="text/css"
      href="${pageContext.request.contextPath}/css/addPortfolio.css"
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
              ><span class="brand-accent">Trader</span> Portfolio
            </h1>
            <i
              >This account will receive a free <b>$50</b> balance for
              commissions!</i
            >
          </div>
          <div class="form-inner">
            <form method="post" class="needs-validation" novalidate>
              <div class="mb-3">
                <label for="owner" class="form-label">Owner</label>
                <div class="input-group">
                  <span class="input-group-text"
                    ><i class="bi bi-person" aria-hidden="true"></i
                  ></span>
                  <input
                    type="text"
                    class="form-control"
                    id="owner"
                    name="owner"
                    required
                  />
                </div>
                <div class="invalid-feedback">Please enter the owner name.</div>
              </div>
              <div class="mb-3">
                <label for="balance" class="form-label"
                  >Cash Account initial balance</label
                >
                <div class="input-group">
                  <span class="input-group-text"
                    ><i class="bi bi-cash-stack" aria-hidden="true"></i
                  ></span>
                  <input
                    type="number"
                    class="form-control"
                    id="balance"
                    name="balance"
                    step="0.01"
                    min="0"
                    value="10000.00"
                    required
                  />
                </div>
                <div class="invalid-feedback">
                  Please enter a valid initial balance (0 or greater).
                </div>
              </div>
              <div class="mb-3">
                <label for="currency" class="form-label"
                  >Cash Account currency</label
                >
                <select
                  class="form-select"
                  id="currency"
                  name="currency"
                  required
                >
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
                </select>
                <div class="invalid-feedback">Please select a currency.</div>
              </div>
              <div class="d-grid gap-2">
                <button
                  type="submit"
                  name="submit"
                  class="btn btn-primary btn-block"
                >
                  <i class="bi bi-plus-circle me-2" aria-hidden="true"></i>Add
                  Portfolio
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
              class="footer-img"
            />
          </a>
        </div>
      </div>
    </div>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <script src="${pageContext.request.contextPath}/js/addPortfolio.js"></script>
  </body>
</html>
