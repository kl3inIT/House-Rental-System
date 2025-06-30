import { validateEmail } from './main.js';

// DOM Elements
const emailInput = document.getElementById('email');
const submitButton = document.getElementById('submitButton');
const emailError = document.getElementById('emailError');

// Validate email field
function validateEmailField() {
    const email = emailInput.value.trim();
    
    if (!email) {
        showError('Email is required');
        return false;
    }
    
    if (!validateEmail(email)) {
        showError('Please enter a valid email address');
        return false;
    }
    
    hideError();
    return true;
}

// Show error message
function showError(message) {
    if (emailError) {
        emailError.textContent = message;
        emailError.classList.remove('hidden');
    }
}

// Hide error message
function hideError() {
    if (emailError) {
        emailError.classList.add('hidden');
    }
}

// Handle form submission
function handleSubmit(event) {
    if (!validateEmailField()) {
        event.preventDefault();
        return false;
    }
}

// Initialize when DOM is loaded
document.addEventListener('DOMContentLoaded', function() {
    // Event listeners
    if (emailInput) {
        emailInput.addEventListener('blur', validateEmailField);
        emailInput.addEventListener('input', hideError);
    }
    
    // Form submission
    const form = document.querySelector('form[method="POST"]');
    if (form) {
        form.addEventListener('submit', handleSubmit);
    }
}); 