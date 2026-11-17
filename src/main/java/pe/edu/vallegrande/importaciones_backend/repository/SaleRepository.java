package pe.edu.vallegrande.importaciones_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pe.edu.vallegrande.importaciones_backend.model.Office;
import pe.edu.vallegrande.importaciones_backend.model.Sale;
import pe.edu.vallegrande.importaciones_backend.model.User;

import java.util.List;

@Repository
public interface SaleRepository extends JpaRepository<Sale, Long> {
    List<Sale> findByClientId(User clientId);

    @Query(value = "SELECT * FROM MARIA_SULCA.sale WHERE client_id = :clientId", nativeQuery = true)
    List<Sale> findByClientIdUserId(@Param("clientId") Long clientId);

    List<Sale> findByEmployeeId(User employeeId);

    List<Sale> findByOfficeId(Office officeId);

    List<Sale> findByStatus(String status);

    List<Sale> findByStatusNot(String status);

}
