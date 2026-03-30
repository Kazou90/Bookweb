package fit.hutech.NguyenVuThanhNam.repositories;

import fit.hutech.NguyenVuThanhNam.entities.Notification;
import fit.hutech.NguyenVuThanhNam.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface INotificationRepository extends JpaRepository<Notification, Long> {
    // Thông báo cho 1 user cụ thể + thông báo chung (user = null), mới nhất trước
    List<Notification> findByUserOrUserIsNullOrderByCreatedAtDesc(User user);

    // Đếm thông báo chưa đọc của user + thông báo chung chưa đọc
    long countByUserAndIsReadFalse(User user);

    // Thông báo chưa đọc cho user cụ thể
    List<Notification> findByUserAndIsReadFalseOrderByCreatedAtDesc(User user);
}

