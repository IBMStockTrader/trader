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
      passwordInput.getAttribute("type") === "password" ? "text" : "password";
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
