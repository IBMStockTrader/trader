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
    <!-- Bootstrap Icons for heading and button -->
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
      href="${pageContext.request.contextPath}/css/submitFeedback.css"
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
          </div>
          <div class="form-inner">
            <div class="mb-3 text-center">
              <h1 class="page-heading mb-2">
                <i
                  class="bi bi-chat-dots text-primary me-2"
                  aria-hidden="true"
                ></i>
                <span class="brand-main">Stock</span
                ><span class="brand-accent">Trader</span> Feedback
              </h1>
              <i>Please share your feedback on this tool!</i>
            </div>
            <form method="post" class="needs-validation" novalidate>
              <div class="mb-3">
                <label for="feedback" class="form-label">Feedback</label>
                <textarea
                  class="form-control"
                  id="feedback"
                  name="feedback"
                  rows="7"
                  required
                  minlength="5"
                ></textarea>
                <div class="invalid-feedback">
                  Please enter your feedback (at least 5 characters).
                </div>
              </div>
              <div class="d-grid gap-2 d-md-flex justify-content-md-center">
                <button
                  type="submit"
                  name="submit"
                  value="Submit"
                  class="btn btn-primary"
                >
                  <i class="bi bi-send me-2" aria-hidden="true"></i>Submit
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
    <script src="${pageContext.request.contextPath}/js/submitFeedback.js"></script>
  </body>
</html>
