<%@ page language="java" contentType="text/html; charset=UTF-8" session="false"
import="com.ibm.hybrid.cloud.sample.stocktrader.trader.Utilities"%> <%! static
String headerImage = Utilities.getHeaderImage(); static String footerImage =
Utilities.getFooterImage(); static String loginMessage =
Utilities.getLoginMessage(); %>

<!DOCTYPE html>
<html lang="en">
  <head>
    <title>Stock Trader</title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <!-- Bootstrap 5 CSS for responsive, modern UI -->
    <link
      href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css"
      rel="stylesheet"
    />
    <!-- Web fonts: Roboto for industry standard appearance -->
    <link
      href="https://fonts.googleapis.com/css?family=Roboto:400,500&display=swap"
      rel="stylesheet"
    />
    <!-- Montserrat font for brand -->
    <link
      href="https://fonts.googleapis.com/css?family=Montserrat:700&display=swap"
      rel="stylesheet"
    />

    <!-- Bootstrap Icons CDN for eye icon in password toggle -->
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
      href="${pageContext.request.contextPath}/css/login.css"
    />
  </head>
  <body class="bg-light">
    <!-- Main container centers the card vertically and horizontally -->
    <div
      class="container min-vh-100 d-flex flex-column justify-content-center align-items-center"
    >
      <div class="card shadow-sm main-card w-100">
        <div class="card-body p-4">
          <div class="text-center mb-4">
            <!-- Header image loaded dynamically -->
            <img
              src="<%=headerImage%>"
              alt="header image"
              class="header-img mb-3"
            />
            <!-- Login message from backend utility -->
            <h1 class="login-heading text-center mb-4">
              Login to <span class="brand-main">Stock</span
              ><span class="brand-accent">Trader</span>
            </h1>
          </div>
          <div class="form-inner">
            <!-- Login Form: Bootstrap, accessible, responsive -->
            <form method="post" class="needs-validation" novalidate>
              <div class="mb-3">
                <label for="username" class="form-label">Username</label>
                <div class="input-group">
                  <span class="input-group-text"
                    ><i class="bi bi-person" aria-hidden="true"></i
                  ></span>
                  <input
                    type="text"
                    class="form-control"
                    id="username"
                    name="id"
                    required
                    aria-required="true"
                    autocomplete="username"
                  />
                </div>
                <div class="invalid-feedback">Please enter your username.</div>
              </div>
              <div class="mb-3">
                <label for="password" class="form-label">Password</label>
                <div class="input-group">
                  <span class="input-group-text"
                    ><i class="bi bi-lock" aria-hidden="true"></i
                  ></span>
                  <input
                    type="password"
                    class="form-control"
                    id="password"
                    name="password"
                    required
                    aria-required="true"
                    autocomplete="current-password"
                  />
                  <button
                    class="btn btn-outline-secondary"
                    type="button"
                    id="togglePassword"
                    tabindex="-1"
                    aria-label="Show or hide password"
                  >
                    <span id="togglePasswordIcon" class="bi bi-eye"></span>
                  </button>
                </div>
                <div class="invalid-feedback">Please enter your password.</div>
              </div>
              <div class="d-grid">
                <button
                  type="submit"
                  name="submit"
                  class="btn btn-primary btn-block"
                >
                  <i
                    class="bi bi-box-arrow-in-right me-2"
                    aria-hidden="true"
                  ></i
                  >Login
                </button>
              </div>
            </form>
          </div>
        </div>
        <div class="card-footer text-center bg-white border-0">
          <!-- Footer image loaded dynamically -->
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
    <!-- Bootstrap JS for validation (optional, but recommended for client-side feedback) -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <script src="${pageContext.request.contextPath}/js/login.js"></script>
  </body>
</html>
