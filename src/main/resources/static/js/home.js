// Simple page functionality
document.addEventListener('DOMContentLoaded', function() {
    initializeHomeSearch();
    initializeWishlistToggles();
    createToastContainer();
});

// Toast Notification System
function createToastContainer() {
    if (document.getElementById('toast-container')) return;
    
    const container = document.createElement('div');
    container.id = 'toast-container';
    container.className = 'fixed top-4 right-4 z-50 space-y-2';
    document.body.appendChild(container);
}

function showToast(message, type = 'success') {
    const container = document.getElementById('toast-container');
    
    const toast = document.createElement('div');
    const bgColor = type === 'success' ? 'bg-green-500' : 'bg-red-500';
    
    toast.className = `${bgColor} text-white px-6 py-3 rounded-lg shadow-lg mb-2 transform transition-all duration-300 translate-x-full opacity-0`;
    toast.style.minWidth = '300px';
    
    toast.innerHTML = `
        <div class="flex items-center justify-between">
            <span class="font-medium">${message}</span>
            <button onclick="this.parentElement.parentElement.remove()" class="ml-4 text-white hover:text-gray-200">
                <i class="fas fa-times"></i>
            </button>
        </div>
    `;
    
    container.appendChild(toast);
    
    // Animate in
    setTimeout(() => {
        toast.classList.remove('translate-x-full', 'opacity-0');
    }, 100);
    
    // Auto remove after 3 seconds
    setTimeout(() => {
        toast.classList.add('translate-x-full', 'opacity-0');
        setTimeout(() => {
            if (toast.parentElement) {
                toast.remove();
            }
        }, 300);
    }, 3000);
}

// Wishlist AJAX functionality
function initializeWishlistToggles() {
    const wishlistForms = document.querySelectorAll('form[action*="/wishlist/toggle/"]');
    
    wishlistForms.forEach(form => {
        const button = form.querySelector('button[type="submit"]');
        const heartIcon = button.querySelector('i');
        
        form.addEventListener('submit', async function(e) {
            e.preventDefault();
            
            const formData = new FormData(form);
            const propertyId = form.action.split('/').pop();
            
            // Add loading state (make icon bolder)
            heartIcon.classList.add('fa-pulse');
            button.style.transform = 'scale(1.1)';
            button.style.opacity = '0.8';
            
            try {
                const response = await fetch(form.action, {
                    method: 'POST',
                    body: formData,
                    headers: {
                        'X-Requested-With': 'XMLHttpRequest'
                    }
                });
                
                if (response.ok) {
                    const result = await response.json();
                    
                    if (result.added) {
                        heartIcon.classList.remove('far', 'text-gray-600');
                        heartIcon.classList.add('fas', 'text-red-500');
                        button.title = 'Remove from wishlist';
                        showToast('❤️ Added to wishlist!', 'success');
                    } else {
                        heartIcon.classList.remove('fas', 'text-red-500');
                        heartIcon.classList.add('far', 'text-gray-600');
                        button.title = 'Add to wishlist';
                        showToast('💔 Removed from wishlist', 'success');
                    }
                    
                    // Update wishlist count in header if exists
                    updateWishlistCount(result.wishlistCount);
                    
                } else {
                    throw new Error('Failed to update wishlist');
                }
                
            } catch (error) {
                console.error('Wishlist error:', error);
                showToast('Failed to update wishlist!', 'error');
            } finally {
                // Always reset loading state
                heartIcon.classList.remove('fa-pulse');
                button.style.transform = '';
                button.style.opacity = '';
            }
        });
    });
}

function updateWishlistCount(count) {
    const wishlistBadge = document.querySelector('[data-wishlist-count]');
    if (wishlistBadge) {
        wishlistBadge.textContent = count;
        // Show badge if count > 0, hide if count = 0
        if (count > 0) {
            wishlistBadge.style.display = 'flex';
        } else {
            wishlistBadge.style.display = 'none';
        }
    }
    
    // Also update mobile menu badge if exists
    const mobileBadges = document.querySelectorAll('a[href="/wishlist"] span');
    mobileBadges.forEach(badge => {
        if (badge && badge.classList.contains('bg-red-500')) {
            badge.textContent = count;
            if (count > 0) {
                badge.style.display = 'flex';
            } else {
                badge.style.display = 'none';
            }
        }
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


