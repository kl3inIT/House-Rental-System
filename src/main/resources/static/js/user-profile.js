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

document.addEventListener('DOMContentLoaded', function () {
    const landlordLink = document.getElementById('landlord-request-link');
    const landlordSection = document.getElementById('landlord-request-section');
    if (landlordLink && landlordSection) {
        landlordLink.addEventListener('click', function (e) {
            e.preventDefault();
            document.querySelectorAll('.profile-section').forEach(sec => sec.style.display = 'none');
            landlordSection.style.display = 'block';

            fetch('/landlord-upgrade-requests')
                .then(res => res.json())
                .then(data => {
                    if (!data || data.length === 0 || data[0].status === 'REJECTED') {
                        landlordSection.innerHTML = `
                            <form id="landlord-request-form" class="max-w-md mx-auto bg-white p-6 rounded shadow">
                                <h3 class="text-xl font-bold mb-4">Request to become Landlord</h3>
                                <div class="mb-4">
                                    <label class="block text-gray-700 mb-1">Full Name</label>
                                    <input type="text" id="landlord-fullname" class="w-full border px-3 py-2 rounded" required>
                                </div>
                                <div class="mb-4">
                                    <label class="block text-gray-700 mb-1">Phone</label>
                                    <input type="tel" id="landlord-phone" class="w-full border px-3 py-2 rounded" required>
                                </div>
                                <div class="mb-4">
                                    <label class="block text-gray-700 mb-1">Reason</label>
                                    <textarea id="landlord-reason" class="w-full border px-3 py-2 rounded" rows="3" required></textarea>
                                </div>
                                <button type="submit" class="btn btn-primary bg-blue-600 text-white px-4 py-2 rounded">Send Request</button>
                            </form>
                        `;
                        document.getElementById('landlord-request-form').onsubmit = function (ev) {
                            ev.preventDefault();
                            const fullName = document.getElementById('landlord-fullname').value;
                            const phone = document.getElementById('landlord-phone').value;
                            const reason = document.getElementById('landlord-reason').value;
                            fetch(`/landlord-upgrade-requests?fullName=${encodeURIComponent(fullName)}&phone=${encodeURIComponent(phone)}&reason=${encodeURIComponent(reason)}`, { method: 'POST' })
                                .then(res => {
                                    if (res.ok) {
                                        landlordSection.innerHTML = '<span class="text-info">Request sent, please wait for approval.</span>';
                                    } else {
                                        landlordSection.innerHTML = '<span class="text-danger">Request failed.</span>';
                                    }
                                });
                        };
                    } else if (data[0].status === 'PENDING') {
                        landlordSection.innerHTML = '<span class="text-warning">Your request is pending approval...</span>';
                    } else if (data[0].status === 'APPROVED') {
                        landlordSection.innerHTML = '<span class="text-success">You are now a Landlord!</span>';
                    }
                });
        });
    }
}); 