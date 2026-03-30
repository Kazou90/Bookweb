package fit.hutech.NguyenVuThanhNam.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "invoices")
public class Invoice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Tên khách hàng không được để trống")
    private String customerName;

    private String phone;
    private String email;
    private String address;

    @Builder.Default
    private LocalDateTime orderDate = LocalDateTime.now();

    private Double totalPrice;

    @Builder.Default
    private String status = "PENDING"; // PENDING, PAID, SHIPPING, COMPLETED, CANCELLED

    // Liên kết đơn hàng với user (cho phép null nếu user chưa đăng nhập)
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = true)
    private User user;

    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL)
    @JsonIgnore
    @ToString.Exclude
    private List<ItemInvoice> items;
}

