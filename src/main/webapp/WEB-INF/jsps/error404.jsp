<%@ page isErrorPage="true" %>
<!DOCTYPE html>
<html lang="en">
  <head>
    <title>404 Not Found - Stock Trader</title>
    <meta charset="UTF-8" />
    <link
      href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css"
      rel="stylesheet"
    />
    <link
      rel="stylesheet"
      href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css"
    />
    <link
      rel="stylesheet"
      type="text/css"
      href="${pageContext.request.contextPath}/css/error404.css"
    />
  </head>
  <body>
    <%@ include file="/WEB-INF/jsps/partials/navbar.jspf" %>
    <div class="container notfound-container text-center">
      <div class="notfound-icon mb-3">
        <i class="bi bi-exclamation-triangle-fill"></i>
      </div>
      <h1 class="display-4">404</h1>
      <p class="lead">Sorry, the page you are looking for does not exist.</p>
      <a href="/trader/summary" class="btn btn-primary mt-3">
        <i class="bi bi-house-door me-2"></i>Back to Home
      </a>
    </div>
  </body>
</html>
