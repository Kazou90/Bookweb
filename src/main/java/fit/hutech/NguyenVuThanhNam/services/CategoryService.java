package fit.hutech.NguyenVuThanhNam.services;

import fit.hutech.NguyenVuThanhNam.entities.Book;
import fit.hutech.NguyenVuThanhNam.entities.Category;
import fit.hutech.NguyenVuThanhNam.repositories.IBookRepository;
import fit.hutech.NguyenVuThanhNam.repositories.ICategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class CategoryService {
    private final ICategoryRepository categoryRepository;
    private final IBookRepository bookRepository;

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public Optional<Category> getCategoryById(Long id) {
        return categoryRepository.findById(id);
    }

    public void addCategory(Category category) {
        categoryRepository.save(category);
    }

    public void updateCategory(Category category) {
        categoryRepository.save(category);
    }

    /**
     * Xóa danh mục: sách trong danh mục sẽ KHÔNG bị xóa,
     * mà chỉ set category = null (hiển thị "Chưa có danh mục")
     */
    public void deleteCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Danh mục không tồn tại: " + id));

        // Set null category cho tất cả sách thuộc danh mục này
        if (category.getBooks() != null) {
            for (Book book : category.getBooks()) {
                book.setCategory(null);
                bookRepository.save(book);
            }
        }

        categoryRepository.deleteById(id);
    }
}

