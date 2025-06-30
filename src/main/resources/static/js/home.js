// Simple page functionality
document.addEventListener('DOMContentLoaded', function() {
    initializeFavorites();
});

// Simple favorites
function initializeFavorites() {
    document.querySelectorAll('.favorite-btn').forEach(button => {
        button.addEventListener('click', function(e) {
            e.stopPropagation();
            
            const icon = this.querySelector('i');
            const isFavorite = icon.classList.contains('fas');
            
            if (isFavorite) {
                icon.className = 'far fa-heart text-gray-600 hover:text-red-500 text-sm';
            } else {
                icon.className = 'fas fa-heart text-red-500 text-sm';
            }
        });
    });
}


