# Mẫu Báo Cáo Kết Quả Bài Tập Code Game

## 1. Thông Tin Nhóm

**Tên Dự Án:** FLOWTASK

**Link Dự Án:** [https://github.com/vananh-bot/app_java_btck-](#)

**Thành Viên Nhóm:**
- Đỗ Thị Vân Anh 
- Trần Ngọc Mai
- Phạm Hoàng Uyên
- Hà Hoài Phương



### Mô hình làm việc

Team hoạt động theo mô hình Scrum, sử dụng Linear để quản lý công việc. Các công việc được keep track đầy đủ trên Linear.
- Link linear: [https://linear.app/hoang-uyen/project/btck-java-9f40d8e5acd6/overview](https://linear.app/hoang-uyen/project/btck-java-9f40d8e5acd6/overview)

Mỗi tuần, team sẽ ngồi lại để review công việc đã làm, cùng nhau giải quyết vấn đề và đề xuất giải pháp cho tuần tiếp theo. Sau đó sẽ có buổi demo cho mentor để nhận phản hồi và hướng dẫn.
### Version Control Strategy


Team hoạt động theo Gitflow để quản lý code. Mỗi thành viên sẽ tạo branch từ `develop` để làm việc, các branch đặt theo format `feature/ten-chuc-nang`, sau khi hoàn thành sẽ tạo Pull Request để review code và merge vào develop
- Các nhánh chính:
  - `main`: Chứa code ổn định, đã qua kiểm tra và test kỹ lưỡng
  - `develop`: Chứa code mới nhất, đã qua review và test
  - `feature/`: Các nhánh chứa code đang phát triển, short-live, sau khi hoàn thành sẽ merge vào `develop`. 

![alt text](Image/image.png)

Sau mỗi tuần, team sẽ merge `develop` vào `main` để release phiên bản mới.



## 2. Giới Thiệu Dự Án

- **FlowTask** là một ứng dụng desktop hỗ trợ quản lý dự án và theo dõi tiến độ công việc một cách hiệu quả, được thiết kế dành cho cá nhân và nhóm làm việc
- Ứng dụng cho phép người dùng tạo và quản lý nhiều **project** khác nhau, mỗi project đại diện cho một mục tiêu hoặc công việc cụ thể. Thành viên trong project có thể mời thêm thành viên khác qua email. 
- Trong mỗi **project**, các thành viên có thể tạo **task** với các thông tin như *tiêu đề, mô tả, deadline, trạng thái (chưa làm, đang thực hiện, hoàn thành), và mức độ ưu tiên*. Điều này giúp theo dõi tiến độ công việc một cách rõ ràng và có hệ thống.
- FlowTask áp dụng *Kanban* để trực quan hơn về quản lý công việc theo tiến trình. Với việc chia thành nhiều cột (TODO, IN PROGRESS, DONE) trong đó các task được biểu diễn dưới dạng một thẻ. Người dùng có thể theo dõi trạng thái công việc, kéo thả task giữa các cột để cập nhật tiến độ, đồng thời kết hợp độ ưu tiên và deadline để sắp xếp và kiểm soát công việc hiệu quả hơn.
- FlowTask cũng tích hợp hệ thống **thông báo** nhằm hỗ trợ người dùng không bỏ lỡ các mốc quan trọng: Tự động gửi thông báo khi **deadline của task còn dưới 24 giờ**, Thông báo khi có **bình luận (comment) mới trong task**
- Ngoài ra, ứng dụng có thể cung cấp **dashboard** tổng quan, giúp người dùng nhanh chóng nắm bắt tình trạng các project và task (số lượng task hoàn thành, đang thực hiện, quá hạn, v.v.).

## 3. Các Chức Năng Chính

### (1) Quản lý người dùng 
- Đăng nhập, đăng kí 
- Quản lý phiên bản làm việc (session): ghi nhớ tạm thời thông tin người dùng app, để hoạt động đúng với từng người

### (2) Quản lý Project
- Tạo, chỉnh sửa project
- Xem danh sách project
- Quản lý thành viên trong project
### (3) Quản lý Task
- Tạo, cập nhật task
- Gán deadline, mức độ ưu tiên, trạng thái
- Tạo, cập nhật và xóa subtask
- Theo dõi tiến độ công việc
### (4) Hệ thống Notification
- Thông báo khi task sắp đến deadline (< 24h)
- Thông báo khi có comment mới
- Thông báo khi có thành viên mới tham gia project 
### (5) Comment & tương tác
- Thêm và xem comment trong task
### (6) Mời thành viên qua Email
- Gửi lời mời tham gia project qua email
- Quản lý trạng thái lời mời
### (7) Dashboard tổng quan
- Hiển thị thống kê task (đang làm, hoàn thành, quá hạn)
- Giúp theo dõi tiến độ nhanh chóng
### (8) Tác vụ nền 
- Tự động kiểm tra deadline bằng Scheduler
- Kích hoạt notification

## 4. Công nghệ

### 4.1. Công Nghệ Sử Dụng
- Ngôn ngữ chính: **Java**
- Giao diện (UI): **JavaFX + FXML + CSS**
   - JavaFX dùng để xây dựng giao diện desktop
   - FXML hỗ trợ tách giao diện và xử lý logic
   - CSS dùng để thiết kế và tùy chỉnh UI
- Cơ sở dữ liệu: **MySQL, JDBC**
  - MySQL dùng để lưu trữ dữ liệu
  - JDBC dùng để kết nối và thao tác với database
- Kiến trúc & thiết kế: **MVC** (Model – View – Controller)

### 4.2 Cấu trúc dự án

```
- Controller
- Service
- DAO
- Model
- DTO
- Enum
- Database
- Utils
- Test
```

**Diễn giải:**
- **Controller**:
Chứa các lớp xử lý giao diện và tương tác người dùng (Login, Task, Project, Dashboard…).
Nhận input từ UI và gọi Service.
- **Service**
Chứa logic nghiệp vụ chính của hệ thống (TaskService, ProjectService, NotificationService…).
Xử lý dữ liệu trước khi làm việc với database.
- **DAO (Data Access Object)**
Thực hiện các thao tác với database (CRUD).
Mỗi entity có DAO và Interface riêng.
- **Model**
Đại diện cho các đối tượng dữ liệu chính (User, Task, Project, Comment…).
- **DTO (Data Transfer Object)**
Dùng để truyền dữ liệu giữa các tầng (DashboardDTO, NotificationDTO…).
- **Enum**
Định nghĩa các giá trị cố định như trạng thái task, quyền user, loại thông báo…
- **Database**
Chứa cấu hình kết nối database (JDBCUtil).
- **Utils**
Các lớp hỗ trợ như:
xử lý thời gian (TimeUtil)
điều hướng giao diện (SceneNavigator)
quản lý session (UserSession)
- **Test**
Chứa các class dùng để kiểm thử ứng dụng.





## 5. Ảnh và Video Demo

**Ảnh Demo:**
![alt text](Image/1.png)
![alt text](Image/2.png)
![alt text](Image/3.png)
![alt text](Image/4.png)
![alt text](Image/5.png)
![alt text](Image/6.png)
![alt text](Image/7.png)
![alt text](Image/8.png)
![alt text](Image/9.png)
![alt text](Image/10.png)
![alt text](Image/11.png)
![alt text](Image/12.png)
![alt text](Image/13.png)
![alt text](Image/14.png)
![alt text](Image/15.png)
![alt text](Image/16.png)
![alt text](Image/17.png)
![alt text](Image/18.png)
![alt text](Image/19.png)
![alt text](Image/20.png)
![alt text](Image/21.png)
![alt text](Image/22.png)

**Video Demo:**
[[Video Link](#)](https://drive.google.com/file/d/1ftiefDPb-OCjbkOiZTeYA7Fh8SHm6NYL/view?usp=sharing)






## 6. Các Vấn Đề Gặp Phải

### Vấn Đề 1: Hiệu năng ứng dụng giảm do gọi database quá nhiều lần
Trong quá trình phát triển FlowTask, ứng dụng gặp tình trạng bị lag và phản hồi chậm khi chuyển màn hình hoặc tải dữ liệu dashboard, project và task.

Nguyên nhân chính là do hệ thống thực hiện quá nhiều truy vấn database lặp lại, đặc biệt ở các chức năng:
- Dashboard
- Danh sách project
- Danh sách task
Việc liên tục gọi database khiến:
- Tăng thời gian tải dữ liệu
- UI bị giật/khựng
- Tăng số lượng query không cần thiết
- Ảnh hưởng trải nghiệm người dùng

### Hành Động Để Giải Quyết

**Giải pháp:** Sử dụng **Cache** để lưu trữ dữ liệu tạm thời

Để giảm số lần truy vấn database, hệ thống áp dụng cơ chế **Cache Layer** bằng cách lưu dữ liệu đã tải vào bộ nhớ RAM.

Các cache được xây dựng:
- DashboardCache
- ProjectCache
- TaskCache

**Cách hoạt động**
- *Khi dữ liệu được tải lần đầu:*
Hệ thống gọi DAO để lấy dữ liệu từ database
Sau đó lưu vào cache
- *Những lần truy cập tiếp theo:*
Dữ liệu được lấy trực tiếp từ cache thay vì query lại database
- *Khi dữ liệu thay đổi:*
Cache sẽ được update hoặc clear để đồng bộ dữ liệu mới

### Kết Quả
Sau khi áp dụng cache:
- Giảm đáng kể số lượng query database
- Tốc độ chuyển màn hình nhanh hơn
- Dashboard và task load mượt hơn
- Giảm hiện tượng lag khi thao tác nhiều dữ liệu

### Vấn Đề 2: Khó kiểm tra deadline task liên tục
Trong FlowTask, hệ thống cần phát hiện các task sắp đến hạn để gửi thông báo cho người dùng.

Ban đầu, việc kiểm tra deadline chỉ được thực hiện khi:
- Người dùng mở dashboard
- Reload dữ liệu
- Hoặc thao tác với task

Điều này dẫn đến các vấn đề:
- Notification không được cập nhật kịp thời
- Có thể bỏ sót task sắp hết hạn
- Phải gọi database nhiều lần khi người dùng thao tác thủ công


### Hành Động Để Giải Quyết

**Giải pháp:** Sử dụng Scheduler để kiểm tra deadline tự động

Hệ thống triển khai SchedulerService chạy nền theo chu kỳ để tự động kiểm tra deadline của các task.

**Cách hoạt động**
- Scheduler sẽ chạy định kỳ sau một khoảng thời gian cố định
- Hệ thống tự động:
   - Lấy danh sách task chưa hoàn thành
   - Kiểm tra deadline
   - Xác định task còn dưới 24 giờ
   - Tạo notification tương ứng

### Kết Quả
Sau khi sử dụng Scheduler:
- Notification deadline được cập nhật tự động
- Không cần người dùng reload thủ công
- Giảm số lần kiểm tra deadline không cần thiết
- Hệ thống hoạt động ổn định hơn

Hiệu quả đạt được:
- Tăng độ chính xác của thông báo deadline
- Cải thiện trải nghiệm người dùng
- Giảm tải cho UI khi xử lý dữ liệu nền

## 7. Kết Luận

**Kết quả đạt được:** 

**FlowTask** đã hoàn thiện các chức năng chính của một hệ thống quản lý công việc và project trên desktop.

Ứng dụng hỗ trợ:
- Quản lý project và task
- Theo dõi tiến độ công việc
- Gửi notification khi task sắp đến deadline hoặc có comment mới
- Mời thành viên tham gia project qua email
- Hiển thị dashboard tổng quan công việc

Ngoài ra, hệ thống đã được tối ưu hiệu năng bằng:
- **Cache Layer** để giảm số lần truy vấn database
- **SchedulerService** để xử lý các tác vụ nền như kiểm tra deadline tự động

Kiến trúc ứng dụng được tổ chức theo mô hình:
- MVC
- Layered Architecture
- Service – DAO – Model

giúp hệ thống dễ bảo trì, dễ mở rộng và phù hợp với phát triển lâu dài.

**Hướng phát triển tiếp theo:** 
- Cho phép chỉnh sửa thông tin người dùng
- Đồng bộ dữ liệu realtime giữa các thành viên
- Tích hợp biểu đồ thống kê tiến độ công việc
- Hỗ trợ upload file và attachment cho task
- Tối ưu cache và database để tăng hiệu năng
- Tích hợp AI hỗ trợ gợi ý deadline và sắp xếp công việc
