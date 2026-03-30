package fit.hutech.NguyenVuThanhNam.entities;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "coupons")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Coupon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 50)
    private String code; // Mã giảm giá VD: SALE20, WELCOME10

    private String description; // Mô tả: "Giảm 20% cho đơn hàng từ 200k"

    @Column(nullable = false)
    private Double discountPercent; // % giảm giá (VD: 10, 20, 50)

    private Double maxDiscount; // Giảm tối đa (VD: 50000 = 50k)

    private Double minOrderAmount; // Đơn tối thiểu để áp dụng (VD: 100000 = 100k)

    private LocalDateTime startDate; // Ngày bắt đầu

    private LocalDateTime endDate; // Ngày hết hạn

    private Integer usageLimit; // Số lần sử dụng tối đa (null = không giới hạn)

    private Integer usedCount = 0; // Số lần đã sử dụng

    private Boolean active = true; // Trạng thái kích hoạt

    // Kiểm tra mã còn hiệu lực không
    public boolean isValid() {
        if (!active)
            return false;
        LocalDateTime now = LocalDateTime.now();
        if (startDate != null && now.isBefore(startDate))
            return false;
        if (endDate != null && now.isAfter(endDate))
            return false;
        if (usageLimit != null && usedCount >= usageLimit)
            return false;
        return true;
    }

    // Tính số tiền giảm
    public double calculateDiscount(double orderAmount) {
        if (!isValid())
            return 0;
        if (minOrderAmount != null && orderAmount < minOrderAmount)
            return 0;
        double discount = orderAmount * discountPercent / 100.0;
        if (maxDiscount != null && discount > maxDiscount) {
            discount = maxDiscount;
        }
        return Math.round(discount);
    }
}

