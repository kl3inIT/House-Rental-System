// Simple page functionality
document.addEventListener('DOMContentLoaded', function() {
    initializeFavorites();
    initializeHomeSearch();
});

// Simple favorites
function initializeFavorites() {
    document.querySelectorAll('.favorite-btn').forEach(button => {
        button.addEventListener('click', function(e) {
            e.stopPropagation();
            const icon = this.querySelector('i');
            const isFavorite = icon.classList.contains('fas');
            if (isFavorite) {
                icon.className = 'far fa-heart text-gray-600 hover:text-red-500 text-sm';
            } else {
                icon.className = 'fas fa-heart text-red-500 text-sm';
            }
        });
    });
}

// Home search functionality
function initializeHomeSearch() {
    const locationInput = document.querySelector('form[action*="search"] input[name="location"]');
    if (!locationInput) {
        return;
    }
    // Create autocomplete container
    const autocompleteContainer = document.createElement('div');
    autocompleteContainer.className = 'absolute z-50 w-full bg-white border border-gray-300 rounded-md shadow-lg max-h-60 overflow-y-auto hidden';
    autocompleteContainer.id = 'home-location-autocomplete';
    autocompleteContainer.style.position = 'absolute';
    autocompleteContainer.style.top = '100%';
    autocompleteContainer.style.left = '0';
    autocompleteContainer.style.right = '0';
    autocompleteContainer.style.zIndex = '9999';
    locationInput.parentElement.style.position = 'relative';
    locationInput.parentElement.appendChild(autocompleteContainer);

    let searchTimeout;
    let selectedIndex = -1;

    locationInput.addEventListener('input', function() {
        const query = this.value.trim();
        if (searchTimeout) {
            clearTimeout(searchTimeout);
        }
        if (query.length < 1) {
            hideHomeAutocomplete();
            return;
        }
        showHomeLoading();
        searchTimeout = setTimeout(() => {
            searchHomeLocations(query);
        }, 300);
    });

    locationInput.addEventListener('focus', function() {
        // No debug
    });

    locationInput.addEventListener('keydown', function(e) {
        const items = autocompleteContainer.querySelectorAll('.autocomplete-item');
        switch(e.key) {
            case 'ArrowDown':
                e.preventDefault();
                selectedIndex = Math.min(selectedIndex + 1, items.length - 1);
                updateHomeSelection(items);
                break;
            case 'ArrowUp':
                e.preventDefault();
                selectedIndex = Math.max(selectedIndex - 1, -1);
                updateHomeSelection(items);
                break;
            case 'Enter':
                e.preventDefault();
                if (selectedIndex >= 0 && items[selectedIndex]) {
                    selectHomeLocation(items[selectedIndex].textContent);
                }
                break;
            case 'Escape':
                hideHomeAutocomplete();
                break;
        }
    });

    document.addEventListener('click', function(e) {
        if (!locationInput.contains(e.target) && !autocompleteContainer.contains(e.target)) {
            hideHomeAutocomplete();
        }
    });
}

function showHomeLoading() {
    const autocompleteContainer = document.getElementById('home-location-autocomplete');
    if (autocompleteContainer) {
        autocompleteContainer.innerHTML = '<div class="px-4 py-3 text-gray-500 text-center">Loading...</div>';
        autocompleteContainer.classList.remove('hidden');
    }
}

async function searchHomeLocations(query) {
    const autocompleteContainer = document.getElementById('home-location-autocomplete');
    try {
        // Search in provinces first with timeout
        const controller = new AbortController();
        const timeoutId = setTimeout(() => controller.abort(), 5000); // 5 second timeout
        const response = await fetch('https://vietnamlabs.com/api/vietnamprovince', {
            signal: controller.signal,
            method: 'GET',
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            }
        });
        clearTimeout(timeoutId);
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        const data = await response.json();
        let results = [];
        if (data.success && data.data) {
            // Search in provinces
            const matchingProvinces = data.data.filter(provinceData => 
                provinceData.province.toLowerCase().includes(query.toLowerCase())
            );
            results.push(...matchingProvinces.map(provinceData => ({
                name: provinceData.province,
                type: 'province'
            })));
            // Search in wards for matching provinces
            for (const provinceData of data.data) {
                if (provinceData.wards && provinceData.wards.length > 0) {
                    const matchingWards = provinceData.wards.filter(ward => 
                        ward.name.toLowerCase().includes(query.toLowerCase())
                    );
                    results.push(...matchingWards.map(ward => ({
                        name: `${ward.name}, ${provinceData.province}`,
                        type: 'ward'
                    })));
                }
            }
        }
        // Limit results and remove duplicates
        const uniqueResults = results.filter((result, index, self) => 
            index === self.findIndex(r => r.name === result.name)
        ).slice(0, 10);
        if (uniqueResults.length > 0) {
            displayHomeAutocompleteResults(uniqueResults);
        } else {
            autocompleteContainer.innerHTML = '<div class="px-4 py-3 text-gray-500 text-center">No results found</div>';
            autocompleteContainer.classList.remove('hidden');
        }
    } catch (error) {
        displayHomeFallbackResults(query);
    }
}

function displayHomeAutocompleteResults(results) {
    const autocompleteContainer = document.getElementById('home-location-autocomplete');
    if (results.length === 0) {
        hideHomeAutocomplete();
        return;
    }
    autocompleteContainer.innerHTML = '';
    results.forEach((result, index) => {
        const item = document.createElement('div');
        item.className = 'autocomplete-item px-4 py-2 hover:bg-gray-100 cursor-pointer text-sm text-black text-left';
        item.textContent = result.name;
        item.addEventListener('click', function() {
            selectHomeLocation(result.name);
        });
        autocompleteContainer.appendChild(item);
    });
    autocompleteContainer.classList.remove('hidden');
}

function displayHomeFallbackResults(query) {
    const autocompleteContainer = document.getElementById('home-location-autocomplete');
    const vietnameseProvinces = [
        'Hà Nội', 'TP Hồ Chí Minh', 'Đà Nẵng', 'Hải Phòng', 'Cần Thơ',
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
        'Vĩnh Phúc', 'Yên Bái', 'Phú Yên'
    ];
    const matchingProvinces = vietnameseProvinces.filter(province => 
        province.toLowerCase().includes(query.toLowerCase())
    ).slice(0, 8);
    if (matchingProvinces.length === 0) {
        hideHomeAutocomplete();
        return;
    }
    autocompleteContainer.innerHTML = '';
    matchingProvinces.forEach(province => {
        const item = document.createElement('div');
        item.className = 'autocomplete-item px-4 py-2 hover:bg-gray-100 cursor-pointer text-sm text-black text-left';
        item.textContent = province;
        item.addEventListener('click', function() {
            selectHomeLocation(province);
        });
        autocompleteContainer.appendChild(item);
    });
    autocompleteContainer.classList.remove('hidden');
}

function selectHomeLocation(locationName) {
    const locationInput = document.querySelector('form[action*="search"] input[name="location"]');
    locationInput.value = locationName;
    hideHomeAutocomplete();
}

function hideHomeAutocomplete() {
    const autocompleteContainer = document.getElementById('home-location-autocomplete');
    if (autocompleteContainer) {
        autocompleteContainer.classList.add('hidden');
    }
}

function updateHomeSelection(items) {
    items.forEach((item, index) => {
        if (index === selectedIndex) {
            item.classList.add('bg-blue-100', 'text-blue-900');
            item.classList.remove('hover:bg-gray-100');
        } else {
            item.classList.remove('bg-blue-100', 'text-blue-900');
            item.classList.add('hover:bg-gray-100');
        }
    });
}


