export function toggleElement(elementId, show = true) {
    const element = document.getElementById(elementId);
    if (element) {
        element.style.display = show ? 'block' : 'none';
    }
}

export function checkPasswordStrength(password) {
    const strength = {
        length: password.length >= 8,
        hasLetter: /[a-zA-Z]/.test(password),
        hasNumber: /\d/.test(password),
        hasUppercase: /[A-Z]/.test(password)
    };
    const score = Object.values(strength).filter(Boolean).length;
    return {
        score,
        isStrong: score >= 3,
        feedback: {
            length: !strength.length ? 'At least 8 characters' : '',
            hasLetter: !strength.hasLetter ? 'Include letters' : '',
            hasNumber: !strength.hasNumber ? 'Include numbers' : '',
            hasUppercase: !strength.hasUppercase ? 'Include an uppercase letter' : ''
        }
    };
}

export function validateEmail(email) {
    const regex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return regex.test(email);
}

export function validateForm(formId) {
    const form = document.getElementById(formId);
    if (!form) return false;

    const inputs = form.querySelectorAll('input[required], select[required], textarea[required]');
    let isValid = true;

    inputs.forEach(input => {
        if (!input.value.trim()) {
            isValid = false;
            input.classList.add('border-red-500');
            const errorMessage = input.parentElement.querySelector('.text-red-500');
            if (errorMessage) {
                errorMessage.textContent = 'This field is required';
            }
        } else {
            input.classList.remove('border-red-500');
            const errorMessage = input.parentElement.querySelector('.text-red-500');
            if (errorMessage) {
                errorMessage.textContent = '';
            }
        }
    });

    return isValid;
}

// Header functionality
function initializeHeader() {
    // Mobile menu toggle
    const mobileMenuButton = document.querySelector('.mobile-menu-button');
    const mobileMenu = document.querySelector('.mobile-menu');

    if (mobileMenuButton && mobileMenu) {
        mobileMenuButton.addEventListener('click', () => {
            mobileMenu.classList.toggle('hidden');
            
            // Change hamburger icon to X when menu is open
            const icon = mobileMenuButton.querySelector('i');
            if (mobileMenu.classList.contains('hidden')) {
                icon.className = 'fas fa-bars';
            } else {
                icon.className = 'fas fa-times';
            }
        });

        // Close mobile menu when clicking outside
        document.addEventListener('click', (event) => {
            if (!mobileMenuButton.contains(event.target) && !mobileMenu.contains(event.target)) {
                mobileMenu.classList.add('hidden');
                const icon = mobileMenuButton.querySelector('i');
                icon.className = 'fas fa-bars';
            }
        });
    }

    // User dropdown functionality for mobile-like behavior
    const userDropdowns = document.querySelectorAll('.group');
    userDropdowns.forEach(dropdown => {
        const button = dropdown.querySelector('button');
        const menu = dropdown.querySelector('.absolute');
        
        if (button && menu) {
            button.addEventListener('click', (e) => {
                e.stopPropagation();
                
                // Close other dropdowns
                userDropdowns.forEach(otherDropdown => {
                    if (otherDropdown !== dropdown) {
                        const otherMenu = otherDropdown.querySelector('.absolute');
                        if (otherMenu) {
                            otherMenu.classList.add('opacity-0', 'invisible');
                            otherMenu.classList.remove('opacity-100', 'visible');
                        }
                    }
                });
                
                // Toggle current dropdown
                menu.classList.toggle('opacity-0');
                menu.classList.toggle('invisible');
                menu.classList.toggle('opacity-100');
                menu.classList.toggle('visible');
            });
        }
    });

    // Close dropdowns when clicking outside
    document.addEventListener('click', () => {
        userDropdowns.forEach(dropdown => {
            const menu = dropdown.querySelector('.absolute');
            if (menu) {
                menu.classList.add('opacity-0', 'invisible');
                menu.classList.remove('opacity-100', 'visible');
            }
        });
    });
}

// Property heart toggle functionality
function initializePropertyInteractions() {
    const heartButtons = document.querySelectorAll('button i.fa-heart');
    
    heartButtons.forEach(heartIcon => {
        const button = heartIcon.parentElement;
        
        button.addEventListener('click', (e) => {
            e.preventDefault();
            e.stopPropagation();
            
            // Toggle between outline and filled heart
            if (heartIcon.classList.contains('far')) {
                heartIcon.classList.remove('far');
                heartIcon.classList.add('fas');
                button.classList.remove('text-gray-400');
                button.classList.add('text-red-500');
                
                // Add animation
                heartIcon.style.transform = 'scale(1.2)';
                setTimeout(() => {
                    heartIcon.style.transform = 'scale(1)';
                }, 150);
                
                // You could add API call here to save favorite
                console.log('Added to favorites');
            } else {
                heartIcon.classList.remove('fas');
                heartIcon.classList.add('far');
                button.classList.remove('text-red-500');
                button.classList.add('text-gray-400');
                
                // You could add API call here to remove favorite
                console.log('Removed from favorites');
            }
        });
    });
}

// Search functionality
function initializeSearch() {
    const searchForm = document.querySelector('form');
    
    if (searchForm && searchForm.querySelector('input[placeholder*="city"]')) {
        searchForm.addEventListener('submit', (e) => {
            e.preventDefault();
            
            const location = searchForm.querySelector('input[placeholder*="city"]').value;
            const propertyType = searchForm.querySelector('select').value;
            const maxPrice = searchForm.querySelectorAll('select')[1].value;
            
            // Build search URL
            const params = new URLSearchParams();
            if (location) params.set('location', location);
            if (propertyType && propertyType !== 'Any Type') params.set('type', propertyType);
            if (maxPrice && maxPrice !== 'Any Price') params.set('price', maxPrice);
            
            // Redirect to search results
            window.location.href = `/properties?${params.toString()}`;
        });
    }
}

document.addEventListener('DOMContentLoaded', () => {
    initializeHeader();
    initializePropertyInteractions();
    initializeSearch();

    // Tooltip functionality
    const tooltips = document.querySelectorAll('[data-tooltip]');
    tooltips.forEach(tooltip => {
        tooltip.addEventListener('mouseenter', (e) => {
            const text = e.target.dataset.tooltip;
            const tooltipElement = document.createElement('div');
            tooltipElement.className = 'absolute z-10 px-2 py-1 text-sm text-white bg-gray-900 rounded shadow-lg';
            tooltipElement.textContent = text;
            tooltipElement.style.top = '100%';
            tooltipElement.style.left = '0';
            tooltipElement.style.marginTop = '0.25rem';
            tooltipElement.style.whiteSpace = 'nowrap';
            tooltipElement.setAttribute('data-tooltip-box', '');
            e.target.appendChild(tooltipElement);
        });

        tooltip.addEventListener('mouseleave', (e) => {
            const tooltipElement = e.target.querySelector('[data-tooltip-box]');
            if (tooltipElement) {
                tooltipElement.remove();
            }
        });
    });

    // Smooth scroll for anchor links
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
});
