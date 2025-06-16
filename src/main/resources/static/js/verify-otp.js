document.addEventListener("DOMContentLoaded", function () {
    const inputs = document.querySelectorAll('.otp-input');
    const hiddenInput = document.getElementById('otp');

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
            if (
                // Cho phép phím số, Backspace, Tab, mũi tên, Delete
                !(
                    (e.key >= '0' && e.key <= '9') ||
                    ["Backspace", "Tab", "ArrowLeft", "ArrowRight", "Delete"].includes(e.key)
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
});
