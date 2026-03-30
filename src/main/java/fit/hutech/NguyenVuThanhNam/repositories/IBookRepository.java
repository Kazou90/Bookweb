package fit.hutech.NguyenVuThanhNam.repositories;

import fit.hutech.NguyenVuThanhNam.entities.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IBookRepository extends JpaRepository<Book, Long> {
    List<Book> findByTitleContainingIgnoreCase(String keyword);

    Page<Book> findByTitleContainingIgnoreCase(String keyword, Pageable pageable);
}

