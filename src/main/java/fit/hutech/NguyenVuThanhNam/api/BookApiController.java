package fit.hutech.NguyenVuThanhNam.api;

import fit.hutech.NguyenVuThanhNam.entities.Book;
import fit.hutech.NguyenVuThanhNam.entities.Category;
import fit.hutech.NguyenVuThanhNam.services.BookService;
import fit.hutech.NguyenVuThanhNam.services.CategoryService;
import fit.hutech.NguyenVuThanhNam.viewmodels.BookGetVm;
import fit.hutech.NguyenVuThanhNam.viewmodels.BookPostVm;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/books")
@RequiredArgsConstructor
public class BookApiController {
    private final BookService bookService;
    private final CategoryService categoryService;

    // Chuyen tu Entity sang ViewModel
    private BookGetVm toGetVm(Book book) {
        return BookGetVm.builder()
                .id(book.getId())
                .title(book.getTitle())
                .author(book.getAuthor())
                .price(book.getPrice())
                .image(book.getImage())
                .categoryName(book.getCategory() != null ? book.getCategory().getName() : null)
                .build();
    }

    // Chuyen tu ViewModel sang Entity
    private Book toEntity(BookPostVm vm) {
        Book book = new Book();
        book.setTitle(vm.getTitle());
        book.setAuthor(vm.getAuthor());
        book.setPrice(vm.getPrice());
        if (vm.getCategoryId() != null) {
            Category category = categoryService.getCategoryById(vm.getCategoryId()).orElse(null);
            book.setCategory(category);
        }
        return book;
    }

    @GetMapping
    public ResponseEntity<List<BookGetVm>> getAllBooks() {
        List<BookGetVm> books = bookService.getAllBooks().stream()
                .map(this::toGetVm)
                .collect(Collectors.toList());
        return ResponseEntity.ok(books);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookGetVm> getBookById(@PathVariable Long id) {
        return bookService.getBookById(id)
                .map(book -> ResponseEntity.ok(toGetVm(book)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<BookGetVm> createBook(@Valid @RequestBody BookPostVm bookVm) {
        Book book = toEntity(bookVm);
        bookService.addBook(book);
        return ResponseEntity.ok(toGetVm(book));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BookGetVm> updateBook(@PathVariable Long id, @Valid @RequestBody BookPostVm bookVm) {
        Book book = toEntity(bookVm);
        book.setId(id);
        bookService.updateBook(book);
        return ResponseEntity.ok(toGetVm(book));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        bookService.deleteBookById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<BookGetVm>> searchBooks(@RequestParam String keyword) {
        List<BookGetVm> results = bookService.searchBooks(keyword).stream()
                .map(this::toGetVm)
                .collect(Collectors.toList());
        return ResponseEntity.ok(results);
    }
}

