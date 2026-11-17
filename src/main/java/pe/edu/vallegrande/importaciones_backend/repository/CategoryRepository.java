package pe.edu.vallegrande.importaciones_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.vallegrande.importaciones_backend.model.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    Category findByCategoryName(String categoryName);
}
