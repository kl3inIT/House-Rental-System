document.addEventListener("DOMContentLoaded", () => {
    const form = document.querySelector("form");
    const emailInput = document.getElementById("email");
    const password = document.getElementById("password");
    const confirmPassword = document.getElementById("confirmPassword");
    const confirmPasswordError = document.getElementById("confirmPasswordError");
    const passwordTooltip = document.getElementById("passwordTooltip");

    // Tạo thanh đo độ mạnh mật khẩu
    const strengthBar = document.createElement("div");
    strengthBar.className = "w-full h-2 bg-gray-200 rounded mt-2";
    const strengthFill = document.createElement("div");
    strengthFill.className = "h-full rounded";
    strengthBar.appendChild(strengthFill);
    password.parentElement.appendChild(strengthBar);

    // Tạo tooltip hướng dẫn độ mạnh mật khẩu
    const requirementsList = document.createElement("ul");
    requirementsList.className = "text-xs text-gray-500 mt-2 space-y-1";
    const requirements = [
        "At least 8 characters",
        "Include uppercase and lowercase letters",
        "Include numbers",
        "Include special characters (!@#$%^&*)"
    ];
    requirements.forEach(req => {
        const li = document.createElement("li");
        li.className = "requirement-item";
        li.innerHTML = `<i class="fas fa-times text-red-500 mr-1"></i>${req}`;
        requirementsList.appendChild(li);
    });
    passwordTooltip.parentElement.appendChild(requirementsList);

    // Kiểm tra định dạng email
    function validateEmail(email) {
        const regex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        return regex.test(email);
    }

    // Độ mạnh mật khẩu
    function checkPasswordStrength() {
        const val = password.value;
        const strength = checkPasswordStrength(val);
        const percent = (strength.score / 4) * 100;
        strengthFill.style.width = percent + "%";

        // Update strength bar color
        if (strength.score <= 1) {
            strengthFill.className = "h-full rounded bg-red-500";
        } else if (strength.score === 2) {
            strengthFill.className = "h-full rounded bg-yellow-400";
        } else {
            strengthFill.className = "h-full rounded bg-green-500";
        }

        // Update requirements list
        const requirements = requirementsList.querySelectorAll('.requirement-item');
        requirements[0].querySelector('i').className = val.length >= 8 ? 'fas fa-check text-green-500 mr-1' : 'fas fa-times text-red-500 mr-1';
        requirements[1].querySelector('i').className = /[A-Z]/.test(val) && /[a-z]/.test(val) ? 'fas fa-check text-green-500 mr-1' : 'fas fa-times text-red-500 mr-1';
        requirements[2].querySelector('i').className = /\d/.test(val) ? 'fas fa-check text-green-500 mr-1' : 'fas fa-times text-red-500 mr-1';
        requirements[3].querySelector('i').className = /[!@#$%^&*]/.test(val) ? 'fas fa-check text-green-500 mr-1' : 'fas fa-times text-red-500 mr-1';
    }

    // Kiểm tra email
    emailInput.addEventListener("input", () => {
        const parent = emailInput.parentElement;
        let emailError = parent.querySelector(".text-red-500.text-sm:not([id])");
        if (!emailError) {
            emailError = document.createElement("span");
            emailError.className = "text-red-500 text-sm";
            parent.appendChild(emailError);
        }

        if (!validateEmail(emailInput.value)) {
            emailError.textContent = "Please enter a valid email address";
        } else {
            emailError.textContent = "";
        }
    });

    // Kiểm tra confirm password
    confirmPassword.addEventListener("input", () => {
        if (confirmPassword.value !== password.value) {
            confirmPasswordError.textContent = "Passwords do not match";
            confirmPassword.classList.add('border-red-500');
        } else {
            confirmPasswordError.textContent = "";
            confirmPassword.classList.remove('border-red-500');
        }
    });

    password.addEventListener("input", checkPasswordStrength);

    // Submit
    form.addEventListener("submit", (e) => {
        let hasError = false;

        // Validate email
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

        // Validate password strength
        const strength = checkPasswordStrength(password.value);
        if (!strength.isStrong) {
            hasError = true;
            password.classList.add('border-red-500');
        }

        // Validate password match
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
