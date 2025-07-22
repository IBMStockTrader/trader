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

// --- Dynamic button label for Buy/Sell ---
document.addEventListener("DOMContentLoaded", function () {
  const buyRadio = document.getElementById("buyRadio");
  const sellRadio = document.getElementById("sellRadio");
  const actionButton = document.getElementById("actionButton");

  function updateButton() {
    if (sellRadio.checked) {
      actionButton.innerHTML =
        '<i class="bi bi-dash-circle me-2" aria-hidden="true"></i>Sell Stock';
    } else {
      actionButton.innerHTML =
        '<i class="bi bi-plus-circle me-2" aria-hidden="true"></i>Buy Stock';
    }
  }

  buyRadio.addEventListener("change", updateButton);
  sellRadio.addEventListener("change", updateButton);
  updateButton();
});
