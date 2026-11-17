package pe.edu.vallegrande.importaciones_backend.repository;

import pe.edu.vallegrande.importaciones_backend.model.Office;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OfficeRepository extends JpaRepository<Office, Long> {

     @Query("SELECT o FROM Office o ORDER BY o.office_id ASC")
     List<Office> findAllOrderedById();

     @Query("SELECT o FROM Office o WHERE o.status = :status ORDER BY o.office_id ASC")
     List<Office> findByStatusOrderedById(@Param("status") String status);

     @Query("SELECT o FROM Office o WHERE o.office_id = :officeId")
     Optional<Office> findByOffice_id(@Param("officeId") Long officeId);
}
