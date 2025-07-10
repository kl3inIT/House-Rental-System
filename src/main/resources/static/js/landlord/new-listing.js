// Image preview functionality and form validation
document.addEventListener('DOMContentLoaded', function() {
    // 1. Khai báo & Khởi tạo biến cho unified image preview (cũ + mới)
    const imageUpload = document.getElementById('image-upload');
    const imagePreview = document.getElementById('image-preview');
    const oldImageInput = document.getElementById('oldImageUrls');
    let imageIdCounter = 0;
    let selectedImages = []; // [{id, type: 'old'|'new', url?, file?}]

    // Load ảnh cũ vào selectedImages
    if (oldImageInput && oldImageInput.value) {
        oldImageInput.value.split(',').filter(x => !!x).forEach(url => {
            selectedImages.push({ id: ++imageIdCounter, type: 'old', url });
        });
        renderImagePreview();
    }

    // 2. Upload ảnh mới (upload hoặc drag-drop)
    if (imageUpload) {
        imageUpload.addEventListener('change', function() {
            const files = Array.from(imageUpload.files);
            const remain = 10 - selectedImages.length;
            files.slice(0, remain).forEach(file => {
                selectedImages.push({ id: ++imageIdCounter, type: 'new', file });
            });
            renderImagePreview();
            syncFileInput();
        });

        const dropZone = imageUpload.parentElement;
        dropZone.addEventListener('dragover', e => { e.preventDefault(); dropZone.classList.add('border-blue-500', 'bg-blue-50'); });
        dropZone.addEventListener('dragleave', e => { e.preventDefault(); dropZone.classList.remove('border-blue-500', 'bg-blue-50'); });
        dropZone.addEventListener('drop', function(e) {
            e.preventDefault();
            dropZone.classList.remove('border-blue-500', 'bg-blue-50');
            const files = Array.from(e.dataTransfer.files);
            const remain = 10 - selectedImages.length;
            files.slice(0, remain).forEach(file => {
                selectedImages.push({ id: ++imageIdCounter, type: 'new', file });
            });
            renderImagePreview();
            syncFileInput();
        });
    }

    // 4. Hàm render lại preview unified (cũ + mới) + xóa ảnh
    function renderImagePreview() {
        imagePreview.innerHTML = '';
        selectedImages.forEach((img, idx) => {
            const container = document.createElement('div');
            container.className = 'relative group bg-gray-100 rounded-lg shadow-sm flex flex-col items-center mr-2 mb-2';

            const imgElem = document.createElement('img');
            imgElem.className = 'w-32 h-32 md:w-40 md:h-40 object-cover rounded-lg mx-auto';
            if (img.type === 'old') {
                imgElem.src = img.url;
            } else {
                const reader = new FileReader();
                reader.onload = e => imgElem.src = e.target.result;
                reader.readAsDataURL(img.file);
            }
            container.appendChild(imgElem);

            // LUÔN tạo nút xóa
            const removeBtn = document.createElement('button');
            removeBtn.type = 'button';
            removeBtn.className = 'remove-image-btn absolute -top-2 -right-2 w-7 h-7 bg-red-600 text-white rounded-full flex items-center justify-center z-50';
            removeBtn.setAttribute('data-id', img.id);
            removeBtn.innerHTML = '<i class="fas fa-times text-sm font-bold"></i>';
            removeBtn.addEventListener('click', function(e) {
                e.preventDefault();
                e.stopPropagation();
                selectedImages = selectedImages.filter(x => x.id !== img.id);
                renderImagePreview();
                syncFileInput();
            });
            container.appendChild(removeBtn);

            // Badge thứ tự
            const badge = document.createElement('div');
            badge.className = 'absolute bottom-1 left-1 bg-white text-gray-700 text-xs px-1.5 py-0.5 rounded font-medium shadow-sm';
            badge.innerText = '#' + (idx + 1);
            container.appendChild(badge);

            // Badge size cho ảnh mới
            if (img.type === 'new') {
                const sizeBadge = document.createElement('div');
                sizeBadge.className = 'absolute bottom-1 right-1 bg-black bg-opacity-70 text-white text-xs px-1.5 py-0.5 rounded shadow-sm';
                sizeBadge.innerText = (img.file.size / 1024 / 1024).toFixed(1) + ' MB';
                container.appendChild(sizeBadge);
            }
            imagePreview.appendChild(container);
        });
        updateUploadButtonState();
        updateCounter();
        console.log('RENDERED preview', imagePreview.innerHTML);

        const btns = document.querySelectorAll('.remove-image-btn');
        btns.forEach(btn => {
            console.log('BTN:', btn, 'computed:', window.getComputedStyle(btn));
        });
    }

    // Đồng bộ input file và input ẩn oldImageUrls
    function syncFileInput() {
        // Sync file input: chỉ new images
        const dt = new DataTransfer();
        selectedImages.filter(x => x.type === 'new').forEach(x => dt.items.add(x.file));
        imageUpload.files = dt.files;
        // Sync hidden input: chỉ old images (chính là danh sách url ảnh cũ còn giữ lại)
        if (oldImageInput)
            oldImageInput.value = selectedImages.filter(x => x.type === 'old').map(x => x.url).join(',');
    }

    // Cập nhật trạng thái nút upload, text, counter...
    function updateUploadButtonState() {
        const uploadLabel = document.querySelector('label[for="image-upload"]');
        const uploadText = document.getElementById('upload-text');
        if (!uploadLabel || !uploadText) return;
        if (selectedImages.length >= 10) {
            uploadLabel.classList.add('bg-gray-400', 'cursor-not-allowed');
            uploadLabel.classList.remove('bg-blue-600', 'hover:bg-blue-700');
            uploadText.textContent = 'Maximum 10 images reached';
            imageUpload.disabled = true;
        } else {
            uploadLabel.classList.remove('bg-gray-400', 'cursor-not-allowed');
            uploadLabel.classList.add('bg-blue-600', 'hover:bg-blue-700');
            uploadText.textContent = 'Add More Images';
            imageUpload.disabled = false;
        }
    }
    function updateCounter() {
        const fileCount = document.getElementById('file-count');
        const selectedCount = document.getElementById('selected-count');
        if (!fileCount || !selectedCount) return;
        if (selectedImages.length > 0) {
            imagePreview.classList.remove('hidden');
            fileCount.classList.remove('hidden');
            selectedCount.textContent = selectedImages.length;
        } else {
            imagePreview.classList.add('hidden');
            fileCount.classList.add('hidden');
            selectedCount.textContent = 0;
        }
    }

    // 5. Description counter (giữ nguyên)
    const descriptionTextarea = document.querySelector('textarea[name="description"]');
    const descriptionCount = document.getElementById('description-count');
    if (descriptionTextarea && descriptionCount) {
        initializeDescriptionCounter();
    }
    function initializeDescriptionCounter() {
        function updateCount() {
            const count = descriptionTextarea.value.length;
            descriptionCount.textContent = count;
            if (count > 2000) {
                descriptionCount.parentElement.classList.add('text-red-600');
                descriptionCount.parentElement.classList.remove('text-gray-500');
            } else {
                descriptionCount.parentElement.classList.remove('text-red-600');
                descriptionCount.parentElement.classList.add('text-gray-500');
            }
        }
        descriptionTextarea.addEventListener('input', updateCount);
        updateCount();
    }

    // 6. Form validation (giữ nguyên)
    initializeFormValidation();
    function initializeFormValidation() {
        const validateFields = document.querySelectorAll('[data-validate]');
        validateFields.forEach(field => {
            field.addEventListener('blur', function() { validateField(this); });
            if (field.type === 'text' || field.type === 'number' || field.tagName === 'TEXTAREA') {
                field.addEventListener('input', function() { clearFieldError(this); });
            }
            if (field.tagName === 'SELECT') {
                field.addEventListener('change', function() { validateField(this); });
            }
        });
    }
    function validateField(field) {
        const rules = field.getAttribute('data-validate').split(',');
        const errorContainer = field.parentElement.querySelector('.validation-error');
        clearFieldError(field);
        for (let rule of rules) {
            const [ruleName, ruleValue] = rule.split(':');
            const error = validateRule(field, ruleName.trim(), ruleValue?.trim());
            if (error) {
                showFieldError(field, error, errorContainer);
                return false;
            }
        }
        return true;
    }
    function validateRule(field, rule, value) {
        const fieldValue = field.value.trim();
        switch (rule) {
            case 'required':
                if (!fieldValue) return `${getFieldLabel(field)} is required.`;
                break;
            case 'maxLength':
                if (fieldValue.length > parseInt(value)) return `${getFieldLabel(field)} must be less than ${value} characters.`;
                break;
            case 'number':
                if (fieldValue && isNaN(fieldValue)) return `${getFieldLabel(field)} must be a valid number.`;
                break;
            case 'min':
                if (fieldValue && parseFloat(fieldValue) < parseFloat(value)) return `${getFieldLabel(field)} must be at least ${value}.`;
                break;
            case 'imageFiles':
                return validateImageFiles(field);
        }
        return null;
    }
    function validateImageFiles(field) {
        // Chỉ check tổng số unified images, không cần check file input trực tiếp
        if (selectedImages.length > 10)
            return 'You can upload maximum 10 images.';
        return null;
    }
    function getFieldLabel(field) {
        const label = field.parentElement.querySelector('label');
        return label ? label.textContent.replace('*', '').trim() : 'Field';
    }
    function showFieldError(field, message, errorContainer) {
        field.classList.add('border-red-500');
        field.classList.remove('border-gray-300');
        if (errorContainer) {
            errorContainer.textContent = message;
            errorContainer.classList.remove('hidden');
        }
    }
    function clearFieldError(field) {
        field.classList.remove('border-red-500');
        field.classList.add('border-gray-300');
        const errorContainer = field.parentElement.querySelector('.validation-error');
        if (errorContainer) {
            errorContainer.classList.add('hidden');
            errorContainer.textContent = '';
        }
    }

    // 7. Submit form (kiểm tra đủ ảnh unified, validate trường, giữ nguyên logic cũ)
    const form = document.getElementById('property-form');
    const submitBtn = document.getElementById('submit-btn');
    if (form) {
        form.addEventListener('submit', handleFormSubmit);
    }
    function handleFormSubmit(event) {
        event.preventDefault();
        if (selectedImages.length < 1) {
            showNotification('Please upload at least one image.', 'error');
            return;
        }
        // Disable submit button to prevent double submission
        submitBtn.disabled = true;
        submitBtn.innerHTML = '<i class="fas fa-spinner fa-spin mr-2"></i>Saving...';
        // Validate all fields
        const validateFields = document.querySelectorAll('[data-validate]');
        let isValid = true, firstInvalidField = null;
        validateFields.forEach(field => {
            const fieldValid = validateField(field);
            if (!fieldValid && !firstInvalidField) {
                firstInvalidField = field;
                isValid = false;
            } else if (!fieldValid) {
                isValid = false;
            }
        });
        // Province/ward check
        const provinceSelect = document.getElementById('province-select');
        const wardSelect = document.getElementById('ward-select');
        if (provinceSelect && provinceSelect.value && (!wardSelect || !wardSelect.value)) {
            showFieldError(wardSelect, 'Please select a ward.', wardSelect.parentElement.querySelector('.validation-error'));
            isValid = false;
            if (!firstInvalidField) firstInvalidField = wardSelect;
        }
        if (!isValid) {
            submitBtn.disabled = false;
            submitBtn.innerHTML = '<i class="fas fa-save mr-2"></i>Save';
            if (firstInvalidField) {
                firstInvalidField.scrollIntoView({ behavior: 'smooth', block: 'center' });
                firstInvalidField.focus();
            }
            showNotification('Please fix the errors above before submitting.', 'error');
            return;
        }
        // Nếu pass validate thì submit
        form.submit();
    }

    // 8. Notification (không đổi)
    function showNotification(message, type = 'info') {
        const notification = document.createElement('div');
        notification.className = `fixed top-4 right-4 z-50 p-4 rounded-md shadow-lg max-w-sm ${
            type === 'error' ? 'bg-red-50 border border-red-200 text-red-800' :
                type === 'success' ? 'bg-green-50 border border-green-200 text-green-800' :
                    'bg-blue-50 border border-blue-200 text-blue-800'
        }`;
        notification.innerHTML = `
            <div class="flex items-center">
                <div class="flex-shrink-0">
                    <i class="fas ${
            type === 'error' ? 'fa-exclamation-circle' :
                type === 'success' ? 'fa-check-circle' : 'fa-info-circle'
        }"></i>
                </div>
                <div class="ml-3">
                    <p class="text-sm font-medium">${message}</p>
                </div>
                <div class="ml-auto pl-3">
                    <button type="button" class="inline-flex rounded-md p-1.5 hover:bg-opacity-20 focus:outline-none" onclick="this.parentElement.parentElement.parentElement.remove()">
                        <i class="fas fa-times text-sm"></i>
                    </button>
                </div>
            </div>
        `;
        document.body.appendChild(notification);
        setTimeout(() => { if (notification.parentNode) notification.remove(); }, 5000);
    }

    // 9. Các hàm helper (nếu cần)
    window.getSelectedImages = function() { return selectedImages; };
    window.clearAllImages = function() {
        selectedImages = [];
        syncFileInput();
        renderImagePreview();
        if (imageUpload) {
            imageUpload.value = '';
            clearFieldError(imageUpload);
        }
    };

    // 10. Deposit cập nhật (nếu dùng)
    const depositPercentageInput = document.getElementById('depositPercentage');
    if (depositPercentageInput) {
        depositPercentageInput.addEventListener('input', updateDepositAmount);
    }
    function updateDepositAmount() {
        var depositPercentage = document.getElementById('depositPercentage').value;
        document.getElementById('deposit-percentage-amount').textContent = depositPercentage;
        var monthlyRent = parseFloat(document.getElementById('monthlyRent').value);
        if (isNaN(monthlyRent) || monthlyRent <= 0) {
            document.getElementById('deposit-amount').textContent = '0';
            return;
        }
        var depositAmount = (depositPercentage / 100) * monthlyRent;
        document.getElementById('deposit-amount').textContent = depositAmount.toFixed(2);
    }
    window.onload = function() { updateDepositAmount(); };

    // 11. Vị trí location dropdown (nếu có)
    if (typeof initializeVietnameseLocations === 'function') {
        initializeVietnameseLocations();
    }
});


// Vietnamese location API functionality using new API structure
function initializeVietnameseLocations() {
    const provinceSelect = document.getElementById('province-select');
    const wardSelect = document.getElementById('ward-select');

    if (!provinceSelect || !wardSelect) {
        return;
    }

    // Load provinces on page load
    loadProvinces();

    // Load wards when province is selected
    provinceSelect.addEventListener('change', function() {
        const selectedProvince = this.value;
        
        wardSelect.innerHTML = '<option value="">Đang tải...</option>';
        wardSelect.disabled = true;
        
        // Clear any validation errors for ward
        const wardErrorContainer = wardSelect.parentElement.querySelector('.validation-error');
        if (wardErrorContainer) {
            wardErrorContainer.classList.add('hidden');
        }
        wardSelect.classList.remove('border-red-500');
        wardSelect.classList.add('border-gray-300');
        
        if (selectedProvince) {
            loadWards(selectedProvince);
        } else {
            wardSelect.innerHTML = '<option value="">Chọn tỉnh/thành phố trước</option>';
            wardSelect.disabled = true;
        }
    });
}

async function loadProvinces() {
    const provinceSelect = document.getElementById('province-select');
    const selectedProvince = document.getElementById('province-value')?.value || '';
    try {
        // Load all provinces from the new API
        const response = await fetch('https://vietnamlabs.com/api/vietnamprovince');
        const data = await response.json();
        
        if (data.success && data.data) {
            // Clear existing options except the first one
            provinceSelect.innerHTML = '<option value="">Chọn tỉnh/thành phố</option>';
            
            data.data.forEach(provinceData => {
                const option = document.createElement('option');
                option.value = provinceData.province;
                option.textContent = provinceData.province;
                provinceSelect.appendChild(option);
            });

            // --- Chọn lại giá trị cũ khi edit ---
            if (selectedProvince) {
                provinceSelect.value = selectedProvince;
                // Trigger load ward nếu có sẵn tỉnh
                loadWards(selectedProvince);
            }
        } else {
            throw new Error('Invalid API response');
        }
    } catch (error) {
        console.error('Error loading provinces:', error);
        // Fallback to static list if API fails
        addFallbackProvinces();
    }
}

async function loadWards(provinceName) {
    const wardSelect = document.getElementById('ward-select');
    const selectedWard = document.getElementById('ward-value')?.value || '';
    let allWards = [];
    let offset = 0;
    const limit = 50;
    let hasMore = true;

    try {
        while (hasMore) {
            const response = await fetch(
                `https://vietnamlabs.com/api/vietnamprovince?province=${encodeURIComponent(provinceName)}&offset=${offset}&limit=${limit}`
            );
            const data = await response.json();

            if (data.success && data.data && data.data.wards) {
                allWards = allWards.concat(data.data.wards);
                if (data.pagination && data.pagination.hasMore) {
                    offset += limit;
                    hasMore = true;
                } else {
                    hasMore = false;
                }
            } else {
                hasMore = false;
            }
        }

        wardSelect.innerHTML = '<option value="">Chọn phường/xã</option>';
        if (allWards.length > 0) {
            allWards.forEach(ward => {
                const option = document.createElement('option');
                option.value = ward.name;
                option.textContent = ward.name;
                wardSelect.appendChild(option);
            });
            wardSelect.disabled = false;
            // --- Chọn lại ward khi edit ---
            if (selectedWard) {
                wardSelect.value = selectedWard;
            }
        } else {
            wardSelect.innerHTML = '<option value="">Không có dữ liệu phường/xã</option>';
        }
    } catch (error) {
        console.error('Error loading wards:', error);
        wardSelect.innerHTML = '<option value="">Lỗi tải dữ liệu phường/xã</option>';
    }
}

// Fallback provinces if API fails
function addFallbackProvinces() {
    const provinceSelect = document.getElementById('province-select');
    const vietnameseProvinces = [
        'An Giang', 'Bà Rịa - Vũng Tàu', 'Bắc Giang', 'Bắc Kạn', 'Bạc Liêu',
        'Bắc Ninh', 'Bến Tre', 'Bình Định', 'Bình Dương', 'Bình Phước',
        'Bình Thuận', 'Cà Mau', 'Cao Bằng', 'Đắk Lắk', 'Đắk Nông',
        'Điện Biên', 'Đồng Nai', 'Đồng Tháp', 'Gia Lai', 'Hà Giang',
        'Hà Nam', 'Hà Tĩnh', 'Hải Dương', 'Hậu Giang', 'Hòa Bình',
        'Hưng Yên', 'Khánh Hòa', 'Kiên Giang', 'Kon Tum', 'Lai Châu',
        'Lâm Đồng', 'Lạng Sơn', 'Lào Cai', 'Long An', 'Nam Định',
        'Nghệ An', 'Ninh Bình', 'Ninh Thuận', 'Phú Thọ', 'Quảng Bình',
        'Quảng Nam', 'Quảng Ngãi', 'Quảng Ninh', 'Quảng Trị', 'Sóc Trăng',
        'Sơn La', 'Tây Ninh', 'Thái Bình', 'Thái Nguyên', 'Thanh Hóa',
        'Thừa Thiên Huế', 'Tiền Giang', 'Trà Vinh', 'Tuyên Quang', 'Vĩnh Long',
        'Vĩnh Phúc', 'Yên Bái', 'Phú Yên', 'Cần Thơ', 'Đà Nẵng',
        'Hải Phòng', 'Hà Nội', 'TP Hồ Chí Minh'
    ];

    provinceSelect.innerHTML = '<option value="">Chọn tỉnh/thành phố</option>';
    vietnameseProvinces.forEach(province => {
        const option = document.createElement('option');
        option.value = province;
        option.textContent = province;
        provinceSelect.appendChild(option);
    });

}

