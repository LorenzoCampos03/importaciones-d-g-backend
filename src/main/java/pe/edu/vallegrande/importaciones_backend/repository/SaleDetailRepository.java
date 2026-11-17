package pe.edu.vallegrande.importaciones_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pe.edu.vallegrande.importaciones_backend.model.SaleDetail;

import java.util.List;

@Repository
public interface SaleDetailRepository extends JpaRepository<SaleDetail, Long> {

    @Query("SELECT sd FROM SaleDetail sd JOIN sd.sale s WHERE s.sale_id = :saleId")
    List<SaleDetail> findBySaleId(@Param("saleId") Long saleId);

    @Query("SELECT CASE WHEN COUNT(sd) > 0 THEN true ELSE false END FROM SaleDetail sd JOIN sd.sale s WHERE s.sale_id = :saleId")
    boolean existsBySaleId(@Param("saleId") Long saleId);
}
