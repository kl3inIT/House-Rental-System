document.addEventListener("DOMContentLoaded", function () {
    const inputs = document.querySelectorAll('.otp-input');
    const hiddenInput = document.getElementById('otp');
    const submitButton = document.getElementById('verify-button');

    if (inputs.length > 0) inputs[0].focus();

    inputs.forEach((input, idx) => {
        // Highlight khi focus
        input.addEventListener('focus', function () {
            this.classList.add('border-blue-500', 'ring-2', 'ring-blue-200');
        });
        input.addEventListener('blur', function () {
            this.classList.remove('border-blue-500', 'ring-2', 'ring-blue-200');
        });

        // Chặn nhập ký tự không phải số
        input.addEventListener('keydown', function (e) {
            // Cho phép Ctrl+V (Windows) hoặc Command+V (Mac)
            if (
                !(
                    (e.key >= '0' && e.key <= '9') ||
                    ["Backspace", "Tab", "ArrowLeft", "ArrowRight", "Delete"].includes(e.key) ||
                    (e.ctrlKey && (e.key === 'v' || e.key === 'V')) ||
                    (e.metaKey && (e.key === 'v' || e.key === 'V'))
                )
            ) {
                e.preventDefault();
            }
            // Di chuyển bằng phím
            switch (e.key) {
                case "Backspace":
                    if (this.value) {
                        this.value = '';
                        updateHiddenInput();
                        e.preventDefault();
                    } else if (idx > 0) {
                        inputs[idx - 1].focus();
                    }
                    break;
                case "ArrowLeft":
                    if (idx > 0) inputs[idx - 1].focus();
                    break;
                case "ArrowRight":
                    if (idx < inputs.length - 1) inputs[idx + 1].focus();
                    break;
            }
        });

        // Khi nhập
        input.addEventListener('input', function (e) {
            const val = this.value.replace(/[^0-9]/g, '');
            this.value = val.length > 1 ? val.charAt(0) : val;
            if (val && idx < inputs.length - 1) {
                inputs[idx + 1].focus();
            }
            updateHiddenInput();
        });

        // Khi paste mã OTP
        input.addEventListener('paste', function (e) {
            const pasted = (e.clipboardData || window.clipboardData).getData('text').trim();
            if (/^\d{6}$/.test(pasted)) {
                [...pasted].forEach((char, i) => {
                    if (i < inputs.length) inputs[i].value = char;
                });
                updateHiddenInput();
                inputs[5].focus();
                e.preventDefault();
            } else {
                // Nếu paste không hợp lệ, loại bỏ ký tự không phải số
                const filtered = pasted.replace(/[^0-9]/g, '');
                [...filtered].forEach((char, i) => {
                    if (i < inputs.length) inputs[i].value = char;
                });
                updateHiddenInput();
                if (filtered.length > 0 && filtered.length <= inputs.length) {
                    inputs[filtered.length - 1].focus();
                }
                e.preventDefault();
            }
        });
    });

    function updateHiddenInput() {
        hiddenInput.value = Array.from(inputs).map(input => input.value).join('');
    }

    // Hiển thị đồng hồ đếm ngược và số lần nhập sai OTP
    (function () {
        const timerSpan = document.getElementById('otp-timer');
        const attemptsSpan = document.getElementById('otp-attempts');
        
        // Lấy giá trị từ attribute data- hoặc hidden input
        let otpExpire = timerSpan ? timerSpan.getAttribute('data-expire') : null;
        let otpFailCount = attemptsSpan ? attemptsSpan.getAttribute('data-failcount') : null;
        
        if (!otpExpire) {
            // Thử lấy từ input hidden nếu có
            const expireInput = document.getElementById('otp-expire');
            if (expireInput) otpExpire = expireInput.value;
        }
        if (!otpFailCount) {
            const failInput = document.getElementById('otp-failcount');
            if (failInput) otpFailCount = failInput.value;
        }
        
        if (!timerSpan || !attemptsSpan) return;
        
        let timerInterval = null;
        let failCount = parseInt(otpFailCount) || 0;
        let attemptsLeft = Math.max(0, 3 - failCount);
        
        // Update attempts display - luôn hiển thị số attempts còn lại
        attemptsSpan.textContent = `Attempts left: ${attemptsLeft}`;
        if (attemptsLeft > 0) {
            attemptsSpan.className = 'text-sm text-yellow-600';
        } else {
            attemptsSpan.className = 'text-sm text-red-600 font-medium';
            
            // Disable form when no attempts left
            if (submitButton) {
                submitButton.disabled = true;
                submitButton.textContent = 'Request New Code';
                submitButton.className = 'w-full py-2 px-4 bg-gray-400 text-white font-medium rounded-md cursor-not-allowed';
            }
            
            // Disable OTP inputs
            inputs.forEach(input => {
                input.disabled = true;
                input.className += ' bg-gray-100 cursor-not-allowed';
            });
        }
        
        // Timer logic
        if (otpExpire && otpExpire !== '0' && attemptsLeft > 0) {
            function updateTimer() {
                const now = Date.now();
                const diff = parseInt(otpExpire) - now;
                if (diff > 0) {
                    const min = Math.floor(diff / 60000);
                    const sec = Math.floor((diff % 60000) / 1000);
                    timerSpan.textContent = `OTP expires in: ${min}:${sec.toString().padStart(2, '0')}`;
                } else {
                    timerSpan.textContent = 'OTP expired. Please resend.';
                    timerSpan.className = 'text-sm text-red-500';
                    clearInterval(timerInterval);
                    
                    // Disable form when expired
                    if (submitButton) {
                        submitButton.disabled = true;
                        submitButton.textContent = 'Request New Code';
                        submitButton.className = 'w-full py-2 px-4 bg-gray-400 text-white font-medium rounded-md cursor-not-allowed';
                    }
                }
            }
            updateTimer();
            timerInterval = setInterval(updateTimer, 1000);
        } else {
            timerSpan.textContent = 'OTP expired. Please resend.';
            timerSpan.className = 'text-sm text-red-500';
        }
    })();
});
