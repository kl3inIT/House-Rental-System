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
    const locationInput = document.querySelector('form[th\\:action*="search"] input[name="location"]');
    if (!locationInput) return;

    // Create autocomplete container
    const autocompleteContainer = document.createElement('div');
    autocompleteContainer.className = 'absolute z-50 w-full bg-white border border-gray-300 rounded-md shadow-lg max-h-60 overflow-y-auto hidden';
    autocompleteContainer.id = 'home-location-autocomplete';
    locationInput.parentElement.appendChild(autocompleteContainer);

    let searchTimeout;
    let selectedIndex = -1;

    locationInput.addEventListener('input', function() {
        const query = this.value.trim();
        
        // Clear previous timeout
        if (searchTimeout) {
            clearTimeout(searchTimeout);
        }

        // Hide autocomplete if query is too short
        if (query.length < 2) {
            hideHomeAutocomplete();
            return;
        }

        // Debounce search
        searchTimeout = setTimeout(() => {
            searchHomeLocations(query);
        }, 300);
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

    // Hide autocomplete when clicking outside
    document.addEventListener('click', function(e) {
        if (!locationInput.contains(e.target) && !autocompleteContainer.contains(e.target)) {
            hideHomeAutocomplete();
        }
    });
}

async function searchHomeLocations(query) {
    const autocompleteContainer = document.getElementById('home-location-autocomplete');
    
    try {
        // Search provinces first
        const response = await fetch(`https://provinces.open-api.vn/api/?depth=1`);
        const provinces = await response.json();
        
        // Filter provinces by query
        const matchingProvinces = provinces.filter(province => 
            province.name.toLowerCase().includes(query.toLowerCase())
        ).slice(0, 5);

        // Search districts for matching provinces
        const districtPromises = matchingProvinces.map(async (province) => {
            try {
                const districtResponse = await fetch(`https://provinces.open-api.vn/api/p/${province.code}?depth=2`);
                const provinceData = await districtResponse.json();
                return provinceData.districts || [];
            } catch (error) {
                return [];
            }
        });

        const districtResults = await Promise.all(districtPromises);
        
        // Combine and filter results
        const allResults = [];
        
        // Add provinces
        matchingProvinces.forEach(province => {
            allResults.push({
                name: province.name,
                type: 'province'
            });
        });

        // Add matching districts
        districtResults.forEach((districts, index) => {
            const matchingDistricts = districts.filter(district => 
                district.name.toLowerCase().includes(query.toLowerCase())
            ).slice(0, 3);
            
            matchingDistricts.forEach(district => {
                allResults.push({
                    name: `${district.name}, ${matchingProvinces[index].name}`,
                    type: 'district'
                });
            });
        });

        displayHomeAutocompleteResults(allResults);
        
    } catch (error) {
        console.error('Error searching locations:', error);
        // Fallback to static search
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
        item.className = 'autocomplete-item px-4 py-2 hover:bg-gray-100 cursor-pointer text-sm';
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
        item.className = 'autocomplete-item px-4 py-2 hover:bg-gray-100 cursor-pointer text-sm';
        item.textContent = province;
        
        item.addEventListener('click', function() {
            selectHomeLocation(province);
        });
        
        autocompleteContainer.appendChild(item);
    });
    
    autocompleteContainer.classList.remove('hidden');
}

function selectHomeLocation(locationName) {
    const locationInput = document.querySelector('form[th\\:action*="search"] input[name="location"]');
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
            item.classList.add('bg-blue-100');
            item.classList.remove('hover:bg-gray-100');
        } else {
            item.classList.remove('bg-blue-100');
            item.classList.add('hover:bg-gray-100');
        }
    });
}


