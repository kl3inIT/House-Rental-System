

// Initialize interactive features when page loads
document.addEventListener('DOMContentLoaded', function() {
    console.log('=== PAGE LOADED - DEBUGGING ===');
    
    // Debug carousels found
    const carousels = document.querySelectorAll('.property-carousel');
    console.log(`Found ${carousels.length} property carousels`);
    
    carousels.forEach((carousel, i) => {
        const slides = carousel.querySelectorAll('.carousel-slide');
        const images = carousel.querySelectorAll('.carousel-slide img');
        const prevBtn = carousel.querySelector('.carousel-prev');
        const nextBtn = carousel.querySelector('.carousel-next');
        
        console.log(`Carousel ${i}:`);
        console.log(`  - Slides: ${slides.length}`);
        console.log(`  - Images: ${images.length}`);
        console.log(`  - Prev button: ${prevBtn ? 'Found' : 'Missing'}`);
        console.log(`  - Next button: ${nextBtn ? 'Found' : 'Missing'}`);
        
        images.forEach((img, j) => {
            console.log(`  - Image ${j}: ${img.src}`);
        });
    });
    
    initializeFavoriteButtons();
    initializeInteractiveElements();
    initializeImageCarousels();
    initializeLazyLoading();
});



// Initialize interactive elements
function initializeInteractiveElements() {
    // Smooth scrolling for anchor links
    document.querySelectorAll('a[href^="#"]').forEach(anchor => {
        anchor.addEventListener('click', function (e) {
            e.preventDefault();
            const target = document.querySelector(this.getAttribute('href'));
            if (target) {
                target.scrollIntoView({
                    behavior: 'smooth',
                    block: 'start'
                });
            }
        });
    });
}

// Initialize favorite buttons
function initializeFavoriteButtons() {
    document.querySelectorAll('.favorite-btn').forEach(button => {
        button.addEventListener('click', function(e) {
            e.preventDefault();
            e.stopPropagation();
            
            const propertyId = this.dataset.propertyId;
            const isFavorite = this.dataset.favorite === 'true';
            
            toggleFavorite(propertyId, !isFavorite, this);
        });
    });
}

// Toggle favorite status
async function toggleFavorite(propertyId, isFavorite, button) {
    try {
        // Show loading state
        button.disabled = true;
        button.classList.add('opacity-50');
        

        await new Promise(resolve => setTimeout(resolve, 300));
        
        // Update button state
        const icon = button.querySelector('i');
        if (isFavorite) {
            icon.className = 'fas fa-heart text-red-500';
            button.dataset.favorite = 'true';
        } else {
            icon.className = 'far fa-heart text-red-500';
            button.dataset.favorite = 'false';
        }
        
        // Show feedback
        showNotification(isFavorite ? 'Added to favorites' : 'Removed from favorites', 'success');
        
    } catch (error) {
        console.error('Error toggling favorite:', error);
        showNotification('Failed to update favorite', 'error');
    } finally {
        button.disabled = false;
        button.classList.remove('opacity-50');
    }
}

// Show notification
function showNotification(message, type = 'info') {
    const notification = document.createElement('div');
    notification.className = `fixed top-4 right-4 z-50 p-4 rounded-md shadow-lg max-w-sm transition-all duration-300 ${
        type === 'success' ? 'bg-green-50 border border-green-200 text-green-800' :
        type === 'error' ? 'bg-red-50 border border-red-200 text-red-800' :
        'bg-blue-50 border border-blue-200 text-blue-800'
    }`;
    
    notification.innerHTML = `
        <div class="flex items-center">
            <i class="fas ${type === 'success' ? 'fa-check-circle' : type === 'error' ? 'fa-exclamation-circle' : 'fa-info-circle'} mr-2"></i>
            <span>${message}</span>
            <button onclick="this.parentElement.parentElement.remove()" class="ml-auto text-lg leading-none">&times;</button>
        </div>
    `;
    
    document.body.appendChild(notification);
    
    // Auto remove after 3 seconds
    setTimeout(() => {
        if (notification.parentNode) {
            notification.style.opacity = '0';
            notification.style.transform = 'translateX(100%)';
            setTimeout(() => notification.remove(), 300);
        }
         }, 3000);
}

// CSS will be moved to separate CSS file

// Image carousel functionality for multiple property images
function initializeImageCarousels() {
    document.querySelectorAll('.property-carousel').forEach((carousel, carouselIndex) => {
        const slides = carousel.querySelectorAll('.carousel-slide');
        const prevBtn = carousel.querySelector('.carousel-prev');
        const nextBtn = carousel.querySelector('.carousel-next');
        const dots = carousel.querySelectorAll('.carousel-dot');
        
        console.log(`Carousel ${carouselIndex}: Found ${slides.length} slides, ${dots.length} dots`);
        console.log('Prev button:', prevBtn);
        console.log('Next button:', nextBtn);
        
        // Skip if no slides or only one slide
        if (slides.length <= 1) {
            console.log(`Carousel ${carouselIndex}: Skipping (only ${slides.length} slides)`);
            // return; // Temporarily disabled for debugging
        }
        
        let currentSlide = 0;
        let autoPlayInterval = null;
        
        // Navigation functions
        function showSlide(index) {
            console.log(`Carousel ${carouselIndex}: showSlide(${index}) called, currentSlide: ${currentSlide}`);
            
            // Ensure index is valid
            if (index < 0 || index >= slides.length) {
                console.log(`Invalid index: ${index}, slides.length: ${slides.length}`);
                return;
            }
            
            // Hide all slides
            slides.forEach((slide, i) => {
                slide.classList.remove('opacity-100', 'active');
                slide.classList.add('opacity-0');
                slide.style.zIndex = '1';
                slide.style.border = '2px solid blue'; // Debug border
                console.log(`Slide ${i}:`, slide.className);
            });
            
            // Show current slide
            const currentSlideEl = slides[index];
            currentSlideEl.classList.remove('opacity-0');
            currentSlideEl.classList.add('opacity-100', 'active');
            currentSlideEl.style.zIndex = '2';
            currentSlideEl.style.border = '2px solid red'; // Active slide border
            console.log(`Active slide ${index}:`, currentSlideEl.className);
            
            // Update dots
            dots.forEach((dot, i) => {
                dot.classList.remove('bg-opacity-80');
                dot.classList.add('bg-opacity-50');
            });
            
            if (dots[index]) {
                dots[index].classList.remove('bg-opacity-50');
                dots[index].classList.add('bg-opacity-80');
            }
            
            currentSlide = index;
            console.log(`Carousel ${carouselIndex}: slide changed to ${index}`);
        }
        
        function nextSlide() {
            const next = (currentSlide + 1) % slides.length;
            showSlide(next);
        }
        
        function prevSlide() {
            const prev = (currentSlide - 1 + slides.length) % slides.length;
            showSlide(prev);
        }
        
        // Auto-play functionality
        function startAutoPlay() {
            stopAutoPlay();
            autoPlayInterval = setInterval(() => {
                nextSlide();
            }, 4000);
        }
        
        function stopAutoPlay() {
            if (autoPlayInterval) {
                clearInterval(autoPlayInterval);
                autoPlayInterval = null;
            }
        }
        
        // Event listeners for navigation buttons
        if (prevBtn) {
            // Debug mouse events
            prevBtn.addEventListener('mouseenter', () => {
                console.log(`Carousel ${carouselIndex}: PREV button HOVER!`);
                prevBtn.style.background = 'yellow';
            });
            
            prevBtn.addEventListener('mouseleave', () => {
                console.log(`Carousel ${carouselIndex}: PREV button LEAVE!`);
                prevBtn.style.background = '';
            });
            
            prevBtn.addEventListener('click', (e) => {
                console.log(`Carousel ${carouselIndex}: PREV button clicked!`);
                
                // Visual feedback
                prevBtn.style.background = 'red';
                setTimeout(() => prevBtn.style.background = '', 500);
                
                e.preventDefault();
                e.stopPropagation();
                prevSlide();
                stopAutoPlay();
            });
        } else {
            console.log(`Carousel ${carouselIndex}: No prev button found`);
        }
        
        if (nextBtn) {
            // Debug mouse events
            nextBtn.addEventListener('mouseenter', () => {
                console.log(`Carousel ${carouselIndex}: NEXT button HOVER!`);
                nextBtn.style.background = 'yellow';
            });
            
            nextBtn.addEventListener('mouseleave', () => {
                console.log(`Carousel ${carouselIndex}: NEXT button LEAVE!`);
                nextBtn.style.background = '';
            });
            
            nextBtn.addEventListener('click', (e) => {
                console.log(`Carousel ${carouselIndex}: NEXT button clicked!`);
                
                // Visual feedback  
                nextBtn.style.background = 'red';
                setTimeout(() => nextBtn.style.background = '', 500);
                
                e.preventDefault();
                e.stopPropagation();
                nextSlide();
                stopAutoPlay();
            });
        } else {
            console.log(`Carousel ${carouselIndex}: No next button found`);
        }
        
        // Dot navigation
        dots.forEach((dot, index) => {
            dot.addEventListener('click', (e) => {
                e.preventDefault();
                e.stopPropagation();
                showSlide(index);
                stopAutoPlay();
            });
        });
        
        // Mouse events for auto-play
        carousel.addEventListener('mouseenter', () => {
            stopAutoPlay();
        });
        
        carousel.addEventListener('mouseleave', () => {
            startAutoPlay();
        });
        
        // Touch events for mobile swipe
        let touchStartX = 0;
        let touchEndX = 0;
        
        carousel.addEventListener('touchstart', (e) => {
            touchStartX = e.changedTouches[0].screenX;
            stopAutoPlay();
        }, { passive: true });
        
        carousel.addEventListener('touchend', (e) => {
            touchEndX = e.changedTouches[0].screenX;
            handleSwipe();
        }, { passive: true });
        
        function handleSwipe() {
            const swipeThreshold = 50;
            const swipeDistance = touchEndX - touchStartX;
            
            if (Math.abs(swipeDistance) > swipeThreshold) {
                if (swipeDistance > 0) {
                    prevSlide();
                } else {
                    nextSlide();
                }
            }
        }
        
        // Initialize first slide
        showSlide(0);
        
        // Start auto-play
        startAutoPlay();
    });
}

// Debug function
window.debugCarousel = function(element) {
    const carousel = element.closest('.property-carousel');
    const slides = carousel.querySelectorAll('.carousel-slide');
    const buttons = carousel.querySelectorAll('.carousel-prev, .carousel-next');
    
    console.log('=== DEBUG CAROUSEL ===');
    console.log('Carousel element:', carousel);
    console.log('Slides found:', slides.length);
    console.log('Buttons found:', buttons.length);
    
    slides.forEach((slide, i) => {
        console.log(`Slide ${i}:`, slide.className, slide.style.zIndex);
    });
    
    buttons.forEach((btn, i) => {
        console.log(`Button ${i}:`, btn.className);
        console.log('Button z-index:', btn.style.zIndex);
        console.log('Button position:', btn.getBoundingClientRect());
        
        // Highlight button
        btn.style.background = 'lime';
        btn.style.border = '3px solid orange';
        
        setTimeout(() => {
            btn.click(); // Force click after highlight
        }, 1000);
    });
};

// Manual button click for testing
window.clickNext = function(carouselIndex = 0) {
    const carousels = document.querySelectorAll('.property-carousel');
    const carousel = carousels[carouselIndex];
    if (carousel) {
        const nextBtn = carousel.querySelector('.carousel-next');
        if (nextBtn) {
            console.log('Manual clicking next button...');
            nextBtn.click();
        }
    }
};

window.clickPrev = function(carouselIndex = 0) {
    const carousels = document.querySelectorAll('.property-carousel');
    const carousel = carousels[carouselIndex];
    if (carousel) {
        const prevBtn = carousel.querySelector('.carousel-prev');
        if (prevBtn) {
            console.log('Manual clicking prev button...');
            prevBtn.click();
        }
    }
};

// Lazy loading for images
function initializeLazyLoading() {
    const imageObserver = new IntersectionObserver((entries, observer) => {
        entries.forEach(entry => {
            if (entry.isIntersecting) {
                const img = entry.target;
                const carousel = img.closest('.property-carousel');
                
                // Load image
                img.style.opacity = '1';
                
                // Preload other images in the same carousel
                if (carousel) {
                    const otherImages = carousel.querySelectorAll('.carousel-slide img');
                    otherImages.forEach(otherImg => {
                        if (otherImg !== img && !otherImg.complete) {
                            // Create a new image element to preload
                            const preloadImg = new Image();
                            preloadImg.src = otherImg.src;
                        }
                    });
                }
                
                observer.unobserve(img);
            }
        });
    }, {
        root: null,
        rootMargin: '50px',
        threshold: 0.1
    });

    // Observe all carousel images
    document.querySelectorAll('.carousel-slide img').forEach(img => {
        imageObserver.observe(img);
        
        // Add loading event listeners
        img.addEventListener('load', function() {
            this.style.opacity = '1';
        });
        
        img.addEventListener('error', function() {
            // Handle image load error
            const placeholder = document.createElement('div');
            placeholder.className = 'w-full h-full bg-gradient-to-br from-gray-200 to-gray-300 flex items-center justify-center';
            placeholder.innerHTML = '<i class="fas fa-image text-4xl text-gray-400"></i>';
            this.parentNode.replaceChild(placeholder, this);
        });
    });
}

// Utility function to preload images
function preloadImages(urls) {
    urls.forEach(url => {
        const img = new Image();
        img.src = url;
    });
}


