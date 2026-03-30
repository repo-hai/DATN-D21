# Hướng dẫn Kết nối WebSocket cho Frontend

## Tổng quan

Sau khi frontend kết nối đến `/bej3/ws`, cần **subscribe vào các destination** để nhận thông báo. Backend sẽ tự động push notification khi có đơn hàng mới hoặc cập nhật trạng thái.

## Bước 1: Kết nối WebSocket

```javascript
import SockJS from 'sockjs-client';
import { Client } from '@stomp/stompjs';

const baseUrl = 'http://localhost:8080/bej3'; // hoặc URL production
const wsUrl = `${baseUrl}/ws`;

// Tạo SockJS connection
const socket = new SockJS(wsUrl);

// Tạo STOMP client
const stompClient = new Client({
    webSocketFactory: () => socket,
    reconnectDelay: 5000,
    heartbeatIncoming: 4000,
    heartbeatOutgoing: 4000,
    onConnect: () => {
        console.log('✅ WebSocket connected');
        // Bước 2: Subscribe sau khi connect
        subscribeToNotifications();
    },
    onStompError: (frame) => {
        console.error('❌ STOMP error:', frame);
    },
    onWebSocketError: (event) => {
        console.error('❌ WebSocket error:', event);
    }
});

// Kết nối
stompClient.activate();
```

## Bước 2: Subscribe để nhận thông báo

Sau khi kết nối thành công, **bắt buộc phải subscribe** vào các destination sau:

### Cách 1: Topic-based (Khuyến nghị - Đơn giản nhất)

```javascript
function subscribeToNotifications() {
    // Lấy userId từ localStorage, context, hoặc API /auth/me
    const userId = getCurrentUserId(); // Ví dụ: "user-uuid-123"
    
    // Subscribe vào topic notifications của user
    const subscription = stompClient.subscribe(
        `/topic/notifications/${userId}`, 
        (message) => {
            const notification = JSON.parse(message.body);
            handleNotification(notification);
        }
    );
    
    console.log('✅ Subscribed to:', `/topic/notifications/${userId}`);
}

function handleNotification(notification) {
    console.log('📨 Received notification:', notification);
    
    // Xử lý notification
    switch(notification.type) {
        case 'ORDER_PLACED':
            // Đơn hàng mới được tạo
            showNotification(notification.title, notification.body);
            break;
        case 'ORDER_STATUS_UPDATE':
            // Trạng thái đơn hàng được cập nhật
            showNotification(notification.title, notification.body);
            updateOrderStatus(notification.metadata.orderId, notification.metadata.newStatus);
            break;
        default:
            showNotification(notification.title, notification.body);
    }
}
```

### Cách 2: User-specific Queue (Cần authentication)

Nếu bạn muốn dùng user-specific queue, cần đảm bảo WebSocket connection có authentication:

```javascript
// Kết nối với JWT token (nếu cần)
const token = localStorage.getItem('token'); // JWT token

stompClient.configure({
    connectHeaders: {
        Authorization: `Bearer ${token}`
    }
});

// Subscribe
stompClient.subscribe(
    `/user/${userId}/queue/notifications`,
    (message) => {
        const notification = JSON.parse(message.body);
        handleNotification(notification);
    }
);
```

**Lưu ý**: Với user-specific queue, userId phải khớp với principal name (phone number) trong JWT token.

### Cách 3: Admin/Staff - Subscribe broadcast

Nếu user là Admin, có thể subscribe vào topic broadcast:

```javascript
// Subscribe vào broadcast notifications (cho admin)
stompClient.subscribe(
    '/topic/notifications',
    (message) => {
        const notification = JSON.parse(message.body);
        handleNotification(notification);
    }
);
```

## Ví dụ hoàn chỉnh (React)

```javascript
import { useEffect, useRef } from 'react';
import SockJS from 'sockjs-client';
import { Client } from '@stomp/stompjs';

function useWebSocketNotifications(userId) {
    const stompClientRef = useRef(null);

    useEffect(() => {
        if (!userId) return;

        const baseUrl = process.env.REACT_APP_API_URL || 'http://localhost:8080/bej3';
        const wsUrl = `${baseUrl}/ws`;

        const socket = new SockJS(wsUrl);
        const client = new Client({
            webSocketFactory: () => socket,
            reconnectDelay: 5000,
            heartbeatIncoming: 4000,
            heartbeatOutgoing: 4000,
            onConnect: () => {
                console.log('✅ WebSocket connected');
                
                // Subscribe vào topic notifications
                const subscription = client.subscribe(
                    `/topic/notifications/${userId}`,
                    (message) => {
                        const notification = JSON.parse(message.body);
                        console.log('📨 Notification received:', notification);
                        
                        // Emit event hoặc update state
                        window.dispatchEvent(new CustomEvent('notification', {
                            detail: notification
                        }));
                    }
                );
                
                console.log('✅ Subscribed to notifications for user:', userId);
            },
            onStompError: (frame) => {
                console.error('❌ STOMP error:', frame);
            },
            onWebSocketError: (event) => {
                console.error('❌ WebSocket error:', event);
            }
        });

        client.activate();
        stompClientRef.current = client;

        // Cleanup
        return () => {
            if (stompClientRef.current) {
                stompClientRef.current.deactivate();
            }
        };
    }, [userId]);

    return stompClientRef.current;
}

// Sử dụng trong component
function NotificationComponent() {
    const { user } = useAuth(); // Lấy user từ context
    useWebSocketNotifications(user?.id);

    useEffect(() => {
        // Listen for notification events
        const handleNotification = (event) => {
            const notification = event.detail;
            // Hiển thị notification
            toast.success(notification.title, {
                description: notification.body
            });
        };

        window.addEventListener('notification', handleNotification);
        return () => window.removeEventListener('notification', handleNotification);
    }, []);

    return null;
}
```

## Ví dụ hoàn chỉnh (Vanilla JavaScript)

```javascript
class WebSocketNotificationService {
    constructor(userId, baseUrl = 'http://localhost:8080/bej3') {
        this.userId = userId;
        this.baseUrl = baseUrl;
        this.stompClient = null;
        this.subscriptions = [];
    }

    connect() {
        const wsUrl = `${this.baseUrl}/ws`;
        const socket = new SockJS(wsUrl);
        
        this.stompClient = Stomp.over(socket);
        this.stompClient.debug = () => {}; // Disable debug logs

        this.stompClient.connect({}, (frame) => {
            console.log('✅ WebSocket connected:', frame);
            this.subscribe();
        }, (error) => {
            console.error('❌ Connection error:', error);
            // Retry after 5 seconds
            setTimeout(() => this.connect(), 5000);
        });
    }

    subscribe() {
        if (!this.userId) {
            console.error('❌ UserId is required for subscription');
            return;
        }

        // Subscribe vào topic notifications
        const topic = `/topic/notifications/${this.userId}`;
        const subscription = this.stompClient.subscribe(topic, (message) => {
            const notification = JSON.parse(message.body);
            this.handleNotification(notification);
        });

        this.subscriptions.push(subscription);
        console.log('✅ Subscribed to:', topic);

        // Nếu là admin, subscribe thêm vào broadcast
        // this.subscribeToBroadcast();
    }

    subscribeToBroadcast() {
        const subscription = this.stompClient.subscribe(
            '/topic/notifications',
            (message) => {
                const notification = JSON.parse(message.body);
                this.handleNotification(notification);
            }
        );
        this.subscriptions.push(subscription);
        console.log('✅ Subscribed to broadcast notifications');
    }

    handleNotification(notification) {
        console.log('📨 Notification received:', notification);
        
        // Emit custom event
        window.dispatchEvent(new CustomEvent('websocket-notification', {
            detail: notification
        }));

        // Hoặc gọi callback nếu có
        if (this.onNotification) {
            this.onNotification(notification);
        }
    }

    disconnect() {
        if (this.stompClient) {
            // Unsubscribe tất cả
            this.subscriptions.forEach(sub => sub.unsubscribe());
            this.subscriptions = [];
            
            // Disconnect
            this.stompClient.disconnect();
            console.log('✅ WebSocket disconnected');
        }
    }
}

// Sử dụng
const notificationService = new WebSocketNotificationService('user-id-123');

// Set callback
notificationService.onNotification = (notification) => {
    console.log('New notification:', notification);
    // Hiển thị notification UI
    showNotificationToast(notification);
};

// Kết nối
notificationService.connect();

// Khi user logout hoặc component unmount
// notificationService.disconnect();
```

## Cấu trúc Notification Object

Khi nhận được notification, bạn sẽ nhận object có cấu trúc:

```typescript
interface NotificationPayload {
    id: string;                    // UUID của notification
    type: string;                   // ORDER_PLACED, ORDER_STATUS_UPDATE, ...
    title: string;                   // Tiêu đề thông báo
    body: string;                    // Nội dung thông báo
    timestamp: string;               // ISO 8601 timestamp
    metadata?: {                    // Metadata tùy chọn
        orderId?: string;
        orderType?: string;
        oldStatus?: string;
        newStatus?: string;
        userId?: string;
    };
}
```

## Checklist cho Frontend Developer

- [ ] ✅ Cài đặt thư viện: `sockjs-client` và `@stomp/stompjs` (hoặc `stompjs`)
- [ ] ✅ Kết nối đến `/bej3/ws` sau khi user login
- [ ] ✅ Lấy `userId` từ API `/auth/me` hoặc từ JWT token
- [ ] ✅ Subscribe vào `/topic/notifications/{userId}` sau khi connect thành công
- [ ] ✅ Xử lý notification khi nhận được (hiển thị toast, update UI, ...)
- [ ] ✅ Disconnect WebSocket khi user logout hoặc component unmount
- [ ] ✅ Handle reconnection nếu connection bị mất

## Lưu ý quan trọng

1. **Bắt buộc phải subscribe**: Chỉ kết nối WebSocket thôi chưa đủ, **phải subscribe** vào destination mới nhận được notification.

2. **UserId là bắt buộc**: Cần có `userId` để subscribe. Lấy từ:
   - API `/auth/me` (sau khi login)
   - JWT token (decode để lấy thông tin user)
   - LocalStorage/Context (nếu đã lưu)

3. **Topic-based vs User Queue**:
   - **Topic-based** (`/topic/notifications/{userId}`): Đơn giản, không cần authentication, khuyến nghị dùng
   - **User Queue** (`/user/{userId}/queue/notifications`): Cần authentication, userId phải khớp với principal name

4. **Reconnection**: Nên implement auto-reconnect nếu connection bị mất.

5. **Cleanup**: Luôn disconnect và unsubscribe khi component unmount hoặc user logout.

## Test nhanh

1. Mở browser console
2. Chạy code kết nối WebSocket
3. Tạo một đơn hàng mới (qua API hoặc UI)
4. Kiểm tra console xem có nhận được notification không

```javascript
// Test trong browser console
const socket = new SockJS('http://localhost:8080/bej3/ws');
const client = Stomp.over(socket);
client.debug = () => {};
client.connect({}, () => {
    client.subscribe('/topic/notifications/YOUR_USER_ID', (msg) => {
        console.log('📨 Notification:', JSON.parse(msg.body));
    });
});
```

## Troubleshooting

### Không nhận được notification?

1. ✅ Kiểm tra đã subscribe chưa?
2. ✅ UserId có đúng không?
3. ✅ WebSocket đã connect thành công chưa?
4. ✅ Kiểm tra backend logs xem có gửi notification không
5. ✅ Kiểm tra RabbitMQ Management UI (http://localhost:15672) xem có message trong queue không

### Connection failed?

1. ✅ Kiểm tra URL: `http://localhost:8080/bej3/ws` (có context-path `/bej3`)
2. ✅ Kiểm tra CORS configuration
3. ✅ Kiểm tra RabbitMQ đang chạy không
4. ✅ Kiểm tra STOMP plugin đã enable chưa

