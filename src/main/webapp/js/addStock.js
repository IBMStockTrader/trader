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

  // --- Sentiment Analysis Preview ---
  const symbolInput = document.getElementById("symbol");
  const sentimentPreview = document.getElementById("sentimentPreview");
  const sentimentContent = document.getElementById("sentimentContent");
  const sentimentLoading = document.getElementById("sentimentLoading");
  const sentimentError = document.getElementById("sentimentError");
  let sentimentTimeout;

  // Use the trader service proxy endpoint for sentiment API
  const sentimentApiUrl = window.location.origin + "/trader/sentiment";

  function fetchSentiment(symbol) {
    if (!symbol || symbol.trim().length === 0) {
      sentimentPreview.style.display = "none";
      return;
    }

    // Show loading state
    sentimentPreview.style.display = "block";
    sentimentContent.style.display = "none";
    sentimentLoading.style.display = "block";
    sentimentError.style.display = "none";

    // Clear any existing timeout
    if (sentimentTimeout) {
      clearTimeout(sentimentTimeout);
    }

    // Debounce: wait 500ms after user stops typing
    sentimentTimeout = setTimeout(() => {
      const symbolUpper = symbol.trim().toUpperCase();
      const url = `${sentimentApiUrl}/${symbolUpper}`;

      fetch(url, {
        credentials: 'include', // Include cookies for authentication
        headers: {
          'Accept': 'application/json'
        }
      })
        .then(response => {
          if (!response.ok) {
            throw new Error(`HTTP ${response.status}`);
          }
          return response.json();
        })
        .then(data => {
          // Hide loading, show content
          sentimentLoading.style.display = "none";
          sentimentContent.style.display = "block";
          sentimentError.style.display = "none";

          // Update sentiment display
          console.log("Sentiment data received:", data); // Debug log
          const dominant = (data.dominant_sentiment && data.dominant_sentiment !== "null" && data.dominant_sentiment !== "NULL") ? data.dominant_sentiment : "unknown";
          const dominantBadge = document.getElementById("dominantSentiment");
          dominantBadge.textContent = dominant.toUpperCase();
          
          // Set badge color based on sentiment
          dominantBadge.className = "badge";
          if (dominant === "positive") {
            dominantBadge.classList.add("bg-success");
          } else if (dominant === "negative") {
            dominantBadge.classList.add("bg-danger");
          } else {
            dominantBadge.classList.add("bg-secondary");
          }

          // Update scores
          document.getElementById("positiveScore").textContent = 
            (data.positive * 100).toFixed(1) + "%";
          document.getElementById("negativeScore").textContent = 
            (data.negative * 100).toFixed(1) + "%";
          document.getElementById("neutralScore").textContent = 
            (data.neutral * 100).toFixed(1) + "%";
          
          // Update net sentiment and sources
          console.log("sources_analyzed value:", data.sources_analyzed, "type:", typeof data.sources_analyzed); // Debug log
          const netSentiment = data.net_sentiment !== undefined && data.net_sentiment !== null ? data.net_sentiment : 0;
          const sourcesAnalyzed = data.sources_analyzed !== undefined && data.sources_analyzed !== null ? data.sources_analyzed : 0;
          document.getElementById("netSentiment").textContent = 
            (netSentiment > 0 ? "+" : "") + netSentiment.toFixed(3);
          document.getElementById("sourcesAnalyzed").textContent = sourcesAnalyzed;
        })
        .catch(error => {
          console.error("Sentiment fetch error:", error);
          // Hide loading, show error
          sentimentLoading.style.display = "none";
          sentimentContent.style.display = "none";
          sentimentError.style.display = "block";
        });
    }, 500); // 500ms debounce
  }

  // Listen for symbol input changes
  symbolInput.addEventListener("input", function() {
    const symbol = this.value.trim();
    fetchSentiment(symbol);
  });

  // Also fetch on blur (when user leaves the field)
  symbolInput.addEventListener("blur", function() {
    const symbol = this.value.trim();
    if (symbol) {
      fetchSentiment(symbol);
    }
  });
});
