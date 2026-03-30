package fit.hutech.NguyenVuThanhNam.controllers;

import fit.hutech.NguyenVuThanhNam.entities.Book;
import fit.hutech.NguyenVuThanhNam.entities.CartItem;
import fit.hutech.NguyenVuThanhNam.services.BookService;
import fit.hutech.NguyenVuThanhNam.services.CartService;
import fit.hutech.NguyenVuThanhNam.services.CouponService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;
    private final BookService bookService;
    private final CouponService couponService;

    @GetMapping
    public String viewCart(HttpSession session, Model model) {
        List<CartItem> cart = cartService.getCart(session);
        model.addAttribute("cart", cart);
        model.addAttribute("total", cartService.getTotal(session));
        model.addAttribute("count", cartService.getCount(session));
        return "cart/index";
    }

    @PostMapping("/add/{bookId}")
    public String addToCart(@PathVariable Long bookId, HttpSession session) {
        Book book = bookService.getBookById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("Sách không tồn tại"));
        cartService.addToCart(session, book);
        return "redirect:/books";
    }

    @PostMapping("/update/{bookId}")
    public String updateQuantity(@PathVariable Long bookId,
            @RequestParam int quantity,
            HttpSession session) {
        cartService.updateQuantity(session, bookId, quantity);
        return "redirect:/cart";
    }

    @GetMapping("/remove/{bookId}")
    public String removeFromCart(@PathVariable Long bookId, HttpSession session) {
        cartService.removeFromCart(session, bookId);
        return "redirect:/cart";
    }

    @GetMapping("/clear")
    public String clearCart(HttpSession session) {
        cartService.clearCart(session);
        return "redirect:/cart";
    }

    @GetMapping("/checkout")
    public String showCheckout(HttpSession session, Model model) {
        List<CartItem> cart = cartService.getCart(session);
        if (cart.isEmpty()) {
            return "redirect:/cart";
        }
        model.addAttribute("cart", cart);
        model.addAttribute("total", cartService.getTotal(session));
        model.addAttribute("activeCoupons", couponService.getActiveCoupons());
        return "cart/checkout";
    }
}

