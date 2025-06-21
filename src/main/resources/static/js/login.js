import { validateEmail } from './main.js';

// DOM Elements
const emailInput = document.getElementById('email');
const passwordInput = document.getElementById('password');
const loginButton = document.getElementById('loginButton');
const loginButtonText = document.getElementById('loginButtonText');
const loginSpinner = document.getElementById('loginSpinner');
const togglePasswordBtn = document.getElementById('togglePassword');
const eyeIcon = document.getElementById('eyeIcon');
const emailError = document.getElementById('emailError');
const passwordError = document.getElementById('passwordError');

let isSubmitting = false;

// Validate email
function validateEmailField() {
    const email = emailInput.value.trim();
    
    if (!email) {
        showError(emailError, 'Email is required');
        return false;
    }
    
    if (!validateEmail(email)) {
        showError(emailError, 'Please enter a valid email address');
        return false;
    }
    
    hideError(emailError);
    return true;
}

// Validate password
function validatePasswordField() {
    const password = passwordInput.value;
    
    if (!password) {
        showError(passwordError, 'Password is required');
        return false;
    }
    
    hideError(passwordError);
    return true;
}

// Show/hide error messages
function showError(element, message) {
    if (element) {
        element.textContent = message;
        element.style.display = 'block';
    }
}

function hideError(element) {
    if (element) {
        element.style.display = 'none';
    }
}

// Toggle password visibility
function togglePassword() {
    const isPassword = passwordInput.type === 'password';
    passwordInput.type = isPassword ? 'text' : 'password';
    
    if (eyeIcon) {
        eyeIcon.className = isPassword ? 'fas fa-eye-slash' : 'fas fa-eye';
    }
}

// Update button state
function updateButton() {
    const email = emailInput ? emailInput.value.trim() : '';
    const password = passwordInput ? passwordInput.value : '';
    const isValid = email && validateEmail(email) && password;
    
    if (loginButton) {
        loginButton.disabled = !isValid || isSubmitting;
    }
}

// Handle form submission
function handleSubmit(event) {
    if (!validateEmailField() || !validatePasswordField()) {
        event.preventDefault();
        return false;
    }
    
    if (isSubmitting) {
        event.preventDefault();
        return false;
    }
    
    // Set loading state
    isSubmitting = true;
    if (loginButton) loginButton.disabled = true;
    if (loginButtonText) loginButtonText.textContent = 'Signing in...';
    if (loginSpinner) loginSpinner.style.display = 'inline';
    
    // Reset after timeout
    setTimeout(() => {
        isSubmitting = false;
        if (loginButton) loginButton.disabled = false;
        if (loginButtonText) loginButtonText.textContent = 'Sign in';
        if (loginSpinner) loginSpinner.style.display = 'none';
    }, 10000);
}

// Initialize
document.addEventListener('DOMContentLoaded', function() {
    // Hide errors initially
    if (emailError) emailError.style.display = 'none';
    if (passwordError) passwordError.style.display = 'none';
    if (loginSpinner) loginSpinner.style.display = 'none';
    
    // Event listeners
    if (emailInput) {
        emailInput.addEventListener('input', updateButton);
        emailInput.addEventListener('blur', validateEmailField);
    }
    
    if (passwordInput) {
        passwordInput.addEventListener('input', updateButton);
        passwordInput.addEventListener('blur', validatePasswordField);
    }
    
    if (togglePasswordBtn) {
        togglePasswordBtn.addEventListener('click', (e) => {
            e.preventDefault();
            togglePassword();
        });
    }
    
    // Form submission
    const form = document.querySelector('form[method="post"]');
    if (form) {
        form.addEventListener('submit', handleSubmit);
    }
    
    // Initial button state
    updateButton();
});
