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

document.addEventListener('DOMContentLoaded', () => {
    const mobileMenuButton = document.querySelector('.mobile-menu-button');
    const mobileMenu = document.querySelector('.mobile-menu');

    if (mobileMenuButton && mobileMenu) {
        mobileMenuButton.addEventListener('click', () => {
            mobileMenu.classList.toggle('hidden');
        });
    }

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
});
