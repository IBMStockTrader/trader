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
    <link href="https://fonts.googleapis.com/css?family=Montserrat:700&display=swap" rel="stylesheet">
    <style>
      body,
      h1,
      h2,
      h3,
      h4,
      h5,
      h6,
      label,
      input,
      button,
      .form-label,
      .form-control {
        font-family: "Roboto", Arial, Helvetica, system-ui, sans-serif;
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
        max-width: 350px;
        margin-left: auto;
        margin-right: auto;
      }
      /* Remove green validation from radio buttons */
      .was-validated .form-check-input:valid {
        border-color: #ced4da;
        box-shadow: none;
      }
      .was-validated .form-check-input:valid:checked {
        background-color: #0d6efd;
        border-color: #0d6efd;
      }
    </style>
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
            <div class="mb-2 card-title h4 text-center">Add Stock</div>
          </div>
          <div class="form-inner">
            <form method="post" class="needs-validation" novalidate>
              <div class="d-flex mb-2 align-items-center">
                <span class="fw-bold me-2" style="min-width: 100px"
                  >Owner:</span
                >
                <span>${param.owner}</span>
              </div>
              <div class="d-flex mb-3 align-items-center">
                <span class="fw-bold me-2" style="min-width: 100px"
                  >Commission:</span
                >
                <span>${commission}</span>
              </div>
              <div class="mb-3">
                <label for="symbol" class="form-label"
                  ><b>Stock Symbol:</b></label
                >
                <input
                  type="text"
                  class="form-control"
                  id="symbol"
                  name="symbol"
                  required
                />
                <div class="invalid-feedback">Please enter a stock symbol.</div>
              </div>
              <div class="mb-3">
                <label for="shares" class="form-label"
                  ><b>Number of Shares:</b></label
                >
                <input
                  type="number"
                  class="form-control"
                  id="shares"
                  name="shares"
                  min="1"
                  required
                />
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
                >
                  Submit
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
    <script>
      // --- Bootstrap client-side validation ---
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
