// Landlord Dashboard JavaScript
document.addEventListener('DOMContentLoaded', function () {
    initializeDashboard();
    initializeCharts();
    initializeQuickActions();
});

function initializeDashboard() {
    // Initialize dashboard components
    initializeStatistics();
    initializeRecentActivity();
    initializeNotifications();
}

function initializeStatistics() {
    // Update statistics with animation
    const statElements = document.querySelectorAll('.stat-number');

    statElements.forEach(element => {
        const finalValue = parseInt(element.getAttribute('data-value') || '0');
        animateNumber(element, 0, finalValue, 1000);
    });
}

function animateNumber(element, start, end, duration) {
    const startTime = performance.now();

    function updateNumber(currentTime) {
        const elapsed = currentTime - startTime;
        const progress = Math.min(elapsed / duration, 1);

        // Easing function for smooth animation
        const easeOutQuart = 1 - Math.pow(1 - progress, 4);
        const currentValue = Math.floor(start + (end - start) * easeOutQuart);

        element.textContent = currentValue.toLocaleString();

        if (progress < 1) {
            requestAnimationFrame(updateNumber);
        }
    }

    requestAnimationFrame(updateNumber);
}

function initializeCharts() {
    // Initialize revenue chart if Chart.js is available
    if (typeof Chart !== 'undefined') {
        initializeRevenueChart();
        initializePropertyChart();
    }
}

function initializeRevenueChart() {
    const ctx = document.getElementById('revenueChart');
    if (!ctx) return;

    const chart = new Chart(ctx, {
        type: 'line',
        data: {
            labels: ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun'],
            datasets: [{
                label: 'Revenue',
                data: [12000, 19000, 15000, 25000, 22000, 30000],
                borderColor: 'rgb(59, 130, 246)',
                backgroundColor: 'rgba(59, 130, 246, 0.1)',
                tension: 0.4,
                fill: true
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                legend: {
                    display: false
                }
            },
            scales: {
                y: {
                    beginAtZero: true,
                    ticks: {
                        callback: function (value) {
                            return '$' + value.toLocaleString();
                        }
                    }
                }
            }
        }
    });
}

function initializePropertyChart() {
    const ctx = document.getElementById('propertyChart');
    if (!ctx) return;

    const chart = new Chart(ctx, {
        type: 'doughnut',
        data: {
            labels: ['Rented', 'Available', 'Maintenance'],
            datasets: [{
                data: [8, 3, 2],
                backgroundColor: [
                    'rgb(34, 197, 94)',
                    'rgb(59, 130, 246)',
                    'rgb(245, 158, 11)'
                ]
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                legend: {
                    position: 'bottom'
                }
            }
        }
    });
}

function initializeQuickActions() {
    // Quick action buttons
    const quickActions = document.querySelectorAll('.quick-action');

    quickActions.forEach(action => {
        action.addEventListener('click', function (e) {
            const actionType = this.getAttribute('data-action');
            handleQuickAction(actionType);
        });
    });
}

function handleQuickAction(actionType) {
    switch (actionType) {
        case 'new-listing':
            window.location.href = '/landlord/properties/new';
            break;
        case 'view-bookings':
            window.location.href = '/landlord/bookings';
            break;
        case 'view-messages':
            window.location.href = '/landlord/messages';
            break;
        case 'view-analytics':
            window.location.href = '/landlord/analytics';
            break;
        default:
            console.log('Unknown action:', actionType);
    }
}

function initializeRecentActivity() {
    // Add click handlers for recent activity items
    const activityItems = document.querySelectorAll('.activity-item');

    activityItems.forEach(item => {
        item.addEventListener('click', function () {
            const activityType = this.getAttribute('data-type');
            const activityId = this.getAttribute('data-id');

            if (activityType === 'booking') {
                window.location.href = `/landlord/bookings/${activityId}`;
            } else if (activityType === 'message') {
                window.location.href = `/landlord/messages/${activityId}`;
            } else if (activityType === 'property') {
                window.location.href = `/landlord/properties/${activityId}`;
            }
        });
    });
}

function initializeNotifications() {
    // Handle notification interactions
    const notificationItems = document.querySelectorAll('.notification-item');

    notificationItems.forEach(item => {
        const markReadBtn = item.querySelector('.mark-read');
        if (markReadBtn) {
            markReadBtn.addEventListener('click', function (e) {
                e.stopPropagation();
                const notificationId = this.getAttribute('data-id');
                markNotificationAsRead(notificationId, item);
            });
        }
    });
}

function markNotificationAsRead(notificationId, element) {
    // Add loading state
    const btn = element.querySelector('.mark-read');
    const originalText = btn.innerHTML;
    btn.innerHTML = '<i class="fas fa-spinner fa-spin"></i>';
    btn.disabled = true;

    // Simulate API call
    setTimeout(() => {
        element.classList.add('opacity-50');
        element.classList.add('line-through');
        btn.innerHTML = '<i class="fas fa-check text-green-500"></i>';

        // Remove from DOM after animation
        setTimeout(() => {
            element.remove();
            updateNotificationCount();
        }, 500);
    }, 1000);
}

function updateNotificationCount() {
    const countElement = document.getElementById('notification-count');
    const notifications = document.querySelectorAll('.notification-item');

    if (countElement) {
        const newCount = Math.max(0, parseInt(countElement.textContent) - 1);
        countElement.textContent = newCount;

        if (newCount === 0) {
            countElement.classList.add('hidden');
        }
    }
}

// Export functions for use in other modules
window.landlordDashboard = {
    animateNumber,
    handleQuickAction,
    markNotificationAsRead
}; 