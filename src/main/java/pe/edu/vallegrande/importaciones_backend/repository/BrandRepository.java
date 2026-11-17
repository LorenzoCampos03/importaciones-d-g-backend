package pe.edu.vallegrande.importaciones_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.vallegrande.importaciones_backend.model.Brand;

public interface BrandRepository extends JpaRepository<Brand, Long> {
    Brand findByBrandName(String brandName);
}
