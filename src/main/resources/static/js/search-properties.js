// Search properties functionality
document.addEventListener('DOMContentLoaded', function() {
    initializeLocationAutocomplete();
    initializeFilterHandlers();
    initializeSortingHandler();
    initializeLoadingStates();
});

function initializeFilterHandlers() {
    // Auto-submit form when filters change
    const filterInputs = document.querySelectorAll('input[type="checkbox"], select[name="minBedrooms"], select[name="minBathrooms"]');
    filterInputs.forEach(input => {
        input.addEventListener('change', function() {
            // Don't auto-submit for published date filters if they're part of a group
            if (this.name === 'publishedRanges') {
                // Handle published date filters with custom logic
                handlePublishedDateFilterChange(this);
                return;
            }
            
            const form = this.closest('form');
            if (form) {
                showLoadingState();
                form.submit();
            }
        });
    });
}

function handlePublishedDateFilterChange(checkbox) {
    const form = checkbox.closest('form');
    if (!form) return;
    
    // Get all published date checkboxes
    const publishedCheckboxes = form.querySelectorAll('input[name="publishedRanges"]');
    const checkedValues = Array.from(publishedCheckboxes)
        .filter(cb => cb.checked)
        .map(cb => cb.value);
    
    // If no published date filters are selected, submit the form
    if (checkedValues.length === 0) {
        showLoadingState();
        form.submit();
        return;
    }
    
    // If this is a "shorter" time range being selected, uncheck "longer" ranges
    const timeRanges = ['today', 'week', 'month', '3months', '6months', 'year'];
    const currentIndex = timeRanges.indexOf(checkbox.value);
    
    if (checkbox.checked && currentIndex >= 0) {
        // Uncheck longer time ranges
        publishedCheckboxes.forEach(cb => {
            const cbIndex = timeRanges.indexOf(cb.value);
            if (cbIndex > currentIndex) {
                cb.checked = false;
            }
        });
    }
    
    // Submit the form after a short delay to allow for UI updates
    setTimeout(() => {
        showLoadingState();
        form.submit();
    }, 100);
}

function initializeSortingHandler() {
    const sortSelect = document.querySelector('select[name="sortBy"]');
    if (sortSelect) {
        sortSelect.addEventListener('change', function() {
            showLoadingState();
            // Update URL with new sort parameter
            const url = new URL(window.location);
            url.searchParams.set('sortBy', this.value);
            window.location.href = url.toString();
        });
    }
}

function initializeLoadingStates() {
    // Add loading indicator to the page
    const loadingIndicator = document.createElement('div');
    loadingIndicator.id = 'loading-indicator';
    loadingIndicator.className = 'fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 hidden';
    loadingIndicator.innerHTML = `
        <div class="bg-white rounded-lg p-6 flex items-center space-x-3">
            <div class="animate-spin rounded-full h-6 w-6 border-b-2 border-blue-600"></div>
            <span class="text-gray-700">Loading...</span>
        </div>
    `;
    document.body.appendChild(loadingIndicator);
}

function showLoadingState() {
    const loadingIndicator = document.getElementById('loading-indicator');
    if (loadingIndicator) {
        loadingIndicator.classList.remove('hidden');
    }
}

function hideLoadingState() {
    const loadingIndicator = document.getElementById('loading-indicator');
    if (loadingIndicator) {
        loadingIndicator.classList.add('hidden');
    }
}

function initializeLocationAutocomplete() {
    const locationInput = document.querySelector('input[name="location"]');
    if (!locationInput) return;

    // Create autocomplete container
    const autocompleteContainer = document.createElement('div');
    autocompleteContainer.className = 'absolute z-50 w-full bg-white border border-gray-300 rounded-md shadow-lg max-h-60 overflow-y-auto hidden';
    autocompleteContainer.id = 'location-autocomplete';
    locationInput.parentElement.style.position = 'relative';
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
            hideAutocomplete();
            return;
        }

        // Show loading indicator in autocomplete
        showAutocompleteLoading();

        // Debounce search
        searchTimeout = setTimeout(() => {
            searchLocations(query);
        }, 300);
    });

    // Handle keyboard navigation
    locationInput.addEventListener('keydown', function(e) {
        const autocompleteContainer = document.getElementById('location-autocomplete');
        const items = autocompleteContainer.querySelectorAll('.autocomplete-item');
        
        if (!autocompleteContainer.classList.contains('hidden') && items.length > 0) {
            switch (e.key) {
                case 'ArrowDown':
                    e.preventDefault();
                    selectedIndex = Math.min(selectedIndex + 1, items.length - 1);
                    updateSelectedItem(items);
                    break;
                case 'ArrowUp':
                    e.preventDefault();
                    selectedIndex = Math.max(selectedIndex - 1, -1);
                    updateSelectedItem(items);
                    break;
                case 'Enter':
                    e.preventDefault();
                    if (selectedIndex >= 0 && selectedIndex < items.length) {
                        items[selectedIndex].click();
                    } else {
                        // Submit the form if no item is selected
                        const form = locationInput.closest('form');
                        if (form) {
                            showLoadingState();
                            form.submit();
                        }
                    }
                    break;
                case 'Escape':
                    hideAutocomplete();
                    selectedIndex = -1;
                    break;
            }
        }
    });

    // Hide autocomplete when clicking outside
    document.addEventListener('click', function(e) {
        if (!locationInput.contains(e.target) && !autocompleteContainer.contains(e.target)) {
            hideAutocomplete();
            selectedIndex = -1;
        }
    });
}

function showAutocompleteLoading() {
    const autocompleteContainer = document.getElementById('location-autocomplete');
    if (autocompleteContainer) {
        autocompleteContainer.innerHTML = '<div class="px-4 py-3 text-gray-500 text-center">Loading...</div>';
        autocompleteContainer.classList.remove('hidden');
    }
}

function updateSelectedItem(items) {
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

async function searchLocations(query) {
    try {
        // Search in provinces first
        const provinceResponse = await fetch('https://vietnamlabs.com/api/vietnamprovince');
        
        if (!provinceResponse.ok) {
            throw new Error(`HTTP error! status: ${provinceResponse.status}`);
        }
        
        const provinceData = await provinceResponse.json();
        
        let results = [];
        
        if (provinceData.success && provinceData.data) {
            // Search in provinces
            const matchingProvinces = provinceData.data.filter(provinceData => 
                provinceData.province.toLowerCase().includes(query.toLowerCase())
            );
            
            results.push(...matchingProvinces.map(provinceData => ({
                name: provinceData.province,
                type: 'province'
            })));
            
            // Search in wards for matching provinces
            for (const provinceData of provinceData.data) {
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
        
        displayAutocompleteResults(uniqueResults);
        
    } catch (error) {
        console.error('Error searching locations:', error);
        // Fallback to static search
        displayFallbackResults(query);
    }
}

function displayAutocompleteResults(results) {
    const autocompleteContainer = document.getElementById('location-autocomplete');
    if (results.length === 0) {
        hideAutocomplete();
        return;
    }
    autocompleteContainer.innerHTML = '';
    results.forEach((result, index) => {
        const item = document.createElement('div');
        item.className = 'autocomplete-item px-4 py-2 hover:bg-gray-100 cursor-pointer text-sm text-black text-left';
        item.textContent = result.name;
        item.addEventListener('click', function() {
            selectLocation(result.name);
        });
        autocompleteContainer.appendChild(item);
    });
    autocompleteContainer.classList.remove('hidden');
}

function displayFallbackResults(query) {
    const autocompleteContainer = document.getElementById('location-autocomplete');
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
        hideAutocomplete();
        return;
    }
    autocompleteContainer.innerHTML = '';
    matchingProvinces.forEach(province => {
        const item = document.createElement('div');
        item.className = 'autocomplete-item px-4 py-2 hover:bg-gray-100 cursor-pointer text-sm text-black text-left';
        item.textContent = province;
        item.addEventListener('click', function() {
            selectLocation(province);
        });
        autocompleteContainer.appendChild(item);
    });
    autocompleteContainer.classList.remove('hidden');
}

function selectLocation(locationName) {
    const locationInput = document.querySelector('input[name="location"]');
    locationInput.value = locationName;
    hideAutocomplete();
    
    // Auto-submit the form when location is selected
    const form = locationInput.closest('form');
    if (form) {
        showLoadingState();
        form.submit();
    }
}

function hideAutocomplete() {
    const autocompleteContainer = document.getElementById('location-autocomplete');
    if (autocompleteContainer) {
        autocompleteContainer.classList.add('hidden');
    }
}

// Utility function to clear all filters
function clearAllFilters() {
    showLoadingState();
    window.location.href = '/properties/search';
}

// Handle form submission with loading state
document.addEventListener('submit', function(e) {
    if (e.target.matches('form[action*="search"]')) {
        showLoadingState();
    }
});

// Hide loading state when page is fully loaded
window.addEventListener('load', function() {
    hideLoadingState();
});
