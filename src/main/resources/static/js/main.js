// Utility function to show/hide elements
function toggleElement(elementId, show = true) {
    const element = document.getElementById(elementId);
    if (element) {
        element.style.display = show ? 'block' : 'none';
    }
}

// Utility function to show flash messages
function showFlashMessage(message, type = 'success') {
    const messageContainer = document.getElementById('flash-messages');
    if (messageContainer) {
        const messageElement = document.createElement('div');
        messageElement.className = `bg-${type === 'success' ? 'green' : 'red'}-100 border border-${type === 'success' ? 'green' : 'red'}-400 text-${type === 'success' ? 'green' : 'red'}-700 px-4 py-3 rounded relative`;
        messageElement.setAttribute('role', 'alert');
        
        const messageText = document.createElement('span');
        messageText.className = 'block sm:inline';
        messageText.textContent = message;
        messageElement.appendChild(messageText);

        const closeButton = document.createElement('button');
        closeButton.className = 'absolute top-0 right-0 mt-2 mr-2 text-gray-500 hover:text-gray-700';
        closeButton.innerHTML = '<i class="fas fa-times"></i>';
        closeButton.onclick = () => messageElement.remove();
        messageElement.appendChild(closeButton);

        messageContainer.appendChild(messageElement);

        // Auto remove after 5 seconds
        setTimeout(() => {
            messageElement.remove();
        }, 5000);
    }
}

// Password strength checker
function checkPasswordStrength(password) {
    const strength = {
        length: password.length >= 8,
        hasLetter: /[a-zA-Z]/.test(password),
        hasNumber: /\d/.test(password),
        hasSpecial: /[!@#$%^&*]/.test(password)
    };

    const score = Object.values(strength).filter(Boolean).length;
    return {
        score,
        isStrong: score >= 3,
        feedback: {
            length: password.length < 8 ? 'At least 8 characters' : '',
            hasLetter: !/[a-zA-Z]/.test(password) ? 'Include letters' : '',
            hasNumber: !/\d/.test(password) ? 'Include numbers' : '',
            hasSpecial: !/[!@#$%^&*]/.test(password) ? 'Include special characters' : ''
        }
    };
}

// Form validation
function validateForm(formId) {
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

// Mobile menu toggle
document.addEventListener('DOMContentLoaded', () => {
    const mobileMenuButton = document.querySelector('.mobile-menu-button');
    const mobileMenu = document.querySelector('.mobile-menu');

    if (mobileMenuButton && mobileMenu) {
        mobileMenuButton.addEventListener('click', () => {
            mobileMenu.classList.toggle('hidden');
        });
    }

    // Initialize tooltips
    const tooltips = document.querySelectorAll('[data-tooltip]');
    tooltips.forEach(tooltip => {
        tooltip.addEventListener('mouseenter', (e) => {
            const text = e.target.dataset.tooltip;
            const tooltipElement = document.createElement('div');
            tooltipElement.className = 'absolute z-10 px-2 py-1 text-sm text-white bg-gray-900 rounded shadow-lg';
            tooltipElement.textContent = text;
            e.target.appendChild(tooltipElement);
        });

        tooltip.addEventListener('mouseleave', (e) => {
            const tooltipElement = e.target.querySelector('div');
            if (tooltipElement) {
                tooltipElement.remove();
            }
        });
    });
}); 