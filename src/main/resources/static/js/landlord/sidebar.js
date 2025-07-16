// Landlord Sidebar JavaScript
document.addEventListener('DOMContentLoaded', function () {
    initializeSidebar();
    initializeResponsiveBehavior();
    initializeActiveStates();
});

function initializeSidebar() {
    const sidebar = document.getElementById('landlord-sidebar');
    const sidebarToggle = document.getElementById('sidebar-toggle');
    const sidebarOverlay = document.getElementById('sidebar-overlay');

    if (!sidebar || !sidebarToggle) return;

    // Toggle sidebar on mobile
    sidebarToggle.addEventListener('click', function (e) {
        e.preventDefault();
        toggleSidebar();
    });

    // Close sidebar when clicking overlay
    if (sidebarOverlay) {
        sidebarOverlay.addEventListener('click', function () {
            closeSidebar();
        });
    }

    // Close sidebar on escape key
    document.addEventListener('keydown', function (e) {
        if (e.key === 'Escape') {
            closeSidebar();
        }
    });
}

function toggleSidebar() {
    const sidebar = document.getElementById('landlord-sidebar');
    const sidebarOverlay = document.getElementById('sidebar-overlay');
    const isOpen = sidebar.classList.contains('translate-x-0');

    if (isOpen) {
        closeSidebar();
    } else {
        openSidebar();
    }
}

function openSidebar() {
    const sidebar = document.getElementById('landlord-sidebar');
    const sidebarOverlay = document.getElementById('sidebar-overlay');

    sidebar.classList.remove('-translate-x-full');
    sidebar.classList.add('translate-x-0');

    if (sidebarOverlay) {
        sidebarOverlay.classList.remove('hidden');
    }

    // Prevent body scroll on mobile
    document.body.style.overflow = 'hidden';
}

function closeSidebar() {
    const sidebar = document.getElementById('landlord-sidebar');
    const sidebarOverlay = document.getElementById('sidebar-overlay');

    sidebar.classList.remove('translate-x-0');
    sidebar.classList.add('-translate-x-full');

    if (sidebarOverlay) {
        sidebarOverlay.classList.add('hidden');
    }

    // Restore body scroll
    document.body.style.overflow = '';
}

function initializeResponsiveBehavior() {
    // Handle window resize
    window.addEventListener('resize', function () {
        const sidebar = document.getElementById('landlord-sidebar');
        const sidebarOverlay = document.getElementById('sidebar-overlay');

        // Auto-close sidebar on larger screens if it was open on mobile
        if (window.innerWidth >= 1024) { // lg breakpoint
            if (sidebarOverlay && !sidebarOverlay.classList.contains('hidden')) {
                closeSidebar();
            }
        }
    });
}

function initializeActiveStates() {
    // Set active state based on current page
    const currentPath = window.location.pathname;
    const sidebarLinks = document.querySelectorAll('#landlord-sidebar a');

    sidebarLinks.forEach(link => {
        const href = link.getAttribute('href');
        if (href && currentPath.includes(href.replace('/landlord', ''))) {
            // Remove active state from all links
            sidebarLinks.forEach(l => {
                l.classList.remove('bg-blue-700', 'text-white');
                l.classList.add('text-gray-300', 'hover:bg-gray-700', 'hover:text-white');
            });

            // Add active state to current link
            link.classList.remove('text-gray-300', 'hover:bg-gray-700', 'hover:text-white');
            link.classList.add('bg-blue-700', 'text-white');
        }
    });
}

// Utility functions for sidebar interactions
function showNotification(message, type = 'info') {
    // Create notification element
    const notification = document.createElement('div');
    notification.className = `fixed top-4 right-4 z-50 p-4 rounded-md shadow-lg max-w-sm transform transition-all duration-300 translate-x-full ${type === 'error' ? 'bg-red-50 border border-red-200 text-red-800' :
            type === 'success' ? 'bg-green-50 border border-green-200 text-green-800' :
                'bg-blue-50 border border-blue-200 text-blue-800'
        }`;

    notification.innerHTML = `
        <div class="flex items-center">
            <div class="flex-shrink-0">
                <i class="fas ${type === 'error' ? 'fa-exclamation-circle' :
            type === 'success' ? 'fa-check-circle' : 'fa-info-circle'
        }"></i>
            </div>
            <div class="ml-3">
                <p class="text-sm font-medium">${message}</p>
            </div>
            <div class="ml-auto pl-3">
                <button type="button" class="inline-flex rounded-md p-1.5 hover:bg-opacity-20 focus:outline-none" onclick="this.parentElement.parentElement.parentElement.remove()">
                    <i class="fas fa-times text-sm"></i>
                </button>
            </div>
        </div>
    `;

    document.body.appendChild(notification);

    // Animate in
    setTimeout(() => {
        notification.classList.remove('translate-x-full');
    }, 100);

    // Auto remove after 5 seconds
    setTimeout(() => {
        if (notification.parentNode) {
            notification.classList.add('translate-x-full');
            setTimeout(() => {
                if (notification.parentNode) {
                    notification.remove();
                }
            }, 300);
        }
    }, 5000);
}

// Export functions for use in other modules
window.landlordSidebar = {
    toggleSidebar,
    openSidebar,
    closeSidebar,
    showNotification
}; 