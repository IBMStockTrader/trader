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
  
  // Add row click functionality for portfolio rows
  const portfolioRows = document.querySelectorAll('.portfolio-row');
  portfolioRows.forEach(function(row) {
    row.addEventListener('click', function(e) {
      // Don't trigger if clicking on action buttons
      if (e.target.closest('.actions-cell') || e.target.closest('button')) {
        return;
      }
      
      const owner = row.getAttribute('data-owner');
      if (owner) {
        // Create and submit form to view portfolio
        const form = document.createElement('form');
        form.method = 'post';
        form.style.display = 'none';
        
        const ownerInput = document.createElement('input');
        ownerInput.type = 'hidden';
        ownerInput.name = 'owner';
        ownerInput.value = owner;
        
        const actionInput = document.createElement('input');
        actionInput.type = 'hidden';
        actionInput.name = 'action';
        actionInput.value = 'retrieve';
        
        const submitInput = document.createElement('input');
        submitInput.type = 'hidden';
        submitInput.name = 'submit';
        submitInput.value = 'Submit';
        
        form.appendChild(ownerInput);
        form.appendChild(actionInput);
        form.appendChild(submitInput);
        document.body.appendChild(form);
        form.submit();
      }
    });
  });
});
