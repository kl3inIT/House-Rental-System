// WebSocket notification handling
class NotificationManager {
    constructor() {
        this.socket = null;
        this.stompClient = null;
        this.userId = this.getCurrentUserId();
        this.notificationCount = 0;
        this.init();
    }

    getCurrentUserId() {
        // Get user ID from meta tag or data attribute
        const userIdMeta = document.querySelector('meta[name="user-id"]');
        if (userIdMeta) {
            return userIdMeta.getAttribute('content');
        }

        // Fallback: try to get from data attribute
        const userIdData = document.querySelector('[data-user-id]');
        if (userIdData) {
            return userIdData.getAttribute('data-user-id');
        }

        return null;
    }

    init() {
        if (!this.userId) {
            console.log('No user ID found, skipping WebSocket connection');
            return;
        }

        try {
            this.socket = new SockJS('/ws');
            this.stompClient = Stomp.over(this.socket);

            this.stompClient.connect({}, (frame) => {
                console.log('Connected to WebSocket');
                this.subscribeToNotifications();
            }, (error) => {
                console.error('WebSocket connection error:', error);
            });
        } catch (error) {
            console.error('Failed to initialize WebSocket:', error);
        }
    }

    subscribeToNotifications() {
        if (!this.stompClient || !this.userId) return;

        this.stompClient.subscribe(`/topic/notifications/${this.userId}`, (message) => {
            try {
                const notification = JSON.parse(message.body);
                this.handleNotification(notification);
            } catch (error) {
                console.error('Error parsing notification:', error);
            }
        });
    }

    handleNotification(notification) {
        console.log('Received notification:', notification);

        // Skip upgrade role related notifications
        if (notification.type === 'upgrade-request-approved' ||
            notification.type === 'upgrade-request-rejected' ||
            notification.type === 'upgrade-request-pending') {
            return;
        }

        // Update notification count
        this.updateNotificationCount();

        // Show notification toast
        this.showNotificationToast(notification);

        // Play notification sound (optional)
        this.playNotificationSound();
    }

    updateNotificationCount() {
        this.notificationCount++;
        const countElement = document.querySelector('.notification-count');
        if (countElement) {
            countElement.textContent = this.notificationCount;
            countElement.classList.remove('hidden');
        }
    }

    showNotificationToast(notification) {
        const toast = document.createElement('div');
        toast.className = `fixed top-4 right-4 z-50 p-4 rounded-md shadow-lg max-w-sm transform transition-all duration-300 translate-x-full ${notification.type === 'rejection' ? 'bg-red-50 border border-red-200 text-red-800' :
            notification.type === 'approval' ? 'bg-green-50 border border-green-200 text-green-800' :
                'bg-blue-50 border border-blue-200 text-blue-800'
            }`;

        const icon = notification.type === 'rejection' ? 'fa-times-circle' :
            notification.type === 'approval' ? 'fa-check-circle' : 'fa-info-circle';

        toast.innerHTML = `
            <div class="flex items-center">
                <div class="flex-shrink-0">
                    <i class="fas ${icon}"></i>
                </div>
                <div class="ml-3">
                    <p class="text-sm font-medium">${notification.message}</p>
                    <p class="text-xs text-gray-500 mt-1">${new Date(notification.timestamp).toLocaleString()}</p>
                </div>
                <div class="ml-auto pl-3">
                    <button type="button" class="inline-flex rounded-md p-1.5 hover:bg-opacity-20 focus:outline-none" onclick="this.parentElement.parentElement.parentElement.remove()">
                        <i class="fas fa-times text-sm"></i>
                    </button>
                </div>
            </div>
        `;

        document.body.appendChild(toast);

        // Animate in
        setTimeout(() => {
            toast.classList.remove('translate-x-full');
        }, 100);

        // Auto remove after 8 seconds
        setTimeout(() => {
            if (toast.parentNode) {
                toast.classList.add('translate-x-full');
                setTimeout(() => {
                    if (toast.parentNode) {
                        toast.remove();
                    }
                }, 300);
            }
        }, 8000);
    }

    playNotificationSound() {
        // Optional: Play a notification sound
        // const audio = new Audio('/sounds/notification.mp3');
        // audio.play().catch(e => console.log('Could not play notification sound:', e));
    }

    disconnect() {
        if (this.stompClient) {
            this.stompClient.disconnect();
        }
        if (this.socket) {
            this.socket.close();
        }
    }
}

// Hiển thị dropdown notification khi click chuông
function setupNotificationDropdown() {
    const bellBtn = document.querySelector('.fa-bell').closest('button');
    if (!bellBtn) return;
    let dropdown = null;
    let isOpen = false;

    bellBtn.addEventListener('click', async function (e) {
        e.stopPropagation();
        if (isOpen) {
            if (dropdown) dropdown.remove();
            isOpen = false;
            return;
        }
        // Fetch notification list
        const res = await fetch('/api/notifications');
        const notifications = await res.json();
        // Tạo dropdown
        dropdown = document.createElement('div');
        dropdown.className = 'absolute right-0 mt-2 w-96 bg-white rounded-md shadow-lg ring-1 ring-black ring-opacity-5 z-50';
        dropdown.style.top = '2.5rem';
        dropdown.style.right = '0';
        dropdown.innerHTML = `
            <div class="py-2 max-h-96 overflow-y-auto">
                <div class="px-4 py-2 border-b font-bold text-gray-700">Thông báo</div>
                ${notifications.length === 0 ? '<div class="px-4 py-4 text-gray-500 text-center">Không có thông báo</div>' :
                notifications.map(n => `
                        <div class="px-4 py-3 border-b hover:bg-gray-50 flex items-start gap-2 ${n.isRead ? '' : 'bg-blue-50'}">
                            <div class="mt-1"><i class="fas fa-info-circle text-blue-500"></i></div>
                            <div class="flex-1">
                                <div class="text-sm font-medium text-gray-900">${n.message}</div>
                                <div class="text-xs text-gray-500 mt-1">${new Date(n.createdAt).toLocaleString()}</div>
                            </div>
                            ${!n.isRead ? `<button class="mark-read-btn text-xs text-blue-600 underline" data-id="${n.id}">Đã đọc</button>` : ''}
                        </div>
                    `).join('')}
            </div>
        `;
        bellBtn.parentElement.appendChild(dropdown);
        isOpen = true;

        // Đánh dấu đã đọc
        dropdown.querySelectorAll('.mark-read-btn').forEach(btn => {
            btn.addEventListener('click', async function (e) {
                e.stopPropagation();
                const id = this.getAttribute('data-id');
                await fetch(`/api/notifications/${id}/read`, { method: 'POST' });
                this.closest('.border-b').classList.remove('bg-blue-50');
                this.remove();
                // Cập nhật badge
                updateNotificationBadge();
            });
        });

        // Đóng dropdown khi click ngoài
        document.addEventListener('click', function closeDropdown(ev) {
            if (dropdown && !dropdown.contains(ev.target) && ev.target !== bellBtn) {
                dropdown.remove();
                isOpen = false;
                document.removeEventListener('click', closeDropdown);
            }
        });
    });
}

// Cập nhật badge theo số thông báo chưa đọc
async function updateNotificationBadge() {
    const res = await fetch('/api/notifications');
    const notifications = await res.json();
    const unread = notifications.filter(n => !n.isRead).length;
    const countElement = document.querySelector('.notification-count');
    if (countElement) {
        countElement.textContent = unread;
        if (unread > 0) {
            countElement.classList.remove('hidden');
        } else {
            countElement.classList.add('hidden');
        }
    }
}

// Initialize notification manager when DOM is loaded
document.addEventListener('DOMContentLoaded', function () {
    // Load SockJS and Stomp if not already loaded
    if (typeof SockJS === 'undefined') {
        const sockjsScript = document.createElement('script');
        sockjsScript.src = 'https://cdn.jsdelivr.net/npm/sockjs-client@1.6.1/dist/sockjs.min.js';
        sockjsScript.onload = function () {
            const stompScript = document.createElement('script');
            stompScript.src = 'https://cdn.jsdelivr.net/npm/stompjs@2.3.3/lib/stomp.min.js';
            stompScript.onload = function () {
                window.notificationManager = new NotificationManager();
            };
            document.head.appendChild(stompScript);
        };
        document.head.appendChild(sockjsScript);
    } else {
        window.notificationManager = new NotificationManager();
    }
});

// Cleanup on page unload
window.addEventListener('beforeunload', function () {
    if (window.notificationManager) {
        window.notificationManager.disconnect();
    }
});

// Gọi khi DOM loaded
if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', () => {
        setupNotificationDropdown();
        updateNotificationBadge();
    });
} else {
    setupNotificationDropdown();
    updateNotificationBadge();
}

// Khi nhận notification mới, cập nhật badge
NotificationManager.prototype.updateNotificationCount = function () {
    updateNotificationBadge();
}; 