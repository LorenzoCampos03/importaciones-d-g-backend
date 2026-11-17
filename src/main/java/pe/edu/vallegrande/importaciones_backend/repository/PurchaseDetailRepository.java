package pe.edu.vallegrande.importaciones_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.edu.vallegrande.importaciones_backend.model.PurchaseDetail;

import java.util.List;

@Repository
public interface PurchaseDetailRepository extends JpaRepository<PurchaseDetail, Long> {

    // Opcional: obtener detalles por id de compra
    List<PurchaseDetail> findByPurchase_PurchaseId(Long purchaseId);
}
