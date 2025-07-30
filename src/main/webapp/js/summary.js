window.addEventListener("pageshow", function () {
  if (document.activeElement && document.activeElement.tagName === "BUTTON") {
    document.activeElement.blur();
  }
});

let deleteForm = null;
let deleteOwner = null;
let lastDeleteButton = null;

function showDeleteModal(button) {
  deleteOwner = button.getAttribute('data-owner');
  deleteForm = button.closest('form');
  lastDeleteButton = button;
  const modal = new bootstrap.Modal(document.getElementById('deleteConfirmModal'));
  modal.show();
}

document.addEventListener('DOMContentLoaded', function() {
  const confirmBtn = document.getElementById('confirmDeleteBtn');
  const modalEl = document.getElementById('deleteConfirmModal');
  if (confirmBtn) {
    confirmBtn.addEventListener('click', function() {
      if (deleteForm) {
        // Trigger the hidden submit button for proper parameter handling
        const submitBtn = deleteForm.querySelector('button[type="submit"][name="submit"]');
        if (submitBtn) submitBtn.click();
      }
      // Hide the modal
      const modal = bootstrap.Modal.getInstance(document.getElementById('deleteConfirmModal'));
      if (modal) modal.hide();
    });
  }
  if (modalEl) {
    modalEl.addEventListener('hidden.bs.modal', function () {
      // Return focus to the button that opened the modal
      if (lastDeleteButton) {
        lastDeleteButton.focus();
      }
    });
  }
});
