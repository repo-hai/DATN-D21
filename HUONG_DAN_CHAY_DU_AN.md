# Hướng dẫn chạy dự án DATN-D21

## 1. Tổng quan repo

Repo hiện gồm 3 phần chính:

- `DATN_BE`: Backend `Spring Boot 3.4.3`, `Java 21`, `MySQL`, `RabbitMQ`
- `DATN_VUE-WEB`: Frontend web `Vue 3 + Vite`
- `DATN_Mobile`: Ứng dụng Android native `Kotlin + Jetpack Compose`

Khuyến nghị chạy theo thứ tự:

1. Chạy backend trước
2. Chạy web hoặc mobile sau
3. Kiểm tra kết nối API

## 2. Yêu cầu môi trường

### Backend

- `Java 21`
- `Maven 3.9+` hoặc dùng `mvnw`/`mvnw.cmd`
- `Docker Desktop` nếu muốn chạy bằng Docker
- `MySQL 8` và `RabbitMQ` nếu muốn chạy local không dùng Docker

### Web

- `Node.js 18+`
- `npm`

### Mobile

- `Android Studio`
- `Android SDK 36`
- JDK phù hợp với Android Studio/AGP
- Android Emulator hoặc thiết bị Android thật

## 3. Cấu trúc thư mục

```text
DATN-D21/
├── DATN_BE/
├── DATN_VUE-WEB/
├── DATN_Mobile/
└── HUONG_DAN_CHAY_DU_AN.md
```

## 4. Chạy backend `DATN_BE`

Backend dùng:

- API base: `http://localhost:8080/bej3`
- Context path: `/bej3`

Có 2 cách chạy:

1. Chạy bằng Docker
2. Chạy local bằng Maven

### 4.1. Chuẩn bị file `.env`

Tạo file `.env` trong thư mục `DATN_BE`.

Mẫu tối thiểu:

```env
DB_HOST=localhost
DB_PORT=3306
DB_NAME=new_bej_sp3
DB_USERNAME=root
DB_PASSWORD=root

RABBITMQ_HOST=localhost
RABBITMQ_PORT=5672
RABBITMQ_STOMP_PORT=61613
RABBITMQ_USERNAME=guest
RABBITMQ_PASSWORD=guest

FIREBASE_PROJECT_ID=datn-d21
FIREBASE_DATABASE_URL=https://datn-d21-default-rtdb.asia-southeast1.firebasedatabase.app/
FIREBASE_STORAGE_BUCKET=datn-d21.firebasestorage.app

JWT_SIGNER_KEY=thay_bang_secret_key_cua_ban

CORS_ALLOWED_ORIGINS=http://localhost:5173,http://localhost:3000

CLOUDINARY_CLOUD_NAME=your_cloud_name
CLOUDINARY_API_KEY=your_api_key
CLOUDINARY_API_SECRET=your_api_secret

VNPAY_PAY_URL=your_vnpay_pay_url
VNPAY_RETURN_URL=your_vnpay_return_url
VNPAY_IPN_URL=your_vnpay_ipn_url
VNPAY_TMN_CODE=your_vnpay_tmn_code
VNPAY_HASH_SECRET=your_vnpay_hash_secret

ZALOPAY_APP_ID=your_zalopay_app_id
ZALOPAY_KEY1=your_zalopay_key1
ZALOPAY_KEY2=your_zalopay_key2
ZALOPAY_CREATE_ORDER_URL=your_zalopay_create_order_url
ZALOPAY_CALLBACK_URL=your_zalopay_callback_url
ZALOPAY_RETURN_URL=your_zalopay_return_url
```

Lưu ý:

- Repo hiện không có sẵn `.env.example`, bạn cần tự tạo file `.env`
- Nếu chưa dùng Cloudinary, VNPay, ZaloPay, Firebase trong luồng test của bạn thì vẫn nên khai báo giá trị placeholder để app khởi động ổn định

### 4.2. Chuẩn bị file Firebase

Backend đang đọc file service account tại:

```text
DATN_BE/src/main/resources/datn-d21-firebase-adminsdk-fbsvc-d2772bc9ae.json
```

Bạn cần:

1. Vào Firebase Console
2. Tạo Service Account Key dạng JSON
3. Đặt file JSON vào đúng thư mục `DATN_BE/src/main/resources/`
4. Đổi đúng tên file như cấu hình trong `application.yaml`

Nếu không có file này, backend có thể lỗi khi khởi động phần Firebase.

### 4.3. Cách 1: chạy backend bằng Docker

Thư mục chạy lệnh:

```powershell
cd DATN_BE
```

Do `docker-compose.yml` đã cấu hình sẵn:

- MySQL container nội bộ chạy cổng `3306`
- MySQL xuất ra máy host cổng `3307`
- RabbitMQ UI: `http://localhost:15672`
- Backend API: `http://localhost:8080/bej3`

Lưu ý quan trọng:

- Container `app` dùng profile `docker`
- Profile này lấy MySQL theo hostname `mysql`, không phải `localhost`
- Vì vậy file `.env` chủ yếu vẫn cần cho các biến còn lại; riêng database khi chạy Docker đã có cấu hình riêng trong `application-docker.yaml`

Các bước chạy:

```powershell
cd DATN_BE
docker compose up -d
```

Xem log:

```powershell
docker compose logs -f
```

Dừng container:

```powershell
docker compose down
```

Dừng và xóa volume dữ liệu:

```powershell
docker compose down -v
```

Kiểm tra nhanh sau khi chạy:

```powershell
curl http://localhost:8080/bej3/home
```

### 4.4. Cách 2: chạy backend local bằng Maven

Trường hợp này bạn phải tự chạy:

- MySQL local
- RabbitMQ local

#### Bước 1: tạo database MySQL

```sql
CREATE DATABASE new_bej_sp3;
```

#### Bước 2: cập nhật `.env`

Nếu chạy local, nên dùng:

```env
DB_HOST=localhost
DB_PORT=3306
RABBITMQ_HOST=localhost
RABBITMQ_PORT=5672
RABBITMQ_STOMP_PORT=61613
```

#### Bước 3: chạy backend

Windows:

```powershell
cd d:\Desktop\DATN-D21\DATN-D21\DATN_BE
.\mvnw.cmd spring-boot:run
```

Hoặc nếu đã có Maven global:

```powershell
cd d:\Desktop\DATN-D21\DATN-D21\DATN_BE
mvn spring-boot:run
```

Build jar:

```powershell
cd d:\Desktop\DATN-D21\DATN-D21\DATN_BE
.\mvnw.cmd clean package
java -jar target\DATN_BE-0.0.1-SNAPSHOT.jar
```

### 4.5. Endpoint kiểm tra backend

- API gốc: `http://localhost:8080/bej3`
- Trang RabbitMQ: `http://localhost:15672`
- Tài khoản RabbitMQ mặc định: `guest / guest`

Có thể thử:

- `GET http://localhost:8080/bej3/home`
- `POST http://localhost:8080/bej3/auth/log-in`

## 5. Chạy web `DATN_VUE-WEB`

### 5.1. Lưu ý trước khi chạy

Frontend web hiện gọi backend chủ yếu bằng URL hardcode:

```text
http://localhost:8080/bej3
```

Vì vậy:

- Backend phải chạy ở đúng cổng `8080`
- Không nên đổi context path backend nếu chưa sửa lại frontend

Ngoài ra một phần WebSocket có hỗ trợ:

```env
VITE_API_BASE_URL=http://localhost:8080/bej3
```

Nhưng nhiều chỗ trong source vẫn đang hardcode URL, nên biến môi trường này chưa thay thế toàn bộ.

### 5.2. Cài dependency và chạy

```powershell
cd d:\Desktop\DATN-D21\DATN-D21\DATN_VUE-WEB
npm install
npm run dev
```

Sau khi chạy, mở:

```text
http://localhost:5173
```

### 5.3. Build production

```powershell
cd d:\Desktop\DATN-D21\DATN-D21\DATN_VUE-WEB
npm run build
```

### 5.4. Kiểm tra kết nối web với backend

Khi backend đã chạy, mở web và kiểm tra:

- Trang chủ có tải danh sách sản phẩm
- Đăng nhập gọi được API `/bej3/auth/log-in`
- Giỏ hàng và đơn hàng gọi được API `/bej3/cart/...`

Nếu web không gọi được API:

- kiểm tra backend đã chạy chưa
- kiểm tra backend đúng `http://localhost:8080`
- kiểm tra `CORS_ALLOWED_ORIGINS` có chứa `http://localhost:5173`

## 6. Chạy mobile `DATN_Mobile`

### 6.1. Cấu hình API của mobile

Mobile đang cấu hình tại file:

```text
DATN_Mobile/app/src/main/java/com/example/datn_mobile/config/ApiConfig.kt
```

Giá trị hiện tại:

```kotlin
const val BASE_URL = "http://10.0.2.2:8080/"
```

Ý nghĩa:

- `10.0.2.2` dùng cho Android Emulator để trỏ tới `localhost` của máy tính
- Các API trong app đã tự thêm prefix `/bej3/...`

### 6.2. Trường hợp chạy bằng Android Emulator

Giữ nguyên:

```kotlin
const val BASE_URL = "http://10.0.2.2:8080/"
```

Điều kiện:

- Backend phải chạy trên máy tính ở cổng `8080`
- Emulator phải truy cập được host machine

### 6.3. Trường hợp chạy trên thiết bị thật

Bạn phải sửa `BASE_URL` sang IP nội bộ của máy tính cùng mạng Wi-Fi, ví dụ:

```kotlin
const val BASE_URL = "http://192.168.1.10:8080/"
```

Lưu ý:

- Điện thoại và máy tính phải cùng mạng
- Windows Firewall có thể chặn truy cập cổng `8080`
- Backend phải bind `0.0.0.0`, và hiện tại backend đã cấu hình như vậy

### 6.4. Cấu hình cleartext traffic

App đang cho phép HTTP không mã hóa tại:

```text
DATN_Mobile/app/src/main/res/xml/network_security_config.xml
```

Trong file này hiện đã whitelist:

- `10.0.2.2`
- `localhost`
- một vài IP nội bộ cụ thể

Nếu bạn đổi sang IP mới trên mạng LAN mà mobile không gọi được API, hãy thêm IP đó vào `network_security_config.xml`.

### 6.5. Chạy app Android

Các bước:

1. Mở thư mục `DATN_Mobile` bằng Android Studio
2. Chờ Gradle sync xong
3. Kiểm tra emulator hoặc cắm điện thoại
4. Chạy app bằng nút `Run`

Nếu muốn build bằng command line:

```powershell
cd DATN_Mobile
.\gradlew.bat assembleDebug
```

## 7. Thứ tự chạy khuyến nghị

### Cách chạy đầy đủ để test toàn hệ thống

1. Chạy backend `DATN_BE`
2. Kiểm tra `http://localhost:8080/bej3/home`
3. Chạy web `DATN_VUE-WEB`
4. Mở `http://localhost:5173`
5. Nếu cần test mobile thì mở `DATN_Mobile` trong Android Studio và chạy emulator

## 8. Các lỗi thường gặp

### Lỗi backend không lên do thiếu biến môi trường

Nguyên nhân:

- Chưa tạo `.env`
- Thiếu các biến Firebase, JWT, Cloudinary, VNPay hoặc ZaloPay

Cách xử lý:

- Tạo `.env` trong `DATN_BE`
- Khai báo đầy đủ các biến cần thiết, có thể dùng placeholder cho phần chưa test

### Lỗi backend không lên do thiếu file Firebase JSON

Nguyên nhân:

- Chưa đặt file service account vào `src/main/resources`
- Tên file không khớp `application.yaml`

Cách xử lý:

- Đặt đúng file JSON
- Hoặc sửa lại đường dẫn/tên file trong cấu hình

### Lỗi web gọi API thất bại

Nguyên nhân:

- Backend chưa chạy
- Sai cổng backend
- Lỗi CORS

Cách xử lý:

- kiểm tra `http://localhost:8080/bej3/home`
- kiểm tra `.env` backend có `CORS_ALLOWED_ORIGINS=http://localhost:5173,http://localhost:3000`

### Lỗi mobile không gọi được API

Nguyên nhân:

- Đang dùng sai `BASE_URL`
- Chưa thêm IP LAN vào `network_security_config.xml`
- Firewall chặn cổng `8080`

Cách xử lý:

- Emulator dùng `10.0.2.2`
- Thiết bị thật dùng IP LAN của máy tính
- Mở firewall nếu cần

### Lỗi Docker chạy nhưng web/mobile không lấy được dữ liệu

Nguyên nhân thường gặp:

- Backend container chưa khởi động xong
- Firebase hoặc biến môi trường làm app lỗi ngầm

Cách xử lý:

```powershell
cd DATN_BE
docker compose logs -f app
```

## 9. Lệnh nhanh

### Backend bằng Docker

```powershell
cd d:\Desktop\DATN-D21\DATN-D21\DATN_BE
docker compose up -d
```

### Backend local

```powershell
cd d:\Desktop\DATN-D21\DATN-D21\DATN_BE
.\mvnw.cmd spring-boot:run
```

### Web

```powershell
cd DATN_VUE-WEB
npm install
npm run dev
```

### Mobile

```powershell
cd DATN_Mobile
.\gradlew.bat assembleDebug
```

## 10. Kết luận

Nếu chỉ cần chạy nhanh để test web:

1. Chạy backend bằng Docker trong `DATN_BE`
2. Chạy web bằng `npm run dev` trong `DATN_VUE-WEB`

Nếu cần test mobile:

1. Đảm bảo backend đang chạy ở cổng `8080`
2. Kiểm tra `ApiConfig.kt`
3. Chạy app bằng Android Studio hoặc Gradle
