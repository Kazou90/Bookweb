package fit.hutech.NguyenVuThanhNam.services;

import fit.hutech.NguyenVuThanhNam.entities.Notification;
import fit.hutech.NguyenVuThanhNam.entities.User;
import fit.hutech.NguyenVuThanhNam.repositories.INotificationRepository;
import fit.hutech.NguyenVuThanhNam.repositories.IUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final INotificationRepository notificationRepository;
    private final IUserRepository userRepository;

    /**
     * Lấy tất cả thông báo cho user (riêng + chung)
     */
    public List<Notification> getNotificationsForUser(User user) {
        return notificationRepository.findByUserOrUserIsNullOrderByCreatedAtDesc(user);
    }

    /**
     * Đếm thông báo chưa đọc
     */
    public long countUnreadForUser(User user) {
        // Đếm riêng cho user + thông báo chung chưa đọc
        long userUnread = notificationRepository.countByUserAndIsReadFalse(user);
        // Thông báo chung chưa đọc (user = null)
        long globalUnread = notificationRepository.findByUserAndIsReadFalseOrderByCreatedAtDesc(null).size();
        return userUnread + globalUnread;
    }

    /**
     * Gửi thông báo cho 1 user cụ thể
     */
    public void sendToUser(User user, String title, String message, String type, String couponCode) {
        Notification notification = Notification.builder()
                .user(user)
                .title(title)
                .message(message)
                .type(type)
                .couponCode(couponCode)
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .build();
        notificationRepository.save(notification);
    }

    /**
     * Gửi thông báo cho TẤT CẢ user
     */
    public void sendToAll(String title, String message, String type, String couponCode) {
        List<User> allUsers = userRepository.findAll();
        for (User user : allUsers) {
            sendToUser(user, title, message, type, couponCode);
        }
    }

    /**
     * Gửi mã giảm giá cho 1 user
     */
    public void sendCouponToUser(User user, String couponCode, String couponDesc) {
        String title = "Bạn nhận được mã giảm giá!";
        String message = "Mã: " + couponCode + " — " + couponDesc
                + ". Nhập mã khi thanh toán để nhận ưu đãi!";
        sendToUser(user, title, message, "COUPON", couponCode);
    }

    /**
     * Gửi mã giảm giá cho tất cả
     */
    public void sendCouponToAll(String couponCode, String couponDesc) {
        String title = "Khuyến mãi mới dành cho bạn!";
        String message = "Mã: " + couponCode + " — " + couponDesc
                + ". Nhập mã khi thanh toán để nhận ưu đãi!";
        sendToAll(title, message, "COUPON", couponCode);
    }

    /**
     * Đánh dấu đã đọc
     */
    public void markAsRead(Long notificationId) {
        notificationRepository.findById(notificationId).ifPresent(n -> {
            n.setIsRead(true);
            notificationRepository.save(n);
        });
    }

    /**
     * Đánh dấu tất cả đã đọc
     */
    public void markAllAsRead(User user) {
        List<Notification> unread = notificationRepository.findByUserAndIsReadFalseOrderByCreatedAtDesc(user);
        for (Notification n : unread) {
            n.setIsRead(true);
        }
        notificationRepository.saveAll(unread);
    }
}

