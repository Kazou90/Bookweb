package fit.hutech.NguyenVuThanhNam.entities;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user; // Người nhận (null = gửi cho tất cả)

    @Column(nullable = false)
    private String title; // Tiêu đề thông báo

    @Column(length = 1000)
    private String message; // Nội dung

    private String couponCode; // Mã giảm giá đính kèm (nếu có)

    @Column(nullable = false)
    private String type; // COUPON, INFO, PROMO

    @Builder.Default
    private Boolean isRead = false;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}

