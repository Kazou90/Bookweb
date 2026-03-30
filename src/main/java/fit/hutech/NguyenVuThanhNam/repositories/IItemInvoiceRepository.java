package fit.hutech.NguyenVuThanhNam.repositories;

import fit.hutech.NguyenVuThanhNam.entities.Book;
import fit.hutech.NguyenVuThanhNam.entities.ItemInvoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IItemInvoiceRepository extends JpaRepository<ItemInvoice, Long> {
    // Tìm tất cả item_invoice liên quan đến 1 book
    List<ItemInvoice> findByBook(Book book);
}

