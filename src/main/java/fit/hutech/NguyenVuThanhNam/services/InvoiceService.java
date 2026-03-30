package fit.hutech.NguyenVuThanhNam.services;

import fit.hutech.NguyenVuThanhNam.entities.*;
import fit.hutech.NguyenVuThanhNam.repositories.IInvoiceRepository;
import fit.hutech.NguyenVuThanhNam.repositories.IUserRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class InvoiceService {
    private final IInvoiceRepository invoiceRepository;
    private final IUserRepository userRepository;
    private final CartService cartService;

    public Invoice createInvoice(String customerName, String phone, String email,
            String address, Double totalPrice, String status, HttpSession session) {
        List<CartItem> cartItems = cartService.getCart(session);
        if (cartItems.isEmpty()) {
            throw new IllegalStateException("Giỏ hàng trống, không thể tạo hóa đơn");
        }

        // Lấy user đang đăng nhập (nếu có)
        User currentUser = getCurrentUser();

        Invoice invoice = Invoice.builder()
                .customerName(customerName)
                .phone(phone)
                .email(email)
                .address(address)
                .orderDate(LocalDateTime.now())
                .totalPrice(totalPrice) // Sử dụng tổng tiền truyền vào (đã tính giảm giá)
                .status(status != null ? status : "PENDING")
                .user(currentUser) // Liên kết đơn hàng với user
                .build();

        List<ItemInvoice> items = new ArrayList<>();
        for (CartItem cartItem : cartItems) {
            ItemInvoice itemInvoice = ItemInvoice.builder()
                    .invoice(invoice)
                    .book(cartItem.getBook())
                    .quantity(cartItem.getQuantity())
                    .price(cartItem.getBook().getPrice())
                    // Lưu snapshot tên sách và tác giả (không bị ảnh hưởng khi admin xóa sách)
                    .bookTitle(cartItem.getBook().getTitle())
                    .bookAuthor(cartItem.getBook().getAuthor())
                    .build();
            items.add(itemInvoice);
        }
        invoice.setItems(items);

        Invoice savedInvoice = invoiceRepository.save(invoice);
        cartService.clearCart(session);
        return savedInvoice;
    }

    /**
     * Lấy user hiện tại đang đăng nhập
     */
    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()
                && !"anonymousUser".equals(authentication.getPrincipal())) {
            String username = authentication.getName();
            return userRepository.findByUsername(username).orElse(null);
        }
        return null;
    }
}

