package fit.hutech.NguyenVuThanhNam.repositories;

import fit.hutech.NguyenVuThanhNam.entities.Invoice;
import fit.hutech.NguyenVuThanhNam.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IInvoiceRepository extends JpaRepository<Invoice, Long> {
    // Tìm đơn hàng theo user (lịch sử mua hàng)
    List<Invoice> findByUserOrderByOrderDateDesc(User user);
}

