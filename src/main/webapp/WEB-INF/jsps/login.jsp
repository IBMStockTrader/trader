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
    <style>
      /* Apply Roboto and fallback fonts to all relevant elements */
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
      .card {
        padding: 1.5rem;
      }
      .card-title.h4.text-center {
        font-size: 1.5rem;
        font-weight: 500;
        color: #2c3e50;
        margin-top: 1rem;
        margin-bottom: 1rem;
        letter-spacing: 0.5px;
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
    </style>
    <!-- Bootstrap Icons CDN for eye icon in password toggle -->
    <link
      rel="stylesheet"
      href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css"
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
            <div class="mb-2 card-title h4 text-center">
              <%=loginMessage%>
            </div>
          </div>
          <div class="form-inner">
            <!-- Login Form: Bootstrap, accessible, responsive -->
            <form method="post" class="needs-validation" novalidate>
              <div class="mb-3">
                <label for="username" class="form-label">Username</label>
                <input
                  type="text"
                  class="form-control"
                  id="username"
                  name="id"
                  required
                  aria-required="true"
                  autocomplete="username"
                />
                <div class="invalid-feedback">
                  Please enter your username.
                </div>
              </div>
              <div class="mb-3">
                <label for="password" class="form-label">Password</label>
                <div class="input-group">
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
                <div class="invalid-feedback">
                  Please enter your password.
                </div>
              </div>
              <div class="d-grid">
                <button
                  type="submit"
                  name="submit"
                  class="btn btn-primary btn-block"
                >
                  Submit
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
              class="img-fluid mt-3"
            />
          </a>
        </div>
      </div>
    </div>
    <!-- Bootstrap JS for validation (optional, but recommended for client-side feedback) -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <script>
      // --- Bootstrap client-side validation ---
      // This script enables Bootstrap's validation feedback for forms.
      // It prevents form submission if fields are invalid and adds the 'was-validated' class for styling.
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

      // --- Show/hide password toggle ---
      // This script toggles the password field between 'password' and 'text' types
      // when the eye icon button is clicked. It also switches the icon between
      // 'bi-eye' and 'bi-eye-slash' for visual feedback.
      const passwordInput = document.getElementById("password");
      const togglePasswordBtn = document.getElementById("togglePassword");
      const togglePasswordIcon = document.getElementById("togglePasswordIcon");
      if (passwordInput && togglePasswordBtn && togglePasswordIcon) {
        togglePasswordBtn.addEventListener("click", function () {
          const type =
            passwordInput.getAttribute("type") === "password"
              ? "text"
              : "password";
          passwordInput.setAttribute("type", type);
          // Toggle icon
          if (type === "text") {
            togglePasswordIcon.classList.remove("bi-eye");
            togglePasswordIcon.classList.add("bi-eye-slash");
          } else {
            togglePasswordIcon.classList.remove("bi-eye-slash");
            togglePasswordIcon.classList.add("bi-eye");
          }
        });
      }
    </script>
  </body>
</html>
