// Initialize interactive features when page loads
document.addEventListener('DOMContentLoaded', function() {
    initializeFavoriteButtons();
    initializeInteractiveElements();
});

// Initialize interactive elements
function initializeInteractiveElements() {
    // Smooth scrolling for anchor links
    document.querySelectorAll('a[href^="#"]').forEach(anchor => {
        anchor.addEventListener('click', function (e) {
            e.preventDefault();
            const target = document.querySelector(this.getAttribute('href'));
            if (target) {
                target.scrollIntoView({
                    behavior: 'smooth',
                    block: 'start'
                });
            }
        });
    });
}

// Initialize favorite buttons
function initializeFavoriteButtons() {
    document.querySelectorAll('.favorite-btn').forEach(button => {
        button.addEventListener('click', function(e) {
            e.preventDefault();
            e.stopPropagation();
            
            const propertyId = this.dataset.propertyId;
            const isFavorite = this.dataset.favorite === 'true';
            
            toggleFavorite(propertyId, !isFavorite, this);
        });
    });
}

// Toggle favorite status
async function toggleFavorite(propertyId, isFavorite, button) {
    try {
        // Show loading state
        button.disabled = true;
        button.classList.add('opacity-50');
        

        await new Promise(resolve => setTimeout(resolve, 300));
        
        // Update button state
        const icon = button.querySelector('i');
        if (isFavorite) {
            icon.className = 'fas fa-heart text-red-500';
            button.dataset.favorite = 'true';
        } else {
            icon.className = 'far fa-heart text-red-500';
            button.dataset.favorite = 'false';
        }
        
        // Show feedback
        showNotification(isFavorite ? 'Added to favorites' : 'Removed from favorites', 'success');
        
    } catch (error) {
        console.error('Error toggling favorite:', error);
        showNotification('Failed to update favorite', 'error');
    } finally {
        button.disabled = false;
        button.classList.remove('opacity-50');
    }
}

// Show notification
function showNotification(message, type = 'info') {
    const notification = document.createElement('div');
    notification.className = `fixed top-4 right-4 z-50 p-4 rounded-md shadow-lg max-w-sm transition-all duration-300 ${
        type === 'success' ? 'bg-green-50 border border-green-200 text-green-800' :
        type === 'error' ? 'bg-red-50 border border-red-200 text-red-800' :
        'bg-blue-50 border border-blue-200 text-blue-800'
    }`;
    
    notification.innerHTML = `
        <div class="flex items-center">
            <i class="fas ${type === 'success' ? 'fa-check-circle' : type === 'error' ? 'fa-exclamation-circle' : 'fa-info-circle'} mr-2"></i>
            <span>${message}</span>
            <button onclick="this.parentElement.parentElement.remove()" class="ml-auto text-lg leading-none">&times;</button>
        </div>
    `;
    
    document.body.appendChild(notification);
    
    // Auto remove after 3 seconds
    setTimeout(() => {
        if (notification.parentNode) {
            notification.style.opacity = '0';
            notification.style.transform = 'translateX(100%)';
            setTimeout(() => notification.remove(), 300);
        }
         }, 3000);
}

// Add CSS utilities for the property cards
const style = document.createElement('style');
style.textContent = `
    .line-clamp-1 {
        display: -webkit-box;
        -webkit-line-clamp: 1;
        -webkit-box-orient: vertical;
        overflow: hidden;
    }
`;
document.head.appendChild(style);

// TODO: Image carousel functionality for multiple property images
function initializeImageCarousels() {
    document.querySelectorAll('.property-carousel').forEach(carousel => {
        const leftBtn = carousel.querySelector('.carousel-left');
        const rightBtn = carousel.querySelector('.carousel-right');
        // Implementation for when we have multiple images per property
    });
}
