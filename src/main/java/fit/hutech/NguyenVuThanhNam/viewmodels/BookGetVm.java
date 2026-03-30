package fit.hutech.NguyenVuThanhNam.viewmodels;

import lombok.*;

/**
 * ViewModel cho GET response - Book (Bai 7: API ViewModels)
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BookGetVm {
    private Long id;
    private String title;
    private String author;
    private Double price;
    private String image;
    private String categoryName;
}

