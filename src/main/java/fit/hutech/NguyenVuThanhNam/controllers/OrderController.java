package fit.hutech.NguyenVuThanhNam.controllers;

import fit.hutech.NguyenVuThanhNam.entities.Invoice;
import fit.hutech.NguyenVuThanhNam.entities.User;
import fit.hutech.NguyenVuThanhNam.repositories.IInvoiceRepository;
import fit.hutech.NguyenVuThanhNam.repositories.IUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Collections;
import java.util.List;

@Controller
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {
    private final IInvoiceRepository invoiceRepository;
    private final IUserRepository userRepository;

    /**
     * Danh sách đơn hàng: User chỉ xem đơn của mình
     */
    @GetMapping
    public String listOrders(Authentication authentication, Model model) {
        List<Invoice> orders;
        if (authentication != null) {
            User user = userRepository.findByUsername(authentication.getName()).orElse(null);
            if (user != null) {
                orders = invoiceRepository.findByUserOrderByOrderDateDesc(user);
            } else {
                orders = Collections.emptyList();
            }
        } else {
            orders = Collections.emptyList();
        }
        model.addAttribute("orders", orders);
        return "order/list";
    }

    /**
     * Chi tiết đơn hàng: chỉ xem được đơn của chính mình
     */
    @GetMapping("/{id}")
    public String orderDetail(@PathVariable Long id, Authentication authentication, Model model) {
        Invoice order = invoiceRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Đơn hàng không tồn tại: " + id));

        // Kiểm tra quyền: chỉ user sở hữu đơn hoặc ADMIN mới xem được
        if (authentication != null) {
            User user = userRepository.findByUsername(authentication.getName()).orElse(null);
            boolean isAdmin = authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

            if (!isAdmin && (order.getUser() == null || !order.getUser().equals(user))) {
                return "redirect:/orders";
            }
        }

        model.addAttribute("order", order);
        return "order/detail";
    }
}

