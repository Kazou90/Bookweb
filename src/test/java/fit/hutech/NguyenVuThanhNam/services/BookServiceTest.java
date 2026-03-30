package fit.hutech.NguyenVuThanhNam.services;

import fit.hutech.NguyenVuThanhNam.entities.Book;
import fit.hutech.NguyenVuThanhNam.entities.Category;
import fit.hutech.NguyenVuThanhNam.repositories.IBookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    private IBookRepository bookRepository;

    @InjectMocks
    private BookService bookService;

    private Book book1;
    private Book book2;

    @BeforeEach
    void setUp() {
        Category category = Category.builder().id(1L).name("Công nghệ").build();
        book1 = Book.builder()
                .id(1L)
                .title("Java Programming")
                .author("John Doe")
                .price(150000.0)
                .category(category)
                .build();
        book2 = Book.builder()
                .id(2L)
                .title("Spring Boot in Action")
                .author("Jane Smith")
                .price(200000.0)
                .category(category)
                .build();
    }

    @Test
    void testGetAllBooks() {
        when(bookRepository.findAll()).thenReturn(Arrays.asList(book1, book2));
        List<Book> books = bookService.getAllBooks();
        assertEquals(2, books.size());
        verify(bookRepository, times(1)).findAll();
    }

    @Test
    void testGetBookById_Found() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book1));
        Optional<Book> found = bookService.getBookById(1L);
        assertTrue(found.isPresent());
        assertEquals("Java Programming", found.get().getTitle());
    }

    @Test
    void testGetBookById_NotFound() {
        when(bookRepository.findById(99L)).thenReturn(Optional.empty());
        Optional<Book> found = bookService.getBookById(99L);
        assertFalse(found.isPresent());
    }

    @Test
    void testAddBook() {
        when(bookRepository.save(book1)).thenReturn(book1);
        bookService.addBook(book1);
        verify(bookRepository, times(1)).save(book1);
    }

    @Test
    void testUpdateBook() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book1));
        when(bookRepository.save(any(Book.class))).thenReturn(book1);
        book1.setTitle("Updated Title");
        bookService.updateBook(book1);
        verify(bookRepository, times(1)).save(any(Book.class));
    }

    @Test
    void testDeleteBookById() {
        doNothing().when(bookRepository).deleteById(1L);
        bookService.deleteBookById(1L);
        verify(bookRepository, times(1)).deleteById(1L);
    }

    @Test
    void testSearchBooks() {
        when(bookRepository.findByTitleContainingIgnoreCase("Java"))
                .thenReturn(Arrays.asList(book1));
        List<Book> results = bookService.searchBooks("Java");
        assertEquals(1, results.size());
        assertEquals("Java Programming", results.get(0).getTitle());
    }

    @Test
    void testUpdateBook_NotFound_ThrowsException() {
        when(bookRepository.findById(99L)).thenReturn(Optional.empty());
        Book nonExistent = Book.builder().id(99L).title("Ghost").build();
        assertThrows(IllegalArgumentException.class, () -> bookService.updateBook(nonExistent));
    }
}

