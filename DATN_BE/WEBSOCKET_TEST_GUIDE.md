# Hướng dẫn Test WebSocket Notifications

## Tổng quan

Sau khi sửa logic, hệ thống sẽ:
1. **Tạo đơn hàng mới** (`createNewOrder` hoặc `placeOrder`) → Fire `OrderCreatedEvent`
2. **Cập nhật trạng thái đơn hàng** (`updateOrderStatus`) → Fire `OrderStatusUpdateEvent`
3. **NotificationEventListener** xử lý các event và gửi thông báo cho:
   - **User** sở hữu đơn hàng
   - **Admin** và **Staff** (tất cả users có role ADMIN)

Thông báo được gửi qua:
- **WebSocket** (RabbitMQ STOMP broker)
- **Firebase Cloud Messaging** (nếu user có FCM token)
- **Database** (lưu vào bảng Notification)

## Cấu hình WebSocket

### Backend Configuration

WebSocket đã được cấu hình với:
- **Endpoint**: `/bej3/ws` (với SockJS)
- **User Destination Prefix**: `/user`
- **Broker**: RabbitMQ STOMP (port 61613)
- **Topics**: `/topic/*`
- **Queues**: `/queue/*`

### ⚠️ QUAN TRỌNG: Client PHẢI Subscribe

Sau khi kết nối WebSocket thành công, **BẮT BUỘC phải subscribe** vào destination để nhận notification. Chỉ kết nối thôi chưa đủ!

### Client Subscription

Client cần subscribe vào một trong các destination sau:

#### Option 1: User-specific Queue (Khuyến nghị)
```javascript
// Subscribe với userId
stompClient.subscribe(`/user/${userId}/queue/notifications`, (message) => {
    const notification = JSON.parse(message.body);
    console.log('Received notification:', notification);
});
```

**Lưu ý**: Với RabbitMQ STOMP broker, userId cần khớp với principal name trong WebSocket session (thường là phone number).

#### Option 2: Topic-based (Fallback)
```javascript
// Subscribe với userId
stompClient.subscribe(`/topic/notifications/${userId}`, (message) => {
    const notification = JSON.parse(message.body);
    console.log('Received notification:', notification);
});
```

#### Option 3: Admin/Staff - Subscribe tất cả notifications
```javascript
// Admin/Staff có thể subscribe vào topic chung
stompClient.subscribe('/topic/notifications', (message) => {
    const notification = JSON.parse(message.body);
    console.log('Received broadcast notification:', notification);
});
```

## Cách Test

### 1. Chuẩn bị môi trường

#### Start Services
```bash
# Start MySQL và RabbitMQ
docker-compose up -d mysql rabbitmq

# Start Spring Boot application
mvn spring-boot:run
# hoặc
./mvnw spring-boot:run
```

#### Kiểm tra RabbitMQ
- Truy cập: http://localhost:15672
- Username: `guest`
- Password: `guest`
- Kiểm tra STOMP plugin đã được enable

### 2. Test WebSocket Connection

#### Sử dụng WebSocket Client (JavaScript)

Tạo file `test-websocket.html`:

```html
<!DOCTYPE html>
<html>
<head>
    <title>WebSocket Notification Test</title>
    <script src="https://cdn.jsdelivr.net/npm/sockjs-client@1/dist/sockjs.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/stompjs@2.3.3/lib/stomp.min.js"></script>
</head>
<body>
    <h1>WebSocket Notification Test</h1>
    <div>
        <label>User ID: <input type="text" id="userId" value="your-user-id-here"></label>
        <button onclick="connect()">Connect</button>
        <button onclick="disconnect()">Disconnect</button>
    </div>
    <div id="messages"></div>

    <script>
        let stompClient = null;
        const baseUrl = 'http://localhost:8080/bej3';
        const wsUrl = `${baseUrl}/ws`;

        function connect() {
            const userId = document.getElementById('userId').value;
            const socket = new SockJS(wsUrl);
            stompClient = Stomp.over(socket);
            
            // Disable debug logs
            stompClient.debug = () => {};

            stompClient.connect({}, function(frame) {
                console.log('Connected: ' + frame);
                
                // Subscribe to user-specific queue
                const userQueue = `/user/${userId}/queue/notifications`;
                stompClient.subscribe(userQueue, function(message) {
                    const notification = JSON.parse(message.body);
                    displayMessage('User Queue', notification);
                });
                
                // Subscribe to topic-based (fallback)
                const topic = `/topic/notifications/${userId}`;
                stompClient.subscribe(topic, function(message) {
                    const notification = JSON.parse(message.body);
                    displayMessage('Topic', notification);
                });
                
                // Subscribe to broadcast (for admin)
                stompClient.subscribe('/topic/notifications', function(message) {
                    const notification = JSON.parse(message.body);
                    displayMessage('Broadcast', notification);
                });
                
                displayMessage('System', { title: 'Connected', body: 'WebSocket connected successfully' });
            }, function(error) {
                console.error('Connection error:', error);
                displayMessage('Error', { title: 'Connection Failed', body: error.toString() });
            });
        }

        function disconnect() {
            if (stompClient !== null) {
                stompClient.disconnect();
            }
            displayMessage('System', { title: 'Disconnected', body: 'WebSocket disconnected' });
        }

        function displayMessage(source, notification) {
            const messagesDiv = document.getElementById('messages');
            const messageDiv = document.createElement('div');
            messageDiv.style.border = '1px solid #ccc';
            messageDiv.style.padding = '10px';
            messageDiv.style.margin = '10px 0';
            messageDiv.innerHTML = `
                <strong>[${source}]</strong><br>
                <strong>Type:</strong> ${notification.type || 'N/A'}<br>
                <strong>Title:</strong> ${notification.title || 'N/A'}<br>
                <strong>Body:</strong> ${notification.body || 'N/A'}<br>
                <strong>Time:</strong> ${new Date(notification.timestamp || Date.now()).toLocaleString()}<br>
                <pre>${JSON.stringify(notification, null, 2)}</pre>
            `;
            messagesDiv.appendChild(messageDiv);
        }
    </script>
</body>
</html>
```

### 3. Test Tạo Đơn Hàng Mới

#### API Request
```bash
# Tạo đơn hàng mới
curl -X POST http://localhost:8080/bej3/orders/create \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "userId": "user-id-here",
    "items": [
      {
        "productAttId": "product-attribute-id",
        "quantity": 1
      }
    ],
    "type": 0,
    "phoneNumber": "0123456789",
    "address": "123 Test Street"
  }'
```

#### Kết quả mong đợi:
1. Đơn hàng được tạo trong database
2. `OrderCreatedEvent` được fire
3. `NotificationEventListener.handleOrderCreatedEvent()` được gọi
4. Thông báo được gửi đến:
   - User sở hữu đơn hàng
   - Tất cả Admin users
5. WebSocket client nhận được notification với:
   - `type`: `ORDER_PLACED`
   - `title`: "Đơn hàng đã được tạo" (cho user) hoặc "Đơn hàng mới" (cho admin)
   - `body`: Mô tả đơn hàng

### 4. Test Cập Nhật Trạng Thái Đơn Hàng

#### API Request
```bash
# Cập nhật trạng thái đơn hàng
curl -X PUT http://localhost:8080/bej3/orders/{orderId}/status \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "status": 1,
    "note": "Đơn hàng đã được xác nhận"
  }'
```

#### Kết quả mong đợi:
1. Trạng thái đơn hàng được cập nhật
2. `OrderStatusUpdateEvent` được fire
3. `NotificationEventListener.handleOrderStatusUpdateEvent()` được gọi
4. Thông báo được gửi đến:
   - User sở hữu đơn hàng
   - Tất cả Admin users
5. WebSocket client nhận được notification với:
   - `type`: `ORDER_STATUS_UPDATE`
   - `title`: "Cập nhật đơn hàng"
   - `body`: Mô tả cập nhật trạng thái

### 5. Kiểm tra Logs

#### Backend Logs
Kiểm tra logs trong console để xem:
```
📦 Handling OrderCreatedEvent - Order: {orderId}, User: {userId}, Type: {type}
✅ Order created notification sent to user: {userId}
✅ Order created notification sent to {count} admin/staff users
✅ Notification sent via WebSocket to user: {userId} (both user queue and topic)
```

#### RabbitMQ Management UI
1. Truy cập http://localhost:15672
2. Vào tab **Queues**
3. Kiểm tra các queue được tạo:
   - `/queue/notifications` (user-specific)
   - `/topic/notifications` (broadcast)
4. Vào tab **Exchanges**
5. Kiểm tra messages được publish

### 6. Debug WebSocket Issues

#### Vấn đề: Không nhận được notification

**Kiểm tra:**
1. WebSocket connection đã được thiết lập?
   ```javascript
   console.log('STOMP Client connected:', stompClient.connected);
   ```

2. Subscription đã được tạo?
   - Kiểm tra trong RabbitMQ Management UI → Queues
   - Queue name phải khớp với subscription

3. User ID có đúng không?
   - User ID phải là UUID string từ database
   - Với user-specific queue, userId cần khớp với principal name (phone number) nếu có authentication

4. Kiểm tra logs backend:
   ```
   ✅ Notification sent via WebSocket to user: {userId}
   ```
   Nếu có lỗi, sẽ thấy:
   ```
   ❌ Failed to send notification via WebSocket to user {userId}: {error}
   ```

#### Giải pháp:
- **Option 1**: Sử dụng topic-based subscription (`/topic/notifications/{userId}`) thay vì user queue
- **Option 2**: Đảm bảo WebSocket connection có authentication và principal name khớp với userId
- **Option 3**: Kiểm tra RabbitMQ STOMP plugin đã được enable

### 7. Test với Postman/Thunder Client

#### WebSocket Connection trong Postman
1. Tạo WebSocket request: `ws://localhost:8080/bej3/ws`
2. Connect
3. Subscribe message:
   ```json
   ["SUBSCRIBE", "id:sub-0", "destination:/topic/notifications/{userId}"]
   ```
4. Gửi request tạo/cập nhật đơn hàng
5. Kiểm tra message nhận được

## Notification Payload Structure

```json
{
  "id": "uuid-string",
  "type": "ORDER_PLACED" | "ORDER_STATUS_UPDATE" | ...,
  "title": "Tiêu đề thông báo",
  "body": "Nội dung thông báo",
  "timestamp": "2024-01-01T00:00:00Z",
  "metadata": {
    "orderId": "order-id",
    "orderType": "0",
    "oldStatus": "0",
    "newStatus": "1"
  }
}
```

## Lưu ý quan trọng

1. **User ID vs Phone Number**: 
   - Backend sử dụng User ID (UUID) để gửi notification
   - Với user-specific queue (`/user/{userId}/queue/notifications`), userId cần khớp với principal name trong WebSocket session
   - Nếu không khớp, sử dụng topic-based subscription (`/topic/notifications/{userId}`)

2. **Admin/Staff Notifications**:
   - Tất cả users có role `ADMIN` sẽ nhận được thông báo về đơn hàng mới và cập nhật trạng thái
   - Admin có thể subscribe vào `/topic/notifications` để nhận broadcast notifications

3. **RabbitMQ Configuration**:
   - Đảm bảo STOMP plugin đã được enable trong RabbitMQ
   - Port STOMP: 61613 (default)
   - Port WebSocket: 15674 (default)

4. **CORS Configuration**:
   - WebSocket endpoint (`/ws/**`) đã được cấu hình trong SecurityConfig là public
   - Không cần authentication để connect WebSocket (nhưng có thể cần để identify user)

## Troubleshooting

### Lỗi: "Failed to send notification via WebSocket"
- Kiểm tra RabbitMQ đang chạy
- Kiểm tra STOMP port (61613) có accessible không
- Kiểm tra credentials trong `application.yaml`

### Lỗi: "User not found"
- Đảm bảo userId tồn tại trong database
- Kiểm tra UserRepository.findDistinctByRoles_NameIn() trả về đúng kết quả

### Lỗi: WebSocket connection failed
- Kiểm tra endpoint: `/bej3/ws` (có context-path)
- Kiểm tra CORS configuration
- Kiểm tra firewall/network

