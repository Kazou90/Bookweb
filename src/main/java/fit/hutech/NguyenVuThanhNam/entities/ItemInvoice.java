package fit.hutech.NguyenVuThanhNam.entities;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "item_invoices")
public class ItemInvoice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "invoice_id")
    private Invoice invoice;

    // Cho phép book = null (khi admin xóa sách, đơn hàng vẫn giữ nguyên thông tin)
    @ManyToOne
    @JoinColumn(name = "book_id", nullable = true)
    private Book book;

    private int quantity;
    private Double price;

    // Lưu snapshot tên sách tại thời điểm mua (để khi xóa sách, đơn hàng vẫn hiển
    // thị tên sách)
    @Column(name = "book_title")
    private String bookTitle;

    // Lưu snapshot tên tác giả
    @Column(name = "book_author")
    private String bookAuthor;
}

