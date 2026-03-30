package fit.hutech.NguyenVuThanhNam.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fit.hutech.NguyenVuThanhNam.config.SepayConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * Service tích hợp SePay Payment Gateway.
 * - Tạo URL QR code thanh toán
 * - Kiểm tra giao dịch đã nhận tiền chưa
 */
@Service
@Slf4j
public class SepayService {
    private final SepayConfig sepayConfig;
    private final ObjectMapper objectMapper;

    public SepayService(SepayConfig sepayConfig) {
        this.sepayConfig = sepayConfig;
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Tạo URL QR code thanh toán.
     * 
     * @param amount  Số tiền (VNĐ)
     * @param content Nội dung chuyển khoản (mã đơn hàng)
     * @return URL ảnh QR code
     */
    public String generateQRCodeUrl(double amount, String content) {
        String encodedContent = URLEncoder.encode(content, StandardCharsets.UTF_8);
        return String.format(
                "https://qr.sepay.vn/img?acc=%s&bank=%s&amount=%.0f&des=%s&template=compact",
                sepayConfig.getAccountNumber(),
                sepayConfig.getBankCode(),
                amount,
                encodedContent);
    }

    /**
     * Kiểm tra xem giao dịch với nội dung cụ thể đã được thanh toán chưa.
     * Gọi API SePay để lấy danh sách giao dịch và tìm khớp content + amount.
     *
     * @param content        Nội dung chuyển khoản cần tìm
     * @param expectedAmount Số tiền kỳ vọng
     * @return true nếu đã nhận được tiền, false nếu chưa
     */
    public boolean checkPaymentStatus(String content, double expectedAmount) {
        try {
            String apiUrl = String.format(
                    "https://my.sepay.vn/userapi/transactions/list?account_number=%s&limit=20",
                    sepayConfig.getAccountNumber());

            URI uri = URI.create(apiUrl);
            HttpURLConnection conn = (HttpURLConnection) uri.toURL().openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Authorization", "Bearer " + sepayConfig.getApiToken());
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(10000);

            int status = conn.getResponseCode();

            if (status == 200) {
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();
                conn.disconnect();

                JsonNode root = objectMapper.readTree(response.toString());
                JsonNode transactions = root.get("transactions");

                if (transactions != null && transactions.isArray()) {
                    for (JsonNode tx : transactions) {
                        String txContent = tx.has("transaction_content")
                                ? tx.get("transaction_content").asText("")
                                : "";
                        double txAmount = tx.has("amount_in")
                                ? tx.get("amount_in").asDouble(0)
                                : 0;

                        // Kiểm tra nội dung chuyển khoản chứa mã đơn hàng và số tiền >= kỳ vọng
                        if (txContent.toUpperCase().contains(content.toUpperCase())
                                && txAmount >= expectedAmount) {
                            return true;
                        }
                    }
                }
            } else {
                log.warn("SePay API trả về status: {}", status);
                conn.disconnect();
            }

            return false;
        } catch (Exception e) {
            log.error("Lỗi kiểm tra thanh toán SePay: {}", e.getMessage());
            return false;
        }
    }

    public String getAccountNumber() {
        return sepayConfig.getAccountNumber();
    }

    public String getBankCode() {
        return sepayConfig.getBankCode();
    }

    public String getAccountName() {
        return sepayConfig.getAccountName();
    }
}

