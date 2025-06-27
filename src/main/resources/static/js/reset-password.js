import { checkPasswordStrength } from './main.js';

// DOM Elements
const newPasswordInput = document.getElementById('newPassword');
const confirmPasswordInput = document.getElementById('confirmPassword');
const submitButton = document.getElementById('submitButton');
const submitButtonText = document.getElementById('submitButtonText');
const submitSpinner = document.getElementById('submitSpinner');
const newPasswordError = document.getElementById('newPasswordError');
const confirmPasswordError = document.getElementById('confirmPasswordError');
const tokenInput = document.getElementById('token');

let isSubmitting = false;

// Password strength indicator
function createPasswordStrengthIndicator() {
    if (!newPasswordInput) return;
    
    const container = newPasswordInput.parentElement;
    
    // Strength bar
    const strengthBar = document.createElement('div');
    strengthBar.className = 'w-full h-2 bg-gray-200 rounded mt-2';
    strengthBar.id = 'strengthBar';
    
    const strengthFill = document.createElement('div');
    strengthFill.className = 'h-full rounded transition-all duration-300';
    strengthFill.id = 'strengthFill';
    strengthBar.appendChild(strengthFill);
    
    // Strength text
    const strengthText = document.createElement('div');
    strengthText.className = 'text-sm mt-1 font-medium';
    strengthText.id = 'strengthText';
    
    // Requirements list
    const requirementsList = document.createElement('ul');
    requirementsList.className = 'text-xs text-gray-500 mt-2 space-y-1';
    requirementsList.id = 'requirementsList';
    
    const requirements = [
        { key: 'length', text: 'At least 6 characters' },
        { key: 'letters', text: 'Include letters' },
        { key: 'number', text: 'Include numbers' },
        { key: 'uppercase', text: 'Include an uppercase letter' }
    ];
    
    requirements.forEach(req => {
        const li = document.createElement('li');
        li.className = 'requirement-item flex items-center';
        li.innerHTML = `<i class="fas fa-times text-red-500 mr-2 w-3"></i>${req.text}`;
        li.setAttribute('data-requirement', req.key);
        requirementsList.appendChild(li);
    });
    
    container.appendChild(strengthBar);
    container.appendChild(strengthText);
    
    // Add to tooltip container like register.js
    const passwordTooltip = document.getElementById('passwordTooltip');
    if (passwordTooltip) {
        passwordTooltip.appendChild(requirementsList);
        passwordTooltip.style.display = "none";
    }
}

// Update password strength
function updatePasswordStrength() {
    const password = newPasswordInput.value;
    const strengthFill = document.getElementById('strengthFill');
    const strengthText = document.getElementById('strengthText');
    const requirementsList = document.getElementById('requirementsList');
    
    if (!strengthFill || !strengthText || !requirementsList) return;
    
    const strength = checkPasswordStrength(password);
    const percent = (strength.score / 4) * 100;
    
    // Update bar
    strengthFill.style.width = percent + '%';
    
    // Update colors and text
    if (strength.score <= 1) {
        strengthFill.className = 'h-full rounded transition-all duration-300 bg-red-500';
        strengthText.className = 'text-sm mt-1 font-medium text-red-600';
        strengthText.textContent = password ? 'Weak' : '';
    } else if (strength.score === 2) {
        strengthFill.className = 'h-full rounded transition-all duration-300 bg-orange-500';
        strengthText.className = 'text-sm mt-1 font-medium text-orange-600';
        strengthText.textContent = 'Fair';
    } else if (strength.score === 3) {
        strengthFill.className = 'h-full rounded transition-all duration-300 bg-yellow-500';
        strengthText.className = 'text-sm mt-1 font-medium text-yellow-600';
        strengthText.textContent = 'Good';
    } else {
        strengthFill.className = 'h-full rounded transition-all duration-300 bg-green-500';
        strengthText.className = 'text-sm mt-1 font-medium text-green-600';
        strengthText.textContent = 'Strong';
    }
    
    // Update requirements
    const requirements = {
        length: password.length >= 6,
        letters: /[a-zA-Z]/.test(password),
        number: /\d/.test(password),
        uppercase: /[A-Z]/.test(password)
    };
    
    Object.keys(requirements).forEach(key => {
        const item = requirementsList.querySelector(`[data-requirement="${key}"]`);
        if (item) {
            const icon = item.querySelector('i');
            if (requirements[key]) {
                icon.className = 'fas fa-check text-green-500 mr-2 w-3';
                item.classList.remove('text-gray-500');
                item.classList.add('text-green-600');
            } else {
                icon.className = 'fas fa-times text-red-500 mr-2 w-3';
                item.classList.remove('text-green-600');
                item.classList.add('text-gray-500');
            }
        }
    });
}

// Validate password field
function validatePasswordField() {
    const password = newPasswordInput.value;
    
    if (!password) {
        showError(newPasswordError, 'Password is required');
        return false;
    }
    
    if (password.length < 6) {
        showError(newPasswordError, 'Password must be at least 6 characters long');
        return false;
    }
    
    const strength = checkPasswordStrength(password);
    if (strength.score < 3) {
        showError(newPasswordError, 'Password is too weak. Please include uppercase, lowercase, and numbers');
        return false;
    }
    
    hideError(newPasswordError);
    return true;
}

// Validate confirm password field
function validateConfirmPasswordField() {
    const password = newPasswordInput.value;
    const confirmPassword = confirmPasswordInput.value;
    
    if (!confirmPassword) {
        showError(confirmPasswordError, 'Please confirm your password');
        return false;
    }
    
    if (password !== confirmPassword) {
        showError(confirmPasswordError, 'Passwords do not match');
        return false;
    }
    
    hideError(confirmPasswordError);
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

// Update button state
function updateButton() {
    const password = newPasswordInput ? newPasswordInput.value : '';
    const confirmPassword = confirmPasswordInput ? confirmPasswordInput.value : '';
    const token = tokenInput ? tokenInput.value : '';
    
    const strength = checkPasswordStrength(password);
    const isValid = token && password && confirmPassword && 
                   strength.score >= 3 && password === confirmPassword;
    
    if (submitButton) {
        submitButton.disabled = !isValid || isSubmitting;
    }
}

// Handle form submission
function handleSubmit(event) {
    let isValid = true;
    
    if (!validatePasswordField()) {
        isValid = false;
    }
    
    if (!validateConfirmPasswordField()) {
        isValid = false;
    }
    
    if (!isValid) {
        event.preventDefault();
        return false;
    }
    
    if (isSubmitting) {
        event.preventDefault();
        return false;
    }
    
    // Set loading state
    isSubmitting = true;
    if (submitButton) submitButton.disabled = true;
    if (submitButtonText) submitButtonText.textContent = 'Resetting...';
    if (submitSpinner) submitSpinner.style.display = 'inline';
    
    // Reset after timeout (in case of server issues)
    setTimeout(() => {
        isSubmitting = false;
        if (submitButton) submitButton.disabled = false;
        if (submitButtonText) submitButtonText.textContent = 'Reset Password';
        if (submitSpinner) submitSpinner.style.display = 'none';
    }, 10000);
}

// Auto-hide alerts after some time
function autoHideAlerts() {
    const alerts = document.querySelectorAll('.alert-auto-hide');
    alerts.forEach(alert => {
        setTimeout(() => {
            if (alert && alert.parentNode) {
                alert.style.opacity = '0';
                setTimeout(() => {
                    if (alert.parentNode) {
                        alert.parentNode.removeChild(alert);
                    }
                }, 300);
            }
        }, 5000); // Hide after 5 seconds
    });
}

// Initialize
document.addEventListener('DOMContentLoaded', function() {
    // Hide errors initially
    if (newPasswordError) newPasswordError.style.display = 'none';
    if (confirmPasswordError) confirmPasswordError.style.display = 'none';
    if (submitSpinner) submitSpinner.style.display = 'none';
    
    // Create password strength indicator
    createPasswordStrengthIndicator();
    
    // Event listeners
    if (newPasswordInput) {
        newPasswordInput.addEventListener('input', () => {
            updatePasswordStrength();
            updateButton();
            
            // Also check confirm password when new password changes
            if (confirmPasswordInput && confirmPasswordInput.value) {
                const password = newPasswordInput.value;
                const confirmPassword = confirmPasswordInput.value;
                
                if (password !== confirmPassword) {
                    showError(confirmPasswordError, 'Passwords do not match');
                    confirmPasswordInput.classList.add('border-red-500');
                } else {
                    hideError(confirmPasswordError);
                    confirmPasswordInput.classList.remove('border-red-500');
                }
            }
        });
        newPasswordInput.addEventListener('focus', () => {
            hideError(newPasswordError);
            const passwordTooltip = document.getElementById('passwordTooltip');
            if (passwordTooltip) {
                passwordTooltip.style.display = "block";
            }
        });
        newPasswordInput.addEventListener('blur', () => {
            validatePasswordField();
            const passwordTooltip = document.getElementById('passwordTooltip');
            if (passwordTooltip) {
                passwordTooltip.style.display = "none";
            }
        });
    }
    
    if (confirmPasswordInput) {
        confirmPasswordInput.addEventListener('input', () => {
            updateButton();
            // Real-time validation like register.js
            const password = newPasswordInput.value;
            const confirmPassword = confirmPasswordInput.value;
            
            if (confirmPassword && password !== confirmPassword) {
                showError(confirmPasswordError, 'Passwords do not match');
                confirmPasswordInput.classList.add('border-red-500');
            } else {
                hideError(confirmPasswordError);
                confirmPasswordInput.classList.remove('border-red-500');
            }
        });
        confirmPasswordInput.addEventListener('blur', validateConfirmPasswordField);
        confirmPasswordInput.addEventListener('focus', () => {
            hideError(confirmPasswordError);
        });
    }
    
    // Form submission
    const form = document.querySelector('form[method="post"]');
    if (form) {
        form.addEventListener('submit', handleSubmit);
    }
    
    // Initial button state
    updateButton();
    
    // Auto-hide success/error messages
    autoHideAlerts();
});

