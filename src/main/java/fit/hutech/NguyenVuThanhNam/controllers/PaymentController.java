package fit.hutech.NguyenVuThanhNam.controllers;

import fit.hutech.NguyenVuThanhNam.entities.CartItem;
import fit.hutech.NguyenVuThanhNam.entities.Coupon;
import fit.hutech.NguyenVuThanhNam.services.CartService;
import fit.hutech.NguyenVuThanhNam.services.CouponService;
import fit.hutech.NguyenVuThanhNam.services.InvoiceService;
import fit.hutech.NguyenVuThanhNam.services.SepayService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/payment")
@RequiredArgsConstructor
public class PaymentController {
    private final SepayService sepayService;
    private final CartService cartService;
    private final InvoiceService invoiceService;
    private final CouponService couponService;

    @PostMapping("/checkout")
    public String showPayment(@RequestParam String customerName,
            @RequestParam String phone,
            @RequestParam String email,
            @RequestParam String address,
            @RequestParam(required = false) String couponCode,
            HttpSession session, Model model) {

        List<CartItem> cart = cartService.getCart(session);
        if (cart.isEmpty()) {
            return "redirect:/cart";
        }

        double total = cartService.getTotal(session);
        double discount = 0;
        Coupon appliedCoupon = null;
        String couponMessage = null;

        // Áp dụng mã giảm giá nếu có
        if (couponCode != null && !couponCode.trim().isEmpty()) {
            CouponService.CouponResult result = couponService.applyCoupon(couponCode, total);
            if (result.success()) {
                discount = result.discount();
                appliedCoupon = result.coupon();
                couponMessage = result.message();
            } else {
                couponMessage = result.message();
            }
        }

        double finalTotal = total - discount;
        if (finalTotal < 0)
            finalTotal = 0;

        String orderCode = "DH" + System.currentTimeMillis();

        // Lưu vào session
        session.setAttribute("payment_customerName", customerName);
        session.setAttribute("payment_phone", phone);
        session.setAttribute("payment_email", email);
        session.setAttribute("payment_address", address);
        session.setAttribute("payment_orderCode", orderCode);
        session.setAttribute("payment_total", finalTotal);
        session.setAttribute("payment_originalTotal", total);
        session.setAttribute("payment_discount", discount);
        if (appliedCoupon != null) {
            session.setAttribute("payment_couponId", appliedCoupon.getId());
            session.setAttribute("payment_couponCode", appliedCoupon.getCode());
        }

        // Tạo QR code với số tiền sau giảm
        String qrCodeUrl = sepayService.generateQRCodeUrl(finalTotal, orderCode);

        model.addAttribute("cart", cart);
        model.addAttribute("total", total);
        model.addAttribute("discount", discount);
        model.addAttribute("finalTotal", finalTotal);
        model.addAttribute("orderCode", orderCode);
        model.addAttribute("qrCodeUrl", qrCodeUrl);
        model.addAttribute("customerName", customerName);
        model.addAttribute("phone", phone);
        model.addAttribute("email", email);
        model.addAttribute("address", address);
        model.addAttribute("accountNumber", sepayService.getAccountNumber());
        model.addAttribute("bankCode", sepayService.getBankCode());
        model.addAttribute("accountName", sepayService.getAccountName());
        model.addAttribute("couponCode", couponCode);
        model.addAttribute("couponMessage", couponMessage);
        model.addAttribute("appliedCoupon", appliedCoupon);

        return "payment/qr-checkout";
    }

    @GetMapping("/check-status")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> checkPaymentStatus(HttpSession session) {
        String orderCode = (String) session.getAttribute("payment_orderCode");
        Double total = (Double) session.getAttribute("payment_total");

        if (orderCode == null || total == null) {
            return ResponseEntity.ok(Map.of("paid", false, "message", "Không tìm thấy thông tin đơn hàng"));
        }

        boolean paid = sepayService.checkPaymentStatus(orderCode, total);

        if (paid) {
            return ResponseEntity.ok(Map.of("paid", true, "message", "Thanh toán thành công!"));
        }

        return ResponseEntity.ok(Map.of("paid", false, "message", "Đang chờ thanh toán..."));
    }

    @PostMapping("/confirm")
    public String confirmPayment(HttpSession session) {
        String orderCode = (String) session.getAttribute("payment_orderCode");
        Double total = (Double) session.getAttribute("payment_total");

        if (orderCode == null || total == null) {
            return "redirect:/cart";
        }

        boolean paid = sepayService.checkPaymentStatus(orderCode, total);
        if (!paid) {
            return "redirect:/cart?error=unpaid";
        }

        // Tạo đơn hàng
        String customerName = (String) session.getAttribute("payment_customerName");
        String phone = (String) session.getAttribute("payment_phone");
        String email = (String) session.getAttribute("payment_email");
        String address = (String) session.getAttribute("payment_address");

        invoiceService.createInvoice(customerName, phone, email, address, total, "PAID", session);

        // Đánh dấu mã giảm giá đã sử dụng
        Long couponId = (Long) session.getAttribute("payment_couponId");
        if (couponId != null) {
            couponService.getCouponById(couponId).ifPresent(couponService::markCouponUsed);
        }

        // Xóa thông tin payment
        session.removeAttribute("payment_customerName");
        session.removeAttribute("payment_phone");
        session.removeAttribute("payment_email");
        session.removeAttribute("payment_address");
        session.removeAttribute("payment_orderCode");
        session.removeAttribute("payment_total");
        session.removeAttribute("payment_originalTotal");
        session.removeAttribute("payment_discount");
        session.removeAttribute("payment_couponId");
        session.removeAttribute("payment_couponCode");

        return "redirect:/?success";
    }
}

