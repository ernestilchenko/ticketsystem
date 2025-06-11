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
        const totalSeats = parseInt(seatSelection.getAttribute('data-total-seats') || 0);
        const eventId = seatSelection.getAttribute('data-event-id');
        const seatContainer = document.getElementById('seat-container');

        if (seatContainer && totalSeats > 0) {

            fetchOccupiedSeats(eventId).then(occupiedSeats => {
                generateSeatMap(totalSeats, occupiedSeats, seatContainer);
            });
        }
    }

    // Funkcja pobierająca zajęte miejsca z serwera
    async function fetchOccupiedSeats(eventId) {
        try {
            const response = await fetch(`/api/events/${eventId}/occupied-seats`);
            if (response.ok) {
                return await response.json();
            }
        } catch (error) {
            console.error('Error fetching occupied seats:', error);
        }
        return [];
    }

    // Funkcja generująca mapę miejsc
    function generateSeatMap(totalSeats, occupiedSeats, container) {
        container.innerHTML = ''; 
        
        let seatRows = Math.ceil(Math.sqrt(totalSeats));
        let seatsPerRow = Math.ceil(totalSeats / seatRows);


        const legend = document.createElement('div');
        legend.className = 'mb-3 text-center';
        legend.innerHTML = `
            <div class="d-flex justify-content-center gap-3">
                <span><i class="bi bi-square text-success"></i> Dostępne</span>
                <span><i class="bi bi-square-fill text-primary"></i> Wybrane</span>
                <span><i class="bi bi-square-fill text-danger"></i> Zajęte</span>
            </div>
        `;
        container.appendChild(legend);

        for (let row = 0; row < seatRows; row++) {
            const rowDiv = document.createElement('div');
            rowDiv.className = 'd-flex justify-content-center my-2';
            
            const rowLabel = document.createElement('div');
            rowLabel.className = 'align-self-center me-2 fw-bold text-muted';
            rowLabel.textContent = `${row + 1}`;
            rowLabel.style.width = '20px';
            rowDiv.appendChild(rowLabel);

            for (let seat = 0; seat < seatsPerRow; seat++) {
                const seatNum = row * seatsPerRow + seat + 1;
                if (seatNum <= totalSeats) {
                    const seatId = `Row ${row + 1}, Seat ${seat + 1}`;
                    const isOccupied = occupiedSeats.includes(seatId);
                    
                    const seatBtn = document.createElement('button');
                    seatBtn.type = 'button';
                    seatBtn.className = 'btn seat-btn m-1';
                    seatBtn.setAttribute('data-seat', seatId);
                    seatBtn.style.width = '40px';
                    seatBtn.style.height = '40px';
                    seatBtn.style.fontSize = '12px';
                    seatBtn.textContent = seat + 1;

                    if (isOccupied) {
                        seatBtn.className += ' btn-danger';
                        seatBtn.disabled = true;
                        seatBtn.setAttribute('data-bs-toggle', 'tooltip');
                        seatBtn.setAttribute('data-bs-title', 'Miejsce zajęte');
                    } else {
                        seatBtn.className += ' btn-outline-success';
                        
                        seatBtn.addEventListener('click', function() {
                            document.querySelectorAll('.seat-btn:not(.btn-danger)').forEach(btn => {
                                btn.classList.remove('btn-primary', 'active');
                                btn.classList.add('btn-outline-success');
                            });

                            this.classList.remove('btn-outline-success');
                            this.classList.add('btn-primary', 'active');

                            const seatInput = document.getElementById('seatNumber');
                            if (seatInput) {
                                seatInput.value = this.getAttribute('data-seat');
                            }
                        });
                    }

                    rowDiv.appendChild(seatBtn);
                }
            }

            container.appendChild(rowDiv);
        }

        const tooltips = container.querySelectorAll('[data-bs-toggle="tooltip"]');
        tooltips.forEach(tooltip => {
            new bootstrap.Tooltip(tooltip);
        });
    }

    // Walidacja formularza przed wysłaniem
    const purchaseForm = document.querySelector('form[action*="/purchase"]');
    const reserveForm = document.querySelector('form[action*="/reserve"]');
    
    if (purchaseForm) {
        purchaseForm.addEventListener('submit', function(e) {
            const seatInput = document.getElementById('seatNumber');
            const selectedSeat = seatInput ? seatInput.value : '';
            
            if (!selectedSeat || selectedSeat === 'General Admission') {
                if (seatInput) {
                    seatInput.value = 'General Admission';
                }
                return true;
            }
            
            e.preventDefault();
            checkSeatAndSubmit(this, selectedSeat);
        });
    }
    
    if (reserveForm) {
        reserveForm.addEventListener('submit', function(e) {
            const seatInput = document.getElementById('seatNumber');
            const selectedSeat = seatInput ? seatInput.value : '';
            
            if (!selectedSeat || selectedSeat === 'General Admission') {
                if (seatInput) {
                    seatInput.value = 'General Admission';
                }
                return true;
            }
            
            e.preventDefault();
            checkSeatAndSubmit(this, selectedSeat);
        });
    }
    
    async function checkSeatAndSubmit(form, seatNumber) {
        const eventId = document.getElementById('seat-selection')?.getAttribute('data-event-id');
        
        if (!eventId) {
            form.submit();
            return;
        }
        
        try {
            const response = await fetch(`/api/events/${eventId}/check-seat`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(seatNumber)
            });
            
            if (response.ok) {
                const isAvailable = await response.json();
                if (isAvailable) {
                    form.submit();
                } else {
                    alert('Wybrane miejsce zostało właśnie zajęte przez innego użytkownika. Proszę wybrać inne miejsce.');
                    location.reload();
                }
            } else {
                form.submit(); 
            }
        } catch (error) {
            console.error('Error checking seat availability:', error);
            form.submit();
        }
    }
});