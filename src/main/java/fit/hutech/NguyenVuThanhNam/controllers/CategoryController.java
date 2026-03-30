package fit.hutech.NguyenVuThanhNam.controllers;

import fit.hutech.NguyenVuThanhNam.services.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

    // PUBLIC: Xem danh sach danh muc (chi doc)
    @GetMapping
    public String listCategories(Model model) {
        model.addAttribute("categories", categoryService.getAllCategories());
        return "category/list";
    }
}

