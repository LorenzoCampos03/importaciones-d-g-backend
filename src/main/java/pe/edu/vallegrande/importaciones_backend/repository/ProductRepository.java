package pe.edu.vallegrande.importaciones_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pe.edu.vallegrande.importaciones_backend.model.Product;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query(value = """
                SELECT
                            p.product_id,
                            p.name,
                            p.description,
                            p.unit_price,
                            p.stock,
                            p.status,
                            p.category_id,
                            p.supplier_id,
                            p.brand_id,
                            c.category_name,
                            s.supplier_name,
                            b.brand_name,
                            p.registration_date
                        FROM lorenzo_campos.product p
                        JOIN lorenzo_campos.category c ON p.category_id = c.category_id
                        JOIN lorenzo_campos.supplier s ON p.supplier_id = s.supplier_id
                        JOIN lorenzo_campos.brand b ON p.brand_id = b.brand_id
            """, nativeQuery = true)
    List<Object[]> findAllWithNames();

    @Query(value = """
                SELECT
                            p.product_id,
                            p.name,
                            p.description,
                            p.unit_price,
                            p.stock,
                            p.status,
                            p.category_id,
                            p.supplier_id,
                            p.brand_id,
                            c.category_name,
                            s.supplier_name,
                            b.brand_name,
                            p.registration_date
                        FROM lorenzo_campos.product p
                        JOIN lorenzo_campos.category c ON p.category_id = c.category_id
                        JOIN lorenzo_campos.supplier s ON p.supplier_id = s.supplier_id
                        JOIN lorenzo_campos.brand b ON p.brand_id = b.brand_id
                        WHERE p.product_id = :productId
            """, nativeQuery = true)
    List<Object[]> findByIdWithNames(@Param("productId") Long productId);
}
