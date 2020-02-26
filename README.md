# AnyQuestion


![Image description](https://github.com/tranvanduong2483/NotificationDUT/blob/master/image/1.png) ![Image description](https://github.com/tranvanduong2483/NotificationDUT/blob/master/image/2.png)



#### 1. Vấn đề?

    - “Ứng dụng Any Question” chạy trên nền Android, ứng dụng ra đời để giải đáp những thắc mắc trong
    học tập cho người dùng cần giúp đỡ trong quá trình học tập của mình. Ứng dụng kết nối những câu hỏi
    thắc mắc đến các chuyên gia đúng lĩnh vực để đưa ra cách giải quyết nhanh chóng, tiện lợi và hiệu
    quả. Về phía chuyên gia, ứng dụng giúp các chuyên gia kiếm tiền vào những thời gian rảnh rỗi
    trong ngày của mình.
    - Hệ thống phục vụ cho 3 đối tượng chính:
        + Người quản trị.
        + Giáo viên.
        + Học sinh.
    - Ứng dụng cho phép người dùng đặt câu hỏi lên hệ thống bằng cách đăng nhập vào tài khoản đã đăng ký.
    Sau khi đặt câu hỏi, hệ thống sẽ tìm kiếm chuyên gia phù hợp và chuyển người dùng vào màn hình nhắn tin
    với chuyên gia. Sau khi câu hỏi thắc mắc được chuyên gia giải đáp, người dùng có thể đánh giá số sao
    tương ứng với mức độ đóng góp của chuyên gia. Mỗi câu hỏi người dùng sẽ trả tiền ở mức giá nhất định
    tùy vào mức độ câu hỏi, số tiền này sẽ được gửi đến các chuyên gia cũng như duy trì hệ thống.


#### 2. Mô tả bài toán: Xây dựng ứng dụng Android thông báo từ việc lấy dữ liệu từ sv.dut.udn.vn
    - Nhận "Thông báo chung"
    - Nhận "Thông báo đến lớp học phần"
    - Nhận thông báo qua email hoặc thông báo ngay trên ứng dụng
    - Xem lịch học offline

#### 3. Sơ đồ hoạt động

![Image description](https://github.com/tranvanduong2483/NotificationDUT/blob/master/image/5.png?s=50)

    - Quá trình đăng nhập:
        (1) Client gửi thông tin đăng nhập và token lên server
        (2) Server tiến hành lấy thông tin đăng nhập để lấy được thông tin sinh viên về thông tin cá nhân và học phần của sinh viên này
        (3.1) Lưu thông tin cá nhân, và học phần về cơ sở dữ liệu MySQL
        (3.2) Lưu học phần vào Firebase Realtime Database
        (3.3) Phản hồi kết quả đăng nhập

    - Quá trình server xử lý thông báo:
        (4) Sau mỗi 30 phút, server tiến hành lấy dữ liệu thông báo từ trang sv.dut.udn.vn
        (5.1), (5.2) Dựa vào dữ liệu thông báo và dữ liệu sinh viên, tiến hành phân tích thông báo đến từng sinh viên
        (6.1) Lưu dữ liệu thông báo vào Firebase Realtime Database để client truy cập
        (6.2) Gửi thông báo và token lên Firebase Cloud Messaging
        (7) Firebase Cloud Messaging có nhiệm vụ phân phát thông báo đến clien dựa vào token

    - Quá trình server xử lý dữ liệu về tuần học:
        (8) Sau mỗi 30 phút, server tiến hành lấy dữ liệu tuần học từ trang dut.udn.vn/lichtuan21
        (9) Lưu dữ liệu tuần học về Firebase Realtime Database

    - Quá trình client xem thông báo, lịch tuần và học phần:
        (10) Client lấy dữ liệu thông báo, lịch tuần và học phần dựa vào nút “Mã Sinh Viên“

#### 4. Firebase Realtime Database:

![Image description](https://github.com/tranvanduong2483/NotificationDUT/blob/master/image/6.png)

#### 5. Demo ứng dụng:

    https://www.youtube.com/watch?v=vbTvzUCPBqM

    https://youtu.be/IvCQnJ6cVoQ

#### 6. Mã nguồn server:

    Server (Nodejs): https://github.com/tranvanduong2483/server-dut-notification

 
