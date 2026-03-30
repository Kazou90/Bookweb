package fit.hutech.NguyenVuThanhNam.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Cấu hình SePay Payment Gateway.
 * Thay đổi thông tin trong application.properties.
 */
@Configuration
@ConfigurationProperties(prefix = "sepay")
@Getter
@Setter
public class SepayConfig {
    // API Token dùng để gọi API kiểm tra giao dịch
    private String apiToken;

    // Số tài khoản ngân hàng nhận tiền
    private String accountNumber;

    // Mã ngân hàng (VD: MBBank, Vietcombank, ACB, TP Bank, v.v.)
    private String bankCode;

    // Tên chủ tài khoản (hiển thị cho người dùng)
    private String accountName;
}

