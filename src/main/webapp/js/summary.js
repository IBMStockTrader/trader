window.addEventListener("pageshow", function () {
  if (document.activeElement && document.activeElement.tagName === "BUTTON") {
    document.activeElement.blur();
  }
});

let deleteForm = null;
let deleteOwner = null;

function showDeleteModal(button) {
  deleteOwner = button.getAttribute('data-owner');
  deleteForm = button.closest('form');
  const modal = new bootstrap.Modal(document.getElementById('deleteConfirmModal'));
  modal.show();
}

document.addEventListener('DOMContentLoaded', function() {
  const confirmBtn = document.getElementById('confirmDeleteBtn');
  if (confirmBtn) {
    confirmBtn.addEventListener('click', function() {
      if (deleteForm) {
        // Actually submit the form
        deleteForm.submit();
      }
      // Hide the modal
      const modal = bootstrap.Modal.getInstance(document.getElementById('deleteConfirmModal'));
      if (modal) modal.hide();
    });
  }
});
