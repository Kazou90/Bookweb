package fit.hutech.NguyenVuThanhNam.controllers;

import fit.hutech.NguyenVuThanhNam.entities.Book;
import fit.hutech.NguyenVuThanhNam.services.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/books")
@RequiredArgsConstructor
public class BookController {
    private final BookService bookService;

    // Danh sach sach voi phan trang va tim kiem (PUBLIC - chi xem)
    @GetMapping
    public String listBooks(@RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(required = false) String keyword,
            Model model) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Book> bookPage;

        if (keyword != null && !keyword.isEmpty()) {
            bookPage = bookService.searchBooks(keyword, pageable);
            model.addAttribute("keyword", keyword);
        } else {
            bookPage = bookService.getAllBooks(pageable);
        }

        model.addAttribute("books", bookPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", bookPage.getTotalPages());
        model.addAttribute("totalItems", bookPage.getTotalElements());
        return "book/list";
    }
}

