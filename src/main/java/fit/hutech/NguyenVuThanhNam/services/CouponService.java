package fit.hutech.NguyenVuThanhNam.services;

import fit.hutech.NguyenVuThanhNam.entities.Coupon;
import fit.hutech.NguyenVuThanhNam.repositories.ICouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CouponService {
    private final ICouponRepository couponRepository;

    public List<Coupon> getAllCoupons() {
        return couponRepository.findAll();
    }

    public Optional<Coupon> getCouponById(Long id) {
        return couponRepository.findById(id);
    }

    public Optional<Coupon> getCouponByCode(String code) {
        return couponRepository.findByCode(code.toUpperCase().trim());
    }

    public Coupon saveCoupon(Coupon coupon) {
        coupon.setCode(coupon.getCode().toUpperCase().trim());
        return couponRepository.save(coupon);
    }

    public void deleteCoupon(Long id) {
        couponRepository.deleteById(id);
    }

    /**
     * Lấy danh sách mã giảm giá đang có hiệu lực (để thông báo cho user)
     */
    public List<Coupon> getActiveCoupons() {
        List<Coupon> coupons = couponRepository.findByActiveTrue();
        return coupons.stream()
                .filter(Coupon::isValid)
                .toList();
    }

    /**
     * Áp dụng mã giảm giá: kiểm tra hiệu lực và tính tiền giảm
     */
    public CouponResult applyCoupon(String code, double orderAmount) {
        Optional<Coupon> optCoupon = getCouponByCode(code);
        if (optCoupon.isEmpty()) {
            return new CouponResult(false, "Mã giảm giá không tồn tại", 0, null);
        }

        Coupon coupon = optCoupon.get();
        if (!coupon.isValid()) {
            return new CouponResult(false, "Mã giảm giá đã hết hạn hoặc hết lượt sử dụng", 0, null);
        }

        if (coupon.getMinOrderAmount() != null && orderAmount < coupon.getMinOrderAmount()) {
            return new CouponResult(false,
                    String.format("Đơn hàng tối thiểu %,.0f ₫ để sử dụng mã này",
                            coupon.getMinOrderAmount()),
                    0, null);
        }

        double discount = coupon.calculateDiscount(orderAmount);
        return new CouponResult(true,
                String.format("Giảm %,.0f ₫ (-%,.0f%%)", discount, coupon.getDiscountPercent()),
                discount, coupon);
    }

    /**
     * Đánh dấu mã đã được sử dụng (tăng usedCount)
     */
    public void markCouponUsed(Coupon coupon) {
        coupon.setUsedCount(coupon.getUsedCount() + 1);
        couponRepository.save(coupon);
    }

    // Record kết quả áp dụng mã
    public record CouponResult(boolean success, String message, double discount, Coupon coupon) {
    }
}

