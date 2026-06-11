# MyBigNumberWeb 🧮

Ứng dụng web cho phép cộng hai số nguyên lớn (Big Number Addition) với giao diện trực quan hiển thị từng bước tính toán — giống cách học sinh tiểu học cộng tay trên giấy.

> **Đặc biệt**: Logic tính toán lõi được viết bằng **C++**, không phải Java. Spring Boot chỉ đóng vai trò gọi file thực thi C++ qua `ProcessBuilder` và hiển thị kết quả.

## 🔗 Liên kết

| Thành phần | Repository |
|-----------|------------|
| **C++ Core Engine** (Task 1) | [github.com/nbThien0101/MyBigNumber](https://github.com/nbThien0101/MyBigNumber) |
| **Spring Boot Web** (Task 2) | Repo hiện tại |

## ⚙️ Cơ chế hoạt động

```
Người dùng                  Spring Boot                   C++ Engine
    │                           │                              │
    │  Nhập num1, num2          │                              │
    │ ─────────────────────────>│                              │
    │                           │  ProcessBuilder              │
    │                           │  ./mybignumber_core n1 n2    │
    │                           │ ────────────────────────────>│
    │                           │                              │ Tính toán
    │                           │                              │ Ghi history.log
    │                           │         exit code 0          │
    │                           │ <────────────────────────────│
    │                           │                              │
    │                           │  Đọc & parse history.log     │
    │                           │  Tách: steps + result        │
    │  Hiển thị kết quả         │                              │
    │ <─────────────────────────│                              │
```

## 🛠️ Yêu cầu hệ thống

- **Java 17+** — Kiểm tra: `java -version`
- **File thực thi C++** — `mybignumber_core` đặt tại thư mục gốc của project

## 🚀 Cách chạy

### 1. Biên dịch C++ Core (nếu chưa có file `mybignumber_core`)

```bash
# Vào thư mục Task 1
cd ../MyBigNumber

# Biên dịch
g++ -o mybignumber_core main.cpp src/MyBigNumber.cpp

# Copy file thực thi sang project web
cp mybignumber_core ../MyBigNumberWeb/
```

### 2. Khởi động Spring Boot

```bash
cd MyBigNumberWeb

# Cấp quyền thực thi cho mybignumber_core (nếu cần)
chmod +x mybignumber_core

# Chạy ứng dụng
./mvnw spring-boot:run
```

### 3. Mở trình duyệt

Truy cập: **http://localhost:8080**

## 📁 Cấu trúc thư mục

```
MyBigNumberWeb/
├── .mvn/wrapper/               # Maven Wrapper
├── src/main/
│   ├── java/com/example/mybignumber/
│   │   ├── MyBigNumberWebApplication.java    # Entry point
│   │   └── controller/
│   │       └── BigNumberController.java      # Xử lý request, gọi C++, parse log
│   └── resources/
│       ├── application.properties            # Cấu hình (port, đường dẫn C++)
│       └── templates/
│           └── index.html                    # Giao diện Thymeleaf + Bootstrap 5
├── mvnw                        # Maven Wrapper script
├── mybignumber_core            # File thực thi C++ (không commit vào git)
├── pom.xml                     # Maven dependencies
└── README.md
```

## 🎨 Giao diện

- **Dark glassmorphism** — nền gradient tối với hiệu ứng kính mờ
- **Animated background** — các orb màu gradient chuyển động
- **Two-column layout** — form nhập trái, kết quả phải
- **Step timeline** — hiển thị từng bước tính toán với badge đánh số
- **Responsive** — tự động xếp dọc trên màn hình nhỏ

## 🧪 Test cases gợi ý

| Số 1 | Số 2 | Kết quả | Đặc điểm |
|------|------|---------|-----------|
| `123` | `456` | `579` | Cộng cơ bản |
| `999` | `1` | `1000` | Nhớ liên tục |
| `99999999999999999999` | `1` | `100000000000000000000` | Số cực lớn |
| `12345` | `0` | `12345` | Cộng với 0 |
| `5000` | `5000` | `10000` | Hai số bằng nhau |

## ⚠️ Lưu ý

- File `mybignumber_core` là binary, **không nên commit vào Git**. Hãy thêm vào `.gitignore`.
- Mỗi lần biên dịch lại C++ source, nhớ copy file mới sang thư mục `MyBigNumberWeb/`.
- Có thể thay đổi đường dẫn file thực thi trong `application.properties`:
  ```properties
  app.core.path=./mybignumber_core
  ```

## 📄 License

MIT License
