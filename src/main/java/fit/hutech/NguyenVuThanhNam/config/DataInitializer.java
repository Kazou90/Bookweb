package fit.hutech.NguyenVuThanhNam.config;

import fit.hutech.NguyenVuThanhNam.entities.Book;
import fit.hutech.NguyenVuThanhNam.entities.Category;
import fit.hutech.NguyenVuThanhNam.entities.Coupon;
import fit.hutech.NguyenVuThanhNam.entities.Role;
import fit.hutech.NguyenVuThanhNam.entities.User;
import fit.hutech.NguyenVuThanhNam.repositories.IBookRepository;
import fit.hutech.NguyenVuThanhNam.repositories.ICategoryRepository;
import fit.hutech.NguyenVuThanhNam.repositories.ICouponRepository;
import fit.hutech.NguyenVuThanhNam.repositories.IRoleRepository;
import fit.hutech.NguyenVuThanhNam.repositories.IUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Khởi tạo dữ liệu mẫu khi ứng dụng chạy lần đầu.
 * Tạo role USER/ADMIN, tài khoản admin/user, danh mục sách mẫu và sách mẫu.
 */
@Configuration
@RequiredArgsConstructor
public class DataInitializer {
        private final IRoleRepository roleRepository;
        private final IUserRepository userRepository;
        private final ICategoryRepository categoryRepository;
        private final IBookRepository bookRepository;
        private final ICouponRepository couponRepository;
        private final PasswordEncoder passwordEncoder;

        @Bean
        public CommandLineRunner initData() {
                return args -> {
                        // ====== TẠO ROLE ======
                        Role userRole = roleRepository.findByName("USER").orElseGet(() -> {
                                Role role = new Role();
                                role.setName("USER");
                                return roleRepository.save(role);
                        });

                        Role adminRole = roleRepository.findByName("ADMIN").orElseGet(() -> {
                                Role role = new Role();
                                role.setName("ADMIN");
                                return roleRepository.save(role);
                        });

                        // ====== TẠO TÀI KHOẢN ADMIN (admin/admin123) ======
                        if (userRepository.findByUsername("admin").isEmpty()) {
                                User admin = new User();
                                admin.setUsername("admin");
                                admin.setPassword(passwordEncoder.encode("admin123"));
                                admin.setEmail("admin@bookstore.com");
                                admin.setPhone("0901234567");
                                Set<Role> adminRoles = new HashSet<>();
                                adminRoles.add(adminRole);
                                adminRoles.add(userRole);
                                admin.setRoles(adminRoles);
                                userRepository.save(admin);
                        }

                        // ====== TẠO TÀI KHOẢN USER MẪU (user1/user123) ======
                        if (userRepository.findByUsername("user1").isEmpty()) {
                                User user1 = new User();
                                user1.setUsername("user1");
                                user1.setPassword(passwordEncoder.encode("user123"));
                                user1.setEmail("user1@bookstore.com");
                                user1.setPhone("0912345678");
                                Set<Role> user1Roles = new HashSet<>();
                                user1Roles.add(userRole);
                                user1.setRoles(user1Roles);
                                userRepository.save(user1);
                        }

                        // ====== TẠO TÀI KHOẢN USER MẪU (user2/user123) ======
                        if (userRepository.findByUsername("user2").isEmpty()) {
                                User user2 = new User();
                                user2.setUsername("user2");
                                user2.setPassword(passwordEncoder.encode("user123"));
                                user2.setEmail("user2@bookstore.com");
                                user2.setPhone("0923456789");
                                Set<Role> user2Roles = new HashSet<>();
                                user2Roles.add(userRole);
                                user2.setRoles(user2Roles);
                                userRepository.save(user2);
                        }

                        // ====== TẠO DANH MỤC SÁCH MẪU ======
                        Category catCNTT = findOrCreateCategory("Công nghệ thông tin");
                        Category catKinhTe = findOrCreateCategory("Kinh tế");
                        Category catVanHoc = findOrCreateCategory("Văn học");
                        Category catKhoaHoc = findOrCreateCategory("Khoa học");
                        Category catNgoaiNgu = findOrCreateCategory("Ngoại ngữ");
                        Category catKyNang = findOrCreateCategory("Kỹ năng sống");
                        Category catLichSu = findOrCreateCategory("Lịch sử");
                        Category catThieuNhi = findOrCreateCategory("Thiếu nhi");

                        // ====== TẠO SÁCH MẪU (chỉ seed nếu chưa có sách) ======
                        if (bookRepository.count() == 0) {
                                // Sách Công nghệ thông tin
                                bookRepository.save(Book.builder()
                                                .title("Lập trình Java cơ bản đến nâng cao")
                                                .author("Nguyễn Văn An")
                                                .price(185000.0)
                                                .category(catCNTT)
                                                .build());

                                bookRepository.save(Book.builder()
                                                .title("Spring Boot trong thực tế")
                                                .author("Trần Minh Tuấn")
                                                .price(250000.0)
                                                .category(catCNTT)
                                                .build());

                                bookRepository.save(Book.builder()
                                                .title("Cấu trúc dữ liệu và giải thuật")
                                                .author("Lê Hoàng Phúc")
                                                .price(195000.0)
                                                .category(catCNTT)
                                                .build());

                                bookRepository.save(Book.builder()
                                                .title("Thiết kế cơ sở dữ liệu với MySQL")
                                                .author("Phạm Thị Hoa")
                                                .price(165000.0)
                                                .category(catCNTT)
                                                .build());

                                bookRepository.save(Book.builder()
                                                .title("Python cho người mới bắt đầu")
                                                .author("Võ Đình Khoa")
                                                .price(145000.0)
                                                .category(catCNTT)
                                                .build());

                                // Sách Kinh tế
                                bookRepository.save(Book.builder()
                                                .title("Kinh tế học vi mô")
                                                .author("Trần Thanh Hải")
                                                .price(220000.0)
                                                .category(catKinhTe)
                                                .build());

                                bookRepository.save(Book.builder()
                                                .title("Marketing căn bản")
                                                .author("Nguyễn Thị Lan")
                                                .price(175000.0)
                                                .category(catKinhTe)
                                                .build());

                                bookRepository.save(Book.builder()
                                                .title("Quản trị tài chính doanh nghiệp")
                                                .author("Lê Văn Bình")
                                                .price(280000.0)
                                                .category(catKinhTe)
                                                .build());

                                // Sách Văn học
                                bookRepository.save(Book.builder()
                                                .title("Truyện Kiều - Nguyễn Du")
                                                .author("Nguyễn Du")
                                                .price(95000.0)
                                                .category(catVanHoc)
                                                .build());

                                bookRepository.save(Book.builder()
                                                .title("Số đỏ")
                                                .author("Vũ Trọng Phụng")
                                                .price(85000.0)
                                                .category(catVanHoc)
                                                .build());

                                bookRepository.save(Book.builder()
                                                .title("Tắt đèn")
                                                .author("Ngô Tất Tố")
                                                .price(75000.0)
                                                .category(catVanHoc)
                                                .build());

                                bookRepository.save(Book.builder()
                                                .title("Dế Mèn phiêu lưu ký")
                                                .author("Tô Hoài")
                                                .price(65000.0)
                                                .category(catVanHoc)
                                                .build());

                                // Sách Khoa học
                                bookRepository.save(Book.builder()
                                                .title("Vật lý đại cương")
                                                .author("Trần Quốc Việt")
                                                .price(210000.0)
                                                .category(catKhoaHoc)
                                                .build());

                                bookRepository.save(Book.builder()
                                                .title("Hóa học hữu cơ")
                                                .author("Nguyễn Minh Đức")
                                                .price(190000.0)
                                                .category(catKhoaHoc)
                                                .build());

                                // Sách Ngoại ngữ
                                bookRepository.save(Book.builder()
                                                .title("TOEIC 990 - Chiến lược luyện thi")
                                                .author("Phạm Tuấn Anh")
                                                .price(320000.0)
                                                .category(catNgoaiNgu)
                                                .build());

                                bookRepository.save(Book.builder()
                                                .title("Ngữ pháp tiếng Anh toàn tập")
                                                .author("Lê Thu Hương")
                                                .price(250000.0)
                                                .category(catNgoaiNgu)
                                                .build());

                                bookRepository.save(Book.builder()
                                                .title("Minna no Nihongo - Tiếng Nhật cơ bản")
                                                .author("3A Corporation")
                                                .price(350000.0)
                                                .category(catNgoaiNgu)
                                                .build());

                                // Sách Kỹ năng sống
                                bookRepository.save(Book.builder()
                                                .title("Đắc nhân tâm")
                                                .author("Dale Carnegie")
                                                .price(108000.0)
                                                .category(catKyNang)
                                                .build());

                                bookRepository.save(Book.builder()
                                                .title("7 thói quen hiệu quả")
                                                .author("Stephen R. Covey")
                                                .price(135000.0)
                                                .category(catKyNang)
                                                .build());

                                bookRepository.save(Book.builder()
                                                .title("Nghĩ giàu làm giàu")
                                                .author("Napoleon Hill")
                                                .price(125000.0)
                                                .category(catKyNang)
                                                .build());

                                // Sách Lịch sử
                                bookRepository.save(Book.builder()
                                                .title("Lịch sử Việt Nam")
                                                .author("Trần Trọng Kim")
                                                .price(195000.0)
                                                .category(catLichSu)
                                                .build());

                                bookRepository.save(Book.builder()
                                                .title("Đại Việt sử ký toàn thư")
                                                .author("Ngô Sĩ Liên")
                                                .price(450000.0)
                                                .category(catLichSu)
                                                .build());

                                // Sách Thiếu nhi
                                bookRepository.save(Book.builder()
                                                .title("Doraemon - Tập 1")
                                                .author("Fujiko F. Fujio")
                                                .price(25000.0)
                                                .category(catThieuNhi)
                                                .build());

                                bookRepository.save(Book.builder()
                                                .title("Conan - Tập 1")
                                                .author("Gosho Aoyama")
                                                .price(25000.0)
                                                .category(catThieuNhi)
                                                .build());

                                bookRepository.save(Book.builder()
                                                .title("Shin - Cậu bé bút chì - Tập 1")
                                                .author("Yoshito Usui")
                                                .price(22000.0)
                                                .category(catThieuNhi)
                                                .build());
                        }

                        // ====== TẠO MÃ GIẢM GIÁ MẪU ======
                        if (couponRepository.count() == 0) {
                                Coupon c1 = new Coupon();
                                c1.setCode("WELCOME10");
                                c1.setDescription("Chào mừng khách hàng mới - Giảm 10%");
                                c1.setDiscountPercent(10.0);
                                c1.setMaxDiscount(30000.0);
                                c1.setMinOrderAmount(null);
                                c1.setStartDate(java.time.LocalDateTime.now());
                                c1.setEndDate(java.time.LocalDateTime.now().plusMonths(3));
                                c1.setUsageLimit(100);
                                c1.setUsedCount(0);
                                c1.setActive(true);
                                couponRepository.save(c1);

                                Coupon c2 = new Coupon();
                                c2.setCode("SALE20");
                                c2.setDescription("Giảm 20% cho đơn từ 200k");
                                c2.setDiscountPercent(20.0);
                                c2.setMaxDiscount(50000.0);
                                c2.setMinOrderAmount(200000.0);
                                c2.setStartDate(java.time.LocalDateTime.now());
                                c2.setEndDate(java.time.LocalDateTime.now().plusMonths(1));
                                c2.setUsageLimit(50);
                                c2.setUsedCount(0);
                                c2.setActive(true);
                                couponRepository.save(c2);

                                Coupon c3 = new Coupon();
                                c3.setCode("VIP50");
                                c3.setDescription("Ưu đãi VIP - Giảm 50% cho đơn từ 500k");
                                c3.setDiscountPercent(50.0);
                                c3.setMaxDiscount(100000.0);
                                c3.setMinOrderAmount(500000.0);
                                c3.setStartDate(java.time.LocalDateTime.now());
                                c3.setEndDate(java.time.LocalDateTime.now().plusMonths(6));
                                c3.setUsageLimit(20);
                                c3.setUsedCount(0);
                                c3.setActive(true);
                                couponRepository.save(c3);
                        }
                };
        }

        /**
         * Tìm danh mục theo tên, nếu chưa có thì tạo mới.
         */
        private Category findOrCreateCategory(String name) {
                List<Category> allCategories = categoryRepository.findAll();
                Optional<Category> existing = allCategories.stream()
                                .filter(c -> c.getName().equals(name))
                                .findFirst();
                return existing.orElseGet(() -> categoryRepository.save(Category.builder().name(name).build()));
        }
}

