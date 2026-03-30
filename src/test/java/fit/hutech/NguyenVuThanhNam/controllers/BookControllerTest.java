package fit.hutech.NguyenVuThanhNam.controllers;

import fit.hutech.NguyenVuThanhNam.entities.Book;
import fit.hutech.NguyenVuThanhNam.entities.Category;
import fit.hutech.NguyenVuThanhNam.services.BookService;
import fit.hutech.NguyenVuThanhNam.services.CategoryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BookService bookService;

    @MockitoBean
    private CategoryService categoryService;

    @Test
    @WithMockUser(roles = "USER")
    void testListBooks() throws Exception {
        Category category = Category.builder().id(1L).name("Cong nghe").build();
        List<Book> books = Arrays.asList(
                Book.builder().id(1L).title("Java").author("John").price(100000.0).category(category).build());
        Page<Book> bookPage = new PageImpl<>(books);

        when(bookService.getAllBooks(any(Pageable.class))).thenReturn(bookPage);

        mockMvc.perform(get("/books"))
                .andExpect(status().isOk())
                .andExpect(view().name("book/list"))
                .andExpect(model().attributeExists("books"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testAddBookForm() throws Exception {
        when(categoryService.getAllCategories()).thenReturn(Arrays.asList());

        mockMvc.perform(get("/books/add"))
                .andExpect(status().isOk())
                .andExpect(view().name("book/add"))
                .andExpect(model().attributeExists("book", "categories"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testEditBookForm() throws Exception {
        Book book = Book.builder().id(1L).title("Test").author("Author").price(50000.0).build();
        when(bookService.getBookById(1L)).thenReturn(Optional.of(book));
        when(categoryService.getAllCategories()).thenReturn(Arrays.asList());

        mockMvc.perform(get("/books/edit/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("book/edit"))
                .andExpect(model().attributeExists("book", "categories"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void testAddBookForbiddenForUser() throws Exception {
        mockMvc.perform(get("/books/add"))
                .andExpect(status().isForbidden());
    }
}

