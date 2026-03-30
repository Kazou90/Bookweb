package fit.hutech.NguyenVuThanhNam.repositories;

import fit.hutech.NguyenVuThanhNam.entities.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ICategoryRepository extends JpaRepository<Category, Long> {
}

