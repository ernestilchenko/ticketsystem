document.addEventListener('DOMContentLoaded', function() {
    // Initialize tooltips
    const tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
    tooltipTriggerList.map(function (tooltipTriggerEl) {
        return new bootstrap.Tooltip(tooltipTriggerEl);
    });

    // Auto-hide alerts after 5 seconds
    const alerts = document.querySelectorAll('.alert-dismissible');
    alerts.forEach(alert => {
        setTimeout(() => {
            const bsAlert = new bootstrap.Alert(alert);
            bsAlert.close();
        }, 5000);
    });

    // Date time picker initialization for event forms
    const datetimeInputs = document.querySelectorAll('input[type="datetime-local"]');
    datetimeInputs.forEach(input => {
        if (!input.value && input.getAttribute('placeholder') === 'Select date and time') {
            // Set default date time to now + 1 day at noon
            const now = new Date();
            now.setDate(now.getDate() + 1);
            now.setHours(12, 0, 0, 0);

            // Format date to YYYY-MM-DDThh:mm
            const year = now.getFullYear();
            const month = String(now.getMonth() + 1).padStart(2, '0');
            const day = String(now.getDate()).padStart(2, '0');
            const hours = String(now.getHours()).padStart(2, '0');
            const minutes = String(now.getMinutes()).padStart(2, '0');

            input.value = `${year}-${month}-${day}T${hours}:${minutes}`;
        }
    });

    // Form validation
    const forms = document.querySelectorAll('.needs-validation');
    Array.from(forms).forEach(form => {
        form.addEventListener('submit', event => {
            if (!form.checkValidity()) {
                event.preventDefault();
                event.stopPropagation();
            }
            form.classList.add('was-validated');
        }, false);
    });

    // Category filter for events
    const categoryFilter = document.getElementById('category-filter');
    if (categoryFilter) {
        categoryFilter.addEventListener('change', function() {
            const selectedCategory = this.value;
            const eventCards = document.querySelectorAll('.event-item');

            eventCards.forEach(card => {
                const cardCategory = card.getAttribute('data-category');
                if (selectedCategory === 'ALL' || cardCategory === selectedCategory) {
                    card.style.display = 'block';
                } else {
                    card.style.display = 'none';
                }
            });
        });
    }

    // Search functionality
    const searchInput = document.getElementById('search-events');
    if (searchInput) {
        searchInput.addEventListener('input', function() {
            const searchTerm = this.value.toLowerCase();
            const eventCards = document.querySelectorAll('.event-item');

            eventCards.forEach(card => {
                const eventName = card.getAttribute('data-name').toLowerCase();
                const eventLocation = card.getAttribute('data-location').toLowerCase();

                if (eventName.includes(searchTerm) || eventLocation.includes(searchTerm)) {
                    card.style.display = 'block';
                } else {
                    card.style.display = 'none';
                }
            });
        });
    }

    // Confirm deletions
    const deleteButtons = document.querySelectorAll('.btn-delete-confirm');
    deleteButtons.forEach(button => {
        button.addEventListener('click', function(e) {
            if (!confirm('Are you sure you want to delete this item? This action cannot be undone.')) {
                e.preventDefault();
            }
        });
    });

    // Dynamic seat selection for event purchase
    const seatSelection = document.getElementById('seat-selection');
    if (seatSelection) {
        const availableSeats = parseInt(seatSelection.getAttribute('data-available-seats') || 0);
        const seatContainer = document.getElementById('seat-container');

        if (seatContainer && availableSeats > 0) {
            let seatRows = Math.ceil(Math.sqrt(availableSeats));
            let seatsPerRow = Math.ceil(availableSeats / seatRows);

            // Create visual seat map
            for (let row = 0; row < seatRows; row++) {
                const rowDiv = document.createElement('div');
                rowDiv.className = 'd-flex justify-content-center my-2';

                for (let seat = 0; seat < seatsPerRow; seat++) {
                    const seatNum = row * seatsPerRow + seat + 1;
                    if (seatNum <= availableSeats) {
                        const seatBtn = document.createElement('button');
                        seatBtn.type = 'button';
                        seatBtn.className = 'btn btn-outline-primary seat-btn m-1';
                        seatBtn.setAttribute('data-seat', `Row ${row+1}, Seat ${seat+1}`);
                        seatBtn.innerHTML = `<i class="bi bi-square"></i>`;
                        seatBtn.style.width = '40px';
                        seatBtn.style.height = '40px';

                        seatBtn.addEventListener('click', function() {
                            document.querySelectorAll('.seat-btn').forEach(btn => {
                                btn.classList.remove('active');
                                btn.innerHTML = `<i class="bi bi-square"></i>`;
                            });

                            this.classList.add('active');
                            this.innerHTML = `<i class="bi bi-check-square-fill"></i>`;

                            document.getElementById('seatNumber').value = this.getAttribute('data-seat');
                        });

                        rowDiv.appendChild(seatBtn);
                    }
                }

                seatContainer.appendChild(rowDiv);
            }
        }
    }
});