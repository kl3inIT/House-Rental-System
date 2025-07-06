// Search properties functionality
document.addEventListener('DOMContentLoaded', function() {
    initializeLocationAutocomplete();
    initializeFilterHandlers();
});

function initializeFilterHandlers() {
    // Add any filter-specific functionality here
    const filterInputs = document.querySelectorAll('select, input[type="number"]');
    filterInputs.forEach(input => {
        input.addEventListener('change', function() {
            // Auto-submit form when filters change
            const form = this.closest('form');
            if (form) {
                form.submit();
            }
        });
    });
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
        hideAutocomplete();
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
        item.className = 'autocomplete-item px-4 py-2 hover:bg-gray-100 cursor-pointer text-sm';
        item.textContent = result.name;
        
        item.addEventListener('click', function() {
            selectLocation(result.name);
        });
        
        autocompleteContainer.appendChild(item);
    });
    
    autocompleteContainer.classList.remove('hidden');
}

function selectLocation(locationName) {
    const locationInput = document.querySelector('input[name="location"]');
    locationInput.value = locationName;
    hideAutocomplete();
}

function hideAutocomplete() {
    const autocompleteContainer = document.getElementById('location-autocomplete');
    if (autocompleteContainer) {
        autocompleteContainer.classList.add('hidden');
    }
}
