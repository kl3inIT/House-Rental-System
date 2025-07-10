// User Profile JavaScript
document.addEventListener('DOMContentLoaded', function () {
    // Profile Edit Toggle
    const editProfileBtn = document.getElementById('editProfileBtn');
    const cancelEditBtn = document.getElementById('cancelEditBtn');
    const profileViewMode = document.getElementById('profileViewMode');
    const profileEditMode = document.getElementById('profileEditMode');

    if (editProfileBtn && cancelEditBtn && profileViewMode && profileEditMode) {
        editProfileBtn.addEventListener('click', function () {
            profileViewMode.classList.add('hidden');
            profileEditMode.classList.remove('hidden');
            editProfileBtn.classList.add('hidden');
        });

        cancelEditBtn.addEventListener('click', function () {
            profileViewMode.classList.remove('hidden');
            profileEditMode.classList.add('hidden');
            editProfileBtn.classList.remove('hidden');
        });
    }

    // Password Change Toggle
    const changePasswordBtn = document.getElementById('changePasswordBtn');
    const cancelPasswordBtn = document.getElementById('cancelPasswordBtn');
    const passwordForm = document.getElementById('passwordForm');

    if (changePasswordBtn && cancelPasswordBtn && passwordForm) {
        changePasswordBtn.addEventListener('click', function () {
            passwordForm.classList.remove('hidden');
            changePasswordBtn.classList.add('hidden');
        });

        cancelPasswordBtn.addEventListener('click', function () {
            passwordForm.classList.add('hidden');
            changePasswordBtn.classList.remove('hidden');
        });
    }

    // Auto-hide success/error messages after 5 seconds
    const successMessage = document.querySelector('.bg-green-100');
    const errorMessage = document.querySelector('.bg-red-100');

    if (successMessage) {
        setTimeout(() => {
            successMessage.style.display = 'none';
        }, 5000);
    }

    if (errorMessage) {
        setTimeout(() => {
            errorMessage.style.display = 'none';
        }, 5000);
    }

    // AJAX Profile Update
    const updateProfileForm = document.querySelector('form[th\\:action*="update"]');
    if (updateProfileForm) {
        updateProfileForm.addEventListener('submit', function (e) {
            e.preventDefault();

            const formData = new FormData(this);
            const data = Object.fromEntries(formData);

            fetch('/api/user/profile', {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(data)
            })
                .then(response => response.json())
                .then(data => {
                    if (data.id) {
                        // Success - reload page to show updated data
                        window.location.reload();
                    } else {
                        // Error
                        showMessage(data, 'error');
                    }
                })
                .catch(error => {
                    showMessage('An error occurred while updating profile', 'error');
                });
        });
    }

    // AJAX Password Change
    const changePasswordForm = document.querySelector('form[th\\:action*="change-password"]');
    if (changePasswordForm) {
        changePasswordForm.addEventListener('submit', function (e) {
            e.preventDefault();

            const formData = new FormData(this);
            const data = Object.fromEntries(formData);

            fetch('/api/user/change-password', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(data)
            })
                .then(response => response.json())
                .then(data => {
                    if (typeof data === 'string' && data.includes('successfully')) {
                        showMessage(data, 'success');
                        this.reset();
                        passwordForm.classList.add('hidden');
                        changePasswordBtn.classList.remove('hidden');
                    } else {
                        showMessage(data, 'error');
                    }
                })
                .catch(error => {
                    showMessage('An error occurred while changing password', 'error');
                });
        });
    }

    function showMessage(message, type) {
        const messageDiv = document.createElement('div');
        messageDiv.className = type === 'success'
            ? 'mb-4 p-4 bg-green-100 border border-green-400 text-green-700 rounded'
            : 'mb-4 p-4 bg-red-100 border border-red-400 text-red-700 rounded';
        messageDiv.textContent = message;

        const profileSection = document.getElementById('profile-section');
        if (profileSection) {
            profileSection.insertBefore(messageDiv, profileSection.firstChild);

            setTimeout(() => {
                messageDiv.remove();
            }, 5000);
        }
    }
}); 