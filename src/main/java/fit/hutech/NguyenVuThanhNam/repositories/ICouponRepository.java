package fit.hutech.NguyenVuThanhNam.repositories;

import fit.hutech.NguyenVuThanhNam.entities.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ICouponRepository extends JpaRepository<Coupon, Long> {
    Optional<Coupon> findByCode(String code);

    // Tìm các mã giảm giá đang hoạt động
    List<Coupon> findByActiveTrueAndStartDateBeforeAndEndDateAfter(
            LocalDateTime now1, LocalDateTime now2);

    // Tìm tất cả mã đang active
    List<Coupon> findByActiveTrue();
}

