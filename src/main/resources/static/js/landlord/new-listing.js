// Image preview functionality
document.addEventListener('DOMContentLoaded', function() {
    const imageUpload = document.getElementById('image-upload');
    const imagePreview = document.getElementById('image-preview');

    if (imageUpload) {
        imageUpload.addEventListener('change', function(event) {
            const files = event.target.files;
            imagePreview.innerHTML = '';
            
            if (files.length > 0) {
                imagePreview.classList.remove('hidden');
                
                for (let i = 0; i < Math.min(files.length, 10); i++) {
                    const file = files[i];
                    const reader = new FileReader();
                    
                    reader.onload = function(e) {
                        const imgContainer = document.createElement('div');
                        imgContainer.className = 'relative group';
                        imgContainer.innerHTML = `
                            <img src="${e.target.result}" alt="Preview" class="w-full h-24 object-cover rounded-md">
                            <button type="button" class="absolute top-1 right-1 bg-red-500 text-white rounded-full w-6 h-6 flex items-center justify-center text-xs opacity-0 group-hover:opacity-100 transition-opacity" onclick="updateFileList(this);">
                                ×
                            </button>
                        `;
                        imagePreview.appendChild(imgContainer);
                    };
                    
                    reader.readAsDataURL(file);
                }
            } else {
                imagePreview.classList.add('hidden');
            }
        });
    }

    // Initialize Vietnamese location dropdowns
    initializeVietnameseLocations();
});

function updateFileList(button) {
    button.parentElement.remove();
    const imagePreview = document.getElementById('image-preview');
    const remainingImages = imagePreview.children.length;
    if (remainingImages === 0) {
        imagePreview.classList.add('hidden');
    }
}

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
