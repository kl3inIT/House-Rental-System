import {
    checkPasswordStrength,
    validateEmail,
} from './main.js';

document.addEventListener("DOMContentLoaded", () => {
    const form = document.querySelector("form");
    const emailInput = document.getElementById("email");
    const password = document.getElementById("password");
    const confirmPassword = document.getElementById("confirmPassword");
    const confirmPasswordError = document.getElementById("confirmPasswordError");
    const passwordTooltip = document.getElementById("passwordTooltip");

    const strengthBar = document.createElement("div");
    strengthBar.className = "w-full h-2 bg-gray-200 rounded mt-2";
    const strengthFill = document.createElement("div");
    strengthFill.className = "h-full rounded";
    strengthBar.appendChild(strengthFill);
    password.parentElement.appendChild(strengthBar);

    const requirementsList = document.createElement("ul");
    requirementsList.className = "text-xs text-gray-500 mt-2 space-y-1";
    const requirements = [
        "At least 8 characters",
        "Include letters",
        "Include numbers",
        "Include an uppercase letter"
    ];
    requirements.forEach(req => {
        const li = document.createElement("li");
        li.className = "requirement-item";
        li.innerHTML = `<i class="fas fa-times text-red-500 mr-1"></i>${req}`;
        requirementsList.appendChild(li);
    });
    if (passwordTooltip) {
        passwordTooltip.appendChild(requirementsList);
        passwordTooltip.style.display = "none";
    }

    password.addEventListener("focus", () => {
        passwordTooltip.style.display = "block";
    });
    password.addEventListener("blur", () => {
        passwordTooltip.style.display = "none";
    });

    function updatePasswordStrength() {
        const val = password.value;
        const strength = checkPasswordStrength(val);
        const percent = (strength.score / 4) * 100;
        strengthFill.style.width = percent + "%";

        if (strength.score <= 1) {
            strengthFill.className = "h-full rounded bg-red-500";
        } else if (strength.score === 2 || strength.score === 3) {
            strengthFill.className = "h-full rounded bg-yellow-400";
        } else {
            strengthFill.className = "h-full rounded bg-green-500";
        }

        const reqItems = requirementsList.querySelectorAll('.requirement-item');
        reqItems[0].querySelector('i').className = val.length >= 8 ? 'fas fa-check text-green-500 mr-1' : 'fas fa-times text-red-500 mr-1';
        reqItems[1].querySelector('i').className = /[a-zA-Z]/.test(val) ? 'fas fa-check text-green-500 mr-1' : 'fas fa-times text-red-500 mr-1';
        reqItems[2].querySelector('i').className = /\d/.test(val) ? 'fas fa-check text-green-500 mr-1' : 'fas fa-times text-red-500 mr-1';
        reqItems[3].querySelector('i').className = /[A-Z]/.test(val) ? 'fas fa-check text-green-500 mr-1' : 'fas fa-times text-red-500 mr-1';
    }

    emailInput.addEventListener("input", () => {
        const parent = emailInput.parentElement;
        let emailError = parent.querySelector(".text-red-500.text-sm:not([id])");
        if (!emailError) {
            emailError = document.createElement("span");
            emailError.className = "text-red-500 text-sm";
            parent.appendChild(emailError);
        }
        emailError.textContent = validateEmail(emailInput.value) ? "" : "Please enter a valid email address";
    });

    confirmPassword.addEventListener("input", () => {
        if (confirmPassword.value !== password.value) {
            confirmPasswordError.textContent = "Passwords do not match";
            confirmPassword.classList.add('border-red-500');
        } else {
            confirmPasswordError.textContent = "";
            confirmPassword.classList.remove('border-red-500');
        }
    });

    password.addEventListener("input", updatePasswordStrength);


    form.addEventListener("submit", (e) => {
        let hasError = false;

        if (!validateEmail(emailInput.value)) {
            hasError = true;
            const parent = emailInput.parentElement;
            let emailError = parent.querySelector(".text-red-500.text-sm:not([id])");
            if (!emailError) {
                emailError = document.createElement("span");
                emailError.className = "text-red-500 text-sm";
                parent.appendChild(emailError);
            }
            emailError.textContent = "Please enter a valid email address";
        }

        const strength = checkPasswordStrength(password.value);
        if (!strength.isStrong) {
            hasError = true;
            password.classList.add('border-red-500');
        }

        if (password.value !== confirmPassword.value) {
            hasError = true;
            confirmPasswordError.textContent = "Passwords do not match";
            confirmPassword.classList.add('border-red-500');
        }

        if (hasError) {
            e.preventDefault();
        }
    });
});
