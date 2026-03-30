package fit.hutech.NguyenVuThanhNam.services;

import fit.hutech.NguyenVuThanhNam.entities.Book;
import fit.hutech.NguyenVuThanhNam.entities.ItemInvoice;
import fit.hutech.NguyenVuThanhNam.repositories.IBookRepository;
import fit.hutech.NguyenVuThanhNam.repositories.IItemInvoiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class BookService {
    private final IBookRepository bookRepository;
    private final IItemInvoiceRepository itemInvoiceRepository;

    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    public Page<Book> getAllBooks(Pageable pageable) {
        return bookRepository.findAll(pageable);
    }

    public Optional<Book> getBookById(Long id) {
        return bookRepository.findById(id);
    }

    public void addBook(Book book) {
        bookRepository.save(book);
    }

    public void updateBook(Book book) {
        Book existingBook = bookRepository.findById(book.getId())
                .orElseThrow(() -> new IllegalArgumentException("Sách không tồn tại"));
        existingBook.setTitle(book.getTitle());
        existingBook.setAuthor(book.getAuthor());
        existingBook.setPrice(book.getPrice());
        existingBook.setCategory(book.getCategory());
        existingBook.setImage(book.getImage());
        bookRepository.save(existingBook);
    }

    /**
     * Xóa sách: lịch sử đơn hàng chứa sách này sẽ KHÔNG bị xóa.
     * Set book = null trong item_invoice, giữ lại snapshot tên/giá.
     */
    public void deleteBookById(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Sách không tồn tại: " + id));

        // Tìm tất cả item_invoice liên quan và set book = null (giữ snapshot)
        List<ItemInvoice> relatedItems = itemInvoiceRepository.findByBook(book);
        for (ItemInvoice item : relatedItems) {
            // Nếu chưa lưu snapshot, lưu ngay
            if (item.getBookTitle() == null) {
                item.setBookTitle(book.getTitle());
            }
            if (item.getBookAuthor() == null) {
                item.setBookAuthor(book.getAuthor());
            }
            item.setBook(null); // Xóa liên kết FK
            itemInvoiceRepository.save(item);
        }

        bookRepository.deleteById(id);
    }

    public List<Book> searchBooks(String keyword) {
        return bookRepository.findByTitleContainingIgnoreCase(keyword);
    }

    public Page<Book> searchBooks(String keyword, Pageable pageable) {
        return bookRepository.findByTitleContainingIgnoreCase(keyword, pageable);
    }
}

