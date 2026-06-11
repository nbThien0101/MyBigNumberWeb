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

Dự án này sử dụng kiến trúc Monorepo, chứa cả mã nguồn C++ và Java trong cùng một nơi. Quá trình triển khai được tối ưu hóa bằng **Docker Multi-stage Build** để tự động biên dịch chéo và tạo ra môi trường chạy gọn nhẹ.

## 🛠️ Yêu cầu hệ thống

Bạn có thể chạy dự án này theo 2 cách, yêu cầu cấu hình sẽ khác nhau:
- **Cách 1 (Docker - Khuyên dùng):** Chỉ cần cài đặt [Docker](https://www.docker.com/).
- **Cách 2 (Chạy trực tiếp):** Cần cài đặt **Java 17+** và trình biên dịch C++ (**g++**).

---

## 🚀 Cách chạy dự án

### Cách 1: Sử dụng Docker (Khuyên dùng)

Đây là cách đơn giản nhất, Docker sẽ tự động biên dịch C++, build Spring Boot và thiết lập môi trường hoàn chỉnh.

```bash
# 1. Build Docker image
docker build -t mybignumber-web .

# 2. Chạy container ở port 8080
docker run -d -p 8080:8080 --name mybignumber mybignumber-web

# 3. Mở trình duyệt và truy cập: http://localhost:8080
```

Nếu bạn muốn lấy file `history.log` từ bên trong container ra ngoài để kiểm tra:
```bash
docker cp mybignumber:/app/history.log ./history.log
cat history.log
```

### Cách 2: Chạy trực tiếp (Local Development)

Nếu bạn muốn phát triển và chạy trực tiếp trên máy của mình (yêu cầu máy có Java và g++):

```bash
# 1. Biên dịch C++ Core
cd cpp_src
g++ -std=c++11 -O2 -static -o mybignumber_core main.cpp src/MyBigNumber.cpp

# 2. Copy file thực thi ra thư mục gốc của project web
cp mybignumber_core ../
cd ..

# 3. Chạy Spring Boot
./mvnw spring-boot:run

# 4. Mở trình duyệt và truy cập: http://localhost:8080
```

---

## 📁 Cấu trúc thư mục

```text
MyBigNumberWeb/
├── cpp_src/                    # Mã nguồn C++ (Logic cốt lõi)
│   ├── main.cpp                
│   └── src/                    
├── src/main/                   # Mã nguồn Java Spring Boot
│   ├── java/com/example/mybignumber/
│   │   ├── MyBigNumberWebApplication.java    
│   │   └── controller/
│   │       └── BigNumberController.java      
│   └── resources/
│       ├── application.properties            
│       └── templates/
│           └── index.html                    
├── Dockerfile                  # Cấu hình Docker multi-stage build
├── .dockerignore               # Loại trừ file build docker
├── .gitignore                  
├── mvnw                        # Maven Wrapper script
├── pom.xml                     # Maven dependencies
└── README.md
```

## 🎨 Giao diện

- **Minimalist** — Giao diện trắng đen tối giản, tập trung vào nội dung.
- **Two-column layout** — Form nhập liệu bên trái, kết quả tính toán bên phải.
- **Step timeline** — Hiển thị chi tiết từng bước tính toán với các huy hiệu đánh số thứ tự rõ ràng.
- **Responsive** — Tự động căn chỉnh xếp dọc khi truy cập bằng thiết bị di động.

## 🧪 Test cases gợi ý

| Số 1 | Số 2 | Kết quả | Đặc điểm |
|------|------|---------|-----------|
| `123` | `456` | `579` | Cộng cơ bản |
| `999` | `1` | `1000` | Nhớ liên tục |
| `99999999999999999999` | `1` | `100000000000000000000` | Số cực lớn |
| `12345` | `0` | `12345` | Cộng với 0 |
| `5000` | `5000` | `10000` | Hai số bằng nhau |

## ⚠️ Lưu ý

- File thực thi `mybignumber_core` được tạo ra trong quá trình biên dịch không nên commit vào Git (đã được cấu hình trong `.gitignore`).
- Khi chạy trên các môi trường đám mây (Render, Railway,...), hãy sử dụng Docker để tránh lỗi khác biệt hệ điều hành (macOS vs Linux) và thiếu thư viện (glibc).
- Chi tiết hơn về kiến trúc và cách giải quyết lỗi, vui lòng xem [REPORT.md](./REPORT.md).

## 📄 License

MIT License
