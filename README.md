# FlowTask
FlowTask là một app desktop dành cho người muốn quản lý các dự án mà phân chia công việc và theo dõi tiến độ hợp lý, rõ ràng. App sẽ cho phép người dùng phân chia công việc, theo dõi tiến độ đảm bảo đúng deadline, nhắc nhở/ thông báo những khoảng thời gian trưởng nhóm setup khi các thành viên chưa hoàn thành, nếu có nội dung nào vừa được nộp app sẽ hiển thị dự án nào vừa có cập nhật mới ở phần nào, hiển thị trạng thái công việc trực quan.

### Authors
[Đỗ Thị Vân Anh](https://github.com/vananh-bot)
[Trần Ngọc Mai](https://github.com/tngocjm)
[Hà Hoài Phương](https://github.com/phuonghiha)
[Phạm Hoàng Uyên](https://github.com/uyenn123)

### Tech Stack
- Ngôn ngữ: Java
- Framework: JavaFX 
- Database: SQLite
- Thư viện cần dùng:
FullCalendar (lịch)
Chart.js (thống kê)
SortableJS (drag & drop)

### Features
- Danh sách dự án: Hiển thị danh sách các dự án mà người dùng đã tạo hoặc tham gia. Có thể lựa chọn một dự án để truy cập vào bảng quản lý công việc của dự án đó.
- ***Bảng quản lý công việc***: Khi mở một dự án, hệ thống hiển thị bảng quản lý công việc theo dạng To-do list với các trạng thái như: To Do (Công việc cần thực hiện), In Progress (Đang thực hiện), Done (Đã hoàn thành)
> Người dùng có thể ***thêm, chỉnh sửa, cập nhật trạng thái hoặc xóa các công việc trong dự án***:
- Tạo mới dự án: tạo dự án mới bằng cách nhập: Tên dự án , Mô tả dự án
Sau khi tạo, dự án sẽ được lưu vào hệ thống và hiển thị trong danh sách dự án
- Tham gia dự án: có thể mời thành viên khác tham gia dự án thông qua link mời hoặc email. Thành viên được mời có thể truy cập và tham gia quản lý công việc trong dự án.
- ***Thống kê tiến độ***: cung cấp thông tin thống kê về tiến độ dự án, bao gồm số lượng công việc đã hoàn thành, đang thực hiện và chưa bắt đầu.
- ***Thông báo và nhắc nhở deadline***: Hệ thống hiển thị thông báo hoặc nhắc nhở khi công việc sắp đến hạn hoàn thành
  
### Installation

### Usage
- Project Structure 
```bash
├── src
│   ├── Main.java
│   ├── Controller
│   │   ├── TaskController.java
│   │   ├── ProjectController.java
│   │   └── AuthController.java
│   │   └── NotificationCotroller.java
│   │
│   ├── Model
│   │   ├── UserProject.java
│   │   ├── Task.java
│   │   ├── Project.java
│   │   ├── User.java
│   │   ├── Invite.java
│   │   ├── Notification.java
│   │
│   ├── View
```

### Link demo 
