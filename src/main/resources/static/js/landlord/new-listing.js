// Image preview functionality and form validation
document.addEventListener('DOMContentLoaded', function() {
    const imageUpload = document.getElementById('image-upload');
    const imagePreview = document.getElementById('image-preview');
    const form = document.getElementById('property-form');
    const submitBtn = document.getElementById('submit-btn');
    const descriptionTextarea = document.querySelector('textarea[name="description"]');
    const descriptionCount = document.getElementById('description-count');
    
    // Store selected files with unique IDs
    let selectedFiles = [];
    let fileIdCounter = 0;

    // Initialize validation
    initializeFormValidation();
    
    // Initialize image upload
    if (imageUpload) {
        imageUpload.addEventListener('change', handleImageUpload);
        
        // Initialize drag and drop functionality
        const dropZone = imageUpload.parentElement;
        
        dropZone.addEventListener('dragover', function(e) {
            e.preventDefault();
            dropZone.classList.add('border-blue-500', 'bg-blue-50');
        });
        
        dropZone.addEventListener('dragleave', function(e) {
            e.preventDefault();
            dropZone.classList.remove('border-blue-500', 'bg-blue-50');
        });
        
        dropZone.addEventListener('drop', function(e) {
            e.preventDefault();
            dropZone.classList.remove('border-blue-500', 'bg-blue-50');
            
            const files = Array.from(e.dataTransfer.files);
            if (files.length > 0) {
                // Simulate file input change
                const dt = new DataTransfer();
                files.forEach(file => dt.items.add(file));
                imageUpload.files = dt.files;
                
                // Trigger the change event
                handleImageUpload({ target: imageUpload });
            }
        });
    }

    // Set up event delegation for remove buttons
    if (imagePreview) {
        imagePreview.addEventListener('click', function(e) {
            if (e.target.closest('.remove-image-btn')) {
                e.preventDefault();
                e.stopPropagation();
                
                const button = e.target.closest('.remove-image-btn');
                const fileId = parseInt(button.getAttribute('data-file-id'));
                removeImagePreview(fileId);
            }
        });
    }

    // Initialize Vietnamese location dropdowns
    initializeVietnameseLocations();

    // Initialize description character counter
    if (descriptionTextarea && descriptionCount) {
        initializeDescriptionCounter();
    }

    // Form submission validation
    if (form) {
        form.addEventListener('submit', handleFormSubmit);
    }

    function initializeFormValidation() {
        // Add event listeners for real-time validation
        const validateFields = document.querySelectorAll('[data-validate]');
        
        validateFields.forEach(field => {
            // Validate on blur
            field.addEventListener('blur', function() {
                validateField(this);
            });

            // Validate on input for text fields
            if (field.type === 'text' || field.type === 'number' || field.tagName === 'TEXTAREA') {
                field.addEventListener('input', function() {
                    clearFieldError(this);
                });
            }

            // Validate on change for select fields
            if (field.tagName === 'SELECT') {
                field.addEventListener('change', function() {
                    validateField(this);
                });
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
                if (!fieldValue) {
                    return `${getFieldLabel(field)} is required.`;
                }
                break;
                
            case 'maxLength':
                if (fieldValue.length > parseInt(value)) {
                    return `${getFieldLabel(field)} must be less than ${value} characters.`;
                }
                break;
                
            case 'number':
                if (fieldValue && isNaN(fieldValue)) {
                    return `${getFieldLabel(field)} must be a valid number.`;
                }
                break;
                
            case 'min':
                if (fieldValue && parseFloat(fieldValue) < parseFloat(value)) {
                    return `${getFieldLabel(field)} must be at least ${value}.`;
                }
                break;
                
            case 'imageFiles':
                return validateImageFiles(field);
        }
        
        return null;
    }

    function validateImageFiles(field) {
        const files = field.files;
        
        if (!files || files.length === 0) {
            return null; // Images are optional
        }
        
        if (files.length > 10) {
            return 'You can upload maximum 10 images.';
        }
        
        const allowedTypes = ['image/jpeg', 'image/jpg', 'image/png'];
        const maxSize = 5 * 1024 * 1024; // 5MB
        
        for (let file of files) {
            if (!allowedTypes.includes(file.type)) {
                return 'Only JPG and PNG images are allowed.';
            }
            
            if (file.size > maxSize) {
                return `Image "${file.name}" is too large. Maximum size is 5MB.`;
            }
        }
        
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

    function handleImageUpload(event) {
        const files = Array.from(event.target.files);
        const errorContainer = event.target.parentElement.parentElement.querySelector('.validation-error');
        
        // Don't process if no files selected
        if (files.length === 0) {
            return;
        }
        
        // Clear previous errors
        clearFieldError(event.target);
        
        // Add new files to selectedFiles array (limit to 10 total)
        const remainingSlots = 10 - selectedFiles.length;
        
        if (remainingSlots <= 0) {
            showFieldError(event.target, 'Maximum 10 images already selected.', errorContainer);
            event.target.value = ''; // Clear the input
            return;
        }
        
        const filesToAdd = files.slice(0, remainingSlots).map(file => ({
            file: file,
            id: ++fileIdCounter
        }));
        
        if (files.length > remainingSlots) {
            showFieldError(event.target, `You can only upload ${remainingSlots} more images (10 maximum total).`, errorContainer);
        }
        
        selectedFiles = selectedFiles.concat(filesToAdd);
        
        // Validate all selected files
        const tempInput = document.createElement('input');
        tempInput.type = 'file';
        tempInput.files = createFileList(selectedFiles.map(item => item.file));
        
        const error = validateImageFiles(tempInput);
        if (error) {
            // Remove the newly added files if validation fails
            selectedFiles = selectedFiles.slice(0, selectedFiles.length - filesToAdd.length);
            showFieldError(event.target, error, errorContainer);
            updateImagePreview();
            return;
        }
        
        // Update the file input and preview
        updateFileInput();
        updateImagePreview();
    }
    
    function createFileList(files) {
        const dt = new DataTransfer();
        files.forEach(file => dt.items.add(file));
        return dt.files;
    }
    
    function updateFileInput() {
        const fileInput = document.getElementById('image-upload');
        fileInput.files = createFileList(selectedFiles.map(item => item.file));
    }
    
    function updateImagePreview() {
        imagePreview.innerHTML = '';
        
        // Update file counter and button text
        const fileCount = document.getElementById('file-count');
        const selectedCount = document.getElementById('selected-count');
        const uploadText = document.getElementById('upload-text');
        
        if (selectedFiles.length > 0) {
            imagePreview.classList.remove('hidden');
            fileCount.classList.remove('hidden');
            selectedCount.textContent = selectedFiles.length;
            uploadText.textContent = selectedFiles.length >= 10 ? 'Maximum reached' : 'Add More Images';
            
            selectedFiles.forEach((fileItem, index) => {
                createImagePreviewItem(fileItem.file, index, fileItem.id);
            });
        } else {
            imagePreview.classList.add('hidden');
            fileCount.classList.add('hidden');
            uploadText.textContent = 'Select Images';
        }
        
        // Update upload button state
        updateUploadButtonState();
    }
    
    function createImagePreviewItem(file, index, fileId) {
        const reader = new FileReader();
        
        reader.onload = function(e) {
            // Create container - removed overflow-hidden to prevent clipping of remove button
            const container = document.createElement('div');
            container.className = 'relative group bg-gray-100 rounded-lg shadow-sm hover:shadow-md transition-shadow duration-200';
            container.setAttribute('data-file-id', fileId);
            
            // Create image element with rounded corners
            const img = document.createElement('img');
            img.src = e.target.result;
            img.alt = `Property image ${index + 1}`;
            img.className = 'w-full h-80 object-cover rounded-lg';
            
            // Create beautiful modern remove button
            const removeBtn = document.createElement('button');
            removeBtn.type = 'button';
            removeBtn.className = 'remove-image-btn absolute -top-2 -right-2 w-7 h-7 bg-red-600 hover:bg-red-700 focus:bg-red-700 text-white rounded-full flex items-center justify-center transition-all duration-300 ease-out shadow-lg hover:shadow-xl border-2 border-white opacity-100 group-hover:scale-110 z-50 focus:outline-none focus:ring-3 focus:ring-red-400 hover:rotate-90 transform-gpu';
            removeBtn.setAttribute('data-file-id', fileId);
            removeBtn.setAttribute('aria-label', `Remove image ${index + 1}`);
            removeBtn.setAttribute('title', `Remove image ${index + 1}`);
            
            // Use FontAwesome icon with better styling
            const icon = document.createElement('i');
            icon.className = 'fas fa-times text-sm font-bold drop-shadow-sm';
            icon.setAttribute('aria-hidden', 'true');
            removeBtn.appendChild(icon);
            
            // Create image number badge
            const numberBadge = document.createElement('div');
            numberBadge.className = 'absolute bottom-1 left-1 bg-white bg-opacity-90 text-gray-700 text-xs px-1.5 py-0.5 rounded font-medium shadow-sm';
            numberBadge.innerHTML = `#${index + 1}`;
            numberBadge.setAttribute('aria-label', `Image ${index + 1}`);
            
            // Create file size badge
            const sizeBadge = document.createElement('div');
            sizeBadge.className = 'absolute bottom-1 right-1 bg-black bg-opacity-70 text-white text-xs px-1.5 py-0.5 rounded shadow-sm';
            sizeBadge.innerHTML = `${(file.size / 1024 / 1024).toFixed(1)} MB`;
            sizeBadge.setAttribute('aria-label', `File size ${(file.size / 1024 / 1024).toFixed(1)} megabytes`);
            
            // Add all elements to container
            container.appendChild(img);
            container.appendChild(removeBtn);
            container.appendChild(numberBadge);
            container.appendChild(sizeBadge);
            
            imagePreview.appendChild(container);
        };
        
        reader.onerror = function() {
            console.error('Error reading file:', file.name);
            showNotification(`Failed to load image: ${file.name}`, 'error');
        };
        
        reader.readAsDataURL(file);
    }
    
    function removeImagePreview(fileId) {
        // Find and remove file from selectedFiles array by ID
        const initialLength = selectedFiles.length;
        selectedFiles = selectedFiles.filter(item => item.id !== fileId);
        
        // Check if file was actually removed
        if (selectedFiles.length === initialLength) {
            console.warn('File not found for removal:', fileId);
            return;
        }
        
        // Update file input
        updateFileInput();
        
        // Refresh preview (this will renumber all items)
        updateImagePreview();
        
        // Show notification
        showNotification('Image removed successfully', 'success');
        
        // Clear any validation errors if files are now within limits
        const fileInput = document.getElementById('image-upload');
        if (selectedFiles.length <= 10) {
            clearFieldError(fileInput);
        }
        
        // If no files left, clear the file input value
        if (selectedFiles.length === 0) {
            fileInput.value = '';
        }
    }
    
    function updateUploadButtonState() {
        const uploadButton = document.querySelector('label[for="image-upload"]');
        const fileInput = document.getElementById('image-upload');
        const uploadText = document.getElementById('upload-text');
        
        if (selectedFiles.length >= 10) {
            uploadButton.classList.add('bg-gray-400', 'cursor-not-allowed');
            uploadButton.classList.remove('bg-blue-600', 'hover:bg-blue-700');
            uploadText.textContent = 'Maximum 10 images reached';
            fileInput.disabled = true;
        } else {
            uploadButton.classList.remove('bg-gray-400', 'cursor-not-allowed');
            uploadButton.classList.add('bg-blue-600', 'hover:bg-blue-700');
            fileInput.disabled = false;
        }
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
        updateCount(); // Initialize count
    }

    function handleFormSubmit(event) {
        event.preventDefault();
        
        // Disable submit button to prevent double submission
        submitBtn.disabled = true;
        submitBtn.innerHTML = '<i class="fas fa-spinner fa-spin mr-2"></i>Creating Property...';
        
        // Validate all fields
        const validateFields = document.querySelectorAll('[data-validate]');
        let isValid = true;
        let firstInvalidField = null;
        
        validateFields.forEach(field => {
            const fieldValid = validateField(field);
            if (!fieldValid && !firstInvalidField) {
                firstInvalidField = field;
                isValid = false;
            } else if (!fieldValid) {
                isValid = false;
            }
        });
        
        // Check if province and district are selected properly
        const provinceSelect = document.getElementById('province-select');
        const districtSelect = document.getElementById('district-select');
        
        if (provinceSelect.value && !districtSelect.value) {
            showFieldError(districtSelect, 'Please select a district.', 
                         districtSelect.parentElement.querySelector('.validation-error'));
            isValid = false;
            if (!firstInvalidField) {
                firstInvalidField = districtSelect;
            }
        }
        
        if (!isValid) {
            // Re-enable submit button
            submitBtn.disabled = false;
            submitBtn.innerHTML = '<i class="fas fa-save mr-2"></i>Create Property';
            
            // Scroll to first invalid field
            if (firstInvalidField) {
                firstInvalidField.scrollIntoView({ behavior: 'smooth', block: 'center' });
                firstInvalidField.focus();
            }
            
            // Show general error message
            showNotification('Please fix the errors above before submitting.', 'error');
            return;
        }
        
        // If validation passes, submit the form
        form.submit();
    }

    function showNotification(message, type = 'info') {
        // Create notification element
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
        
        // Auto remove after 5 seconds
        setTimeout(() => {
            if (notification.parentNode) {
                notification.remove();
            }
        }, 5000);
    }

    // Make helper functions globally available for debugging
    window.getSelectedFiles = function() {
        return selectedFiles.map(item => item.file);
    };
    
    window.getSelectedFilesWithIds = function() {
        return selectedFiles;
    };
    
    window.clearAllImages = function() {
        selectedFiles = [];
        updateFileInput();
        updateImagePreview();
        
        // Clear the file input value
        const fileInput = document.getElementById('image-upload');
        if (fileInput) {
            fileInput.value = '';
            clearFieldError(fileInput);
        }
    };
});

// Vietnamese location API functionality
function initializeVietnameseLocations() {
    const provinceSelect = document.getElementById('province-select');
    const districtSelect = document.getElementById('district-select');

    if (!provinceSelect || !districtSelect) {
        return;
    }

    // Load provinces on page load
    loadProvinces();

    // Load districts when province is selected
    provinceSelect.addEventListener('change', function() {
        const selectedOption = this.options[this.selectedIndex];
        const provinceCode = selectedOption.dataset.code;
        
        districtSelect.innerHTML = '<option value="">Đang tải...</option>';
        districtSelect.disabled = true;
        
        // Clear any validation errors for district
        const districtErrorContainer = districtSelect.parentElement.querySelector('.validation-error');
        if (districtErrorContainer) {
            districtErrorContainer.classList.add('hidden');
        }
        districtSelect.classList.remove('border-red-500');
        districtSelect.classList.add('border-gray-300');
        
        if (provinceCode) {
            loadDistricts(provinceCode);
        } else {
            districtSelect.innerHTML = '<option value="">Chọn tỉnh/thành phố trước</option>';
            districtSelect.disabled = true;
        }
    });
}

async function loadProvinces() {
    const provinceSelect = document.getElementById('province-select');
    
    try {
        // Load provinces with depth=1 to get only provinces
        const response = await fetch('https://provinces.open-api.vn/api/?depth=1');
        const provinces = await response.json();
        
        // Clear existing options except the first one
        provinceSelect.innerHTML = '<option value="">Chọn tỉnh/thành phố</option>';
        
        provinces.forEach(province => {
            const option = document.createElement('option');
            option.value = province.name;
            option.textContent = province.name;
            option.dataset.code = province.code;
            provinceSelect.appendChild(option);
        });
    } catch (error) {
        console.error('Error loading provinces:', error);
        // Fallback to static list if API fails
        addFallbackProvinces();
    }
}

async function loadDistricts(provinceCode) {
    const districtSelect = document.getElementById('district-select');
    
    try {
        // Load specific province with depth=2 to get districts
        const response = await fetch(`https://provinces.open-api.vn/api/p/${provinceCode}?depth=2`);
        const provinceData = await response.json();
        
        districtSelect.innerHTML = '<option value="">Chọn quận/huyện</option>';
        
        if (provinceData.districts && provinceData.districts.length > 0) {
            provinceData.districts.forEach(district => {
                const option = document.createElement('option');
                option.value = district.name;
                option.textContent = district.name;
                option.dataset.code = district.code;
                districtSelect.appendChild(option);
            });
            districtSelect.disabled = false;
        } else {
            districtSelect.innerHTML = '<option value="">Không có dữ liệu quận/huyện</option>';
        }
    } catch (error) {
        console.error('Error loading districts:', error);
        districtSelect.innerHTML = '<option value="">Lỗi tải dữ liệu quận/huyện</option>';
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
