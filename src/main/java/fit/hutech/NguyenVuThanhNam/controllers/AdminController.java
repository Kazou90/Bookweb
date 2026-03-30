package fit.hutech.NguyenVuThanhNam.controllers;

import fit.hutech.NguyenVuThanhNam.entities.Book;
import fit.hutech.NguyenVuThanhNam.entities.Category;
import fit.hutech.NguyenVuThanhNam.entities.Coupon;
import fit.hutech.NguyenVuThanhNam.entities.Invoice;
import fit.hutech.NguyenVuThanhNam.entities.Role;
import fit.hutech.NguyenVuThanhNam.entities.User;
import fit.hutech.NguyenVuThanhNam.repositories.IBookRepository;
import fit.hutech.NguyenVuThanhNam.repositories.ICategoryRepository;
import fit.hutech.NguyenVuThanhNam.repositories.IInvoiceRepository;
import fit.hutech.NguyenVuThanhNam.repositories.IRoleRepository;
import fit.hutech.NguyenVuThanhNam.repositories.IUserRepository;
import fit.hutech.NguyenVuThanhNam.services.BookService;
import fit.hutech.NguyenVuThanhNam.services.CategoryService;
import fit.hutech.NguyenVuThanhNam.services.CouponService;
import fit.hutech.NguyenVuThanhNam.services.NotificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {
    private final IBookRepository bookRepository;
    private final ICategoryRepository categoryRepository;
    private final IInvoiceRepository invoiceRepository;
    private final IUserRepository userRepository;
    private final IRoleRepository roleRepository;
    private final BookService bookService;
    private final CategoryService categoryService;
    private final CouponService couponService;
    private final NotificationService notificationService;

    @GetMapping
    public String dashboard(Model model) {
        long totalBooks = bookRepository.count();
        long totalCategories = categoryRepository.count();
        long totalOrders = invoiceRepository.count();
        long totalUsers = userRepository.count();

        List<Invoice> allInvoices = invoiceRepository.findAll();
        double totalRevenue = allInvoices.stream()
                .filter(inv -> inv.getTotalPrice() != null)
                .mapToDouble(Invoice::getTotalPrice)
                .sum();

        List<Invoice> recentOrders = invoiceRepository.findAll(PageRequest.of(0, 10)).getContent();
        List<User> users = userRepository.findAll();

        model.addAttribute("totalBooks", totalBooks);
        model.addAttribute("totalCategories", totalCategories);
        model.addAttribute("totalOrders", totalOrders);
        model.addAttribute("totalUsers", totalUsers);
        model.addAttribute("totalRevenue", totalRevenue);
        model.addAttribute("recentOrders", recentOrders);
        model.addAttribute("users", users);
        model.addAttribute("books", bookService.getAllBooks());
        model.addAttribute("categories", categoryService.getAllCategories());

        return "admin/dashboard";
    }

    @GetMapping("/books")
    public String manageBooks(Model model) {
        model.addAttribute("books", bookService.getAllBooks());
        model.addAttribute("categories", categoryService.getAllCategories());
        return "admin/books";
    }

    @GetMapping("/categories")
    public String manageCategories(Model model) {
        model.addAttribute("categories", categoryService.getAllCategories());
        return "admin/categories";
    }

    // ==================== CRUD SÁCH (ADMIN) ====================

    @GetMapping("/books/add")
    public String adminAddBookForm(Model model) {
        model.addAttribute("book", new Book());
        model.addAttribute("categories", categoryService.getAllCategories());
        return "admin/book-add";
    }

    @PostMapping("/books/add")
    public String adminAddBook(@Valid @ModelAttribute("book") Book book,
            BindingResult result,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
            Model model, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("categories", categoryService.getAllCategories());
            return "admin/book-add";
        }
        if (imageFile != null && !imageFile.isEmpty()) {
            String imageName = saveImage(imageFile);
            book.setImage(imageName);
        }
        bookService.addBook(book);
        redirectAttributes.addFlashAttribute("success", "Thêm sách '" + book.getTitle() + "' thành công!");
        return "redirect:/admin/books";
    }

    @GetMapping("/books/edit/{id}")
    public String adminEditBookForm(@PathVariable Long id, Model model) {
        Book book = bookService.getBookById(id)
                .orElseThrow(() -> new IllegalArgumentException("Sách không tồn tại: " + id));
        model.addAttribute("book", book);
        model.addAttribute("categories", categoryService.getAllCategories());
        return "admin/book-edit";
    }

    @PostMapping("/books/edit")
    public String adminEditBook(@Valid @ModelAttribute("book") Book book,
            BindingResult result,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
            Model model, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("categories", categoryService.getAllCategories());
            return "admin/book-edit";
        }
        if (imageFile != null && !imageFile.isEmpty()) {
            String imageName = saveImage(imageFile);
            book.setImage(imageName);
        }
        bookService.updateBook(book);
        redirectAttributes.addFlashAttribute("success", "Cập nhật sách '" + book.getTitle() + "' thành công!");
        return "redirect:/admin/books";
    }

    @GetMapping("/books/delete/{id}")
    public String adminDeleteBook(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            bookService.deleteBookById(id);
            redirectAttributes.addFlashAttribute("success", "Đã xóa sách thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi xóa sách: " + e.getMessage());
        }
        return "redirect:/admin/books";
    }

    // ==================== CRUD DANH MỤC (ADMIN) ====================

    @GetMapping("/categories/add")
    public String adminAddCategoryForm(Model model) {
        model.addAttribute("category", new Category());
        return "admin/category-add";
    }

    @PostMapping("/categories/add")
    public String adminAddCategory(@Valid @ModelAttribute("category") Category category,
            BindingResult result, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "admin/category-add";
        }
        categoryService.addCategory(category);
        redirectAttributes.addFlashAttribute("success", "Thêm danh mục '" + category.getName() + "' thành công!");
        return "redirect:/admin/categories";
    }

    @GetMapping("/categories/edit/{id}")
    public String adminEditCategoryForm(@PathVariable Long id, Model model) {
        Category category = categoryService.getCategoryById(id)
                .orElseThrow(() -> new IllegalArgumentException("Danh mục không tồn tại: " + id));
        model.addAttribute("category", category);
        return "admin/category-edit";
    }

    @PostMapping("/categories/edit")
    public String adminEditCategory(@Valid @ModelAttribute("category") Category category,
            BindingResult result, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "admin/category-edit";
        }
        categoryService.updateCategory(category);
        redirectAttributes.addFlashAttribute("success", "Cập nhật danh mục '" + category.getName() + "' thành công!");
        return "redirect:/admin/categories";
    }

    @GetMapping("/categories/delete/{id}")
    public String adminDeleteCategory(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            categoryService.deleteCategoryById(id);
            redirectAttributes.addFlashAttribute("success", "Đã xóa danh mục thành công! Sách liên quan giữ nguyên.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi xóa danh mục: " + e.getMessage());
        }
        return "redirect:/admin/categories";
    }

    // Helper: Lưu ảnh vào thư mục static/images
    private String saveImage(MultipartFile file) {
        try {
            String fileName = java.util.UUID.randomUUID() + "_" + file.getOriginalFilename();
            java.nio.file.Path uploadDir = java.nio.file.Paths.get("src/main/resources/static/images");
            if (!java.nio.file.Files.exists(uploadDir)) {
                java.nio.file.Files.createDirectories(uploadDir);
            }
            java.nio.file.Path filePath = uploadDir.resolve(fileName);
            java.nio.file.Files.copy(file.getInputStream(), filePath,
                    java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            return fileName;
        } catch (java.io.IOException e) {
            throw new RuntimeException("Không thể lưu ảnh: " + e.getMessage());
        }
    }

    @GetMapping("/orders")
    public String manageOrders(Model model) {
        model.addAttribute("orders", invoiceRepository.findAll());
        return "admin/orders";
    }

    @GetMapping("/orders/{id}")
    public String orderDetail(@PathVariable Long id, Model model) {
        Invoice order = invoiceRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Đơn hàng không tồn tại: " + id));
        model.addAttribute("order", order);
        return "admin/order-detail";
    }

    @PostMapping("/orders/{id}/status")
    public String updateOrderStatus(@PathVariable Long id, @RequestParam String status,
            RedirectAttributes redirectAttributes) {
        Invoice order = invoiceRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Đơn hàng không tồn tại: " + id));
        order.setStatus(status);
        invoiceRepository.save(order);
        redirectAttributes.addFlashAttribute("success", "Cập nhật trạng thái đơn hàng thành công!");
        return "redirect:/admin/orders/" + id;
    }

    @GetMapping("/users")
    public String manageUsers(Model model, Authentication authentication) {
        model.addAttribute("users", userRepository.findAll());
        model.addAttribute("allRoles", roleRepository.findAll());
        model.addAttribute("currentAdmin", authentication.getName());
        return "admin/users";
    }

    @PostMapping("/users/{userId}/roles")
    public String updateUserRoles(@PathVariable Long userId,
            @RequestParam(value = "roles", required = false) List<String> roleNames,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        User targetUser = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Người dùng không tồn tại"));

        boolean targetIsAdmin = targetUser.getRoles().stream()
                .anyMatch(r -> r.getName().equals("ADMIN"));

        if (targetIsAdmin) {
            redirectAttributes.addFlashAttribute("error",
                    "Không thể chỉnh sửa vai trò của Admin cùng cấp: " + targetUser.getUsername());
            return "redirect:/admin/users";
        }

        if (targetUser.getUsername().equals(authentication.getName())) {
            redirectAttributes.addFlashAttribute("error",
                    "Không thể chỉnh sửa vai trò của chính bạn!");
            return "redirect:/admin/users";
        }

        Set<Role> newRoles = new HashSet<>();
        if (roleNames != null) {
            for (String roleName : roleNames) {
                roleRepository.findByName(roleName).ifPresent(newRoles::add);
            }
        }

        if (newRoles.isEmpty()) {
            roleRepository.findByName("USER").ifPresent(newRoles::add);
        }

        targetUser.setRoles(newRoles);
        userRepository.save(targetUser);

        redirectAttributes.addFlashAttribute("success",
                "Đã cập nhật vai trò cho " + targetUser.getUsername() + " thành công!");
        return "redirect:/admin/users";
    }

    // ==================== QUẢN LÝ MÃ GIẢM GIÁ ====================

    @GetMapping("/coupons")
    public String manageCoupons(Model model) {
        model.addAttribute("coupons", couponService.getAllCoupons());
        model.addAttribute("newCoupon", new Coupon());
        model.addAttribute("users", userRepository.findAll());
        return "admin/coupons";
    }

    @PostMapping("/coupons/add")
    public String addCoupon(@RequestParam String code,
            @RequestParam String description,
            @RequestParam Double discountPercent,
            @RequestParam(required = false) Double maxDiscount,
            @RequestParam(required = false) Double minOrderAmount,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(required = false) Integer usageLimit,
            RedirectAttributes redirectAttributes) {

        // Kiểm tra mã đã tồn tại chưa
        if (couponService.getCouponByCode(code).isPresent()) {
            redirectAttributes.addFlashAttribute("error", "Mã giảm giá '" + code + "' đã tồn tại!");
            return "redirect:/admin/coupons";
        }

        Coupon coupon = new Coupon();
        coupon.setCode(code.toUpperCase().trim());
        coupon.setDescription(description);
        coupon.setDiscountPercent(discountPercent);
        coupon.setMaxDiscount(maxDiscount);
        coupon.setMinOrderAmount(minOrderAmount);
        coupon.setStartDate(startDate != null ? startDate : LocalDateTime.now());
        coupon.setEndDate(endDate);
        coupon.setUsageLimit(usageLimit);
        coupon.setUsedCount(0);
        coupon.setActive(true);

        couponService.saveCoupon(coupon);
        redirectAttributes.addFlashAttribute("success", "Thêm mã giảm giá '" + coupon.getCode() + "' thành công!");
        return "redirect:/admin/coupons";
    }

    @PostMapping("/coupons/{id}/toggle")
    public String toggleCoupon(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        couponService.getCouponById(id).ifPresent(coupon -> {
            coupon.setActive(!coupon.getActive());
            couponService.saveCoupon(coupon);
            redirectAttributes.addFlashAttribute("success",
                    (coupon.getActive() ? "Kích hoạt" : "Tắt") + " mã '" + coupon.getCode() + "' thành công!");
        });
        return "redirect:/admin/coupons";
    }

    @GetMapping("/coupons/delete/{id}")
    public String deleteCoupon(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        couponService.getCouponById(id).ifPresent(coupon -> {
            couponService.deleteCoupon(id);
            redirectAttributes.addFlashAttribute("success", "Đã xóa mã '" + coupon.getCode() + "'");
        });
        return "redirect:/admin/coupons";
    }

    // ==================== GỬI MÃ GIẢM GIÁ CHO USER ====================

    @PostMapping("/coupons/{id}/send")
    public String sendCouponToUser(@PathVariable Long id,
            @RequestParam(required = false) Long userId,
            @RequestParam(defaultValue = "false") boolean sendToAll,
            RedirectAttributes redirectAttributes) {

        var couponOpt = couponService.getCouponById(id);
        if (couponOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Mã giảm giá không tồn tại");
            return "redirect:/admin/coupons";
        }
        Coupon coupon = couponOpt.get();

        if (sendToAll) {
            notificationService.sendCouponToAll(coupon.getCode(), coupon.getDescription());
            redirectAttributes.addFlashAttribute("success",
                    "Đã gửi mã '" + coupon.getCode() + "' cho tất cả người dùng!");
        } else if (userId != null) {
            var userOpt = userRepository.findById(userId);
            if (userOpt.isPresent()) {
                notificationService.sendCouponToUser(userOpt.get(), coupon.getCode(), coupon.getDescription());
                redirectAttributes.addFlashAttribute("success",
                        "Đã gửi mã '" + coupon.getCode() + "' cho " + userOpt.get().getUsername() + "!");
            }
        } else {
            redirectAttributes.addFlashAttribute("error", "Vui lòng chọn người nhận!");
        }
        return "redirect:/admin/coupons";
    }

    // ==================== GỬI THÔNG BÁO TÙY CHỈNH ====================

    @PostMapping("/notifications/send")
    public String sendNotification(
            @RequestParam String title,
            @RequestParam String message,
            @RequestParam(required = false) Long userId,
            @RequestParam(defaultValue = "false") boolean sendToAll,
            RedirectAttributes redirectAttributes) {

        if (sendToAll) {
            notificationService.sendToAll(title, message, "INFO", null);
            redirectAttributes.addFlashAttribute("success", "Đã gửi thông báo cho tất cả người dùng!");
        } else if (userId != null) {
            var userOpt = userRepository.findById(userId);
            if (userOpt.isPresent()) {
                notificationService.sendToUser(userOpt.get(), title, message, "INFO", null);
                redirectAttributes.addFlashAttribute("success",
                        "Đã gửi thông báo cho " + userOpt.get().getUsername() + "!");
            }
        }
        return "redirect:/admin/coupons";
    }
}

