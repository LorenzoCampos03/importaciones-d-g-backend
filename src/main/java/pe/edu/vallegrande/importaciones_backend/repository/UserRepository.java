package pe.edu.vallegrande.importaciones_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pe.edu.vallegrande.importaciones_backend.model.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query("SELECT u FROM User u WHERE u.role = :role AND u.status = :status ORDER BY u.user_id ASC")
    List<User> findAllActiveClientsOrderedById(@Param("role") String role, @Param("status") String status);

    @Query("SELECT u FROM User u WHERE u.role = :role ORDER BY u.user_id ASC")
    List<User> findAllClientsIncludingInactiveOrderedById(@Param("role") String role);

    @Query("SELECT u FROM User u WHERE u.role = :role AND u.status = :status ORDER BY u.user_id ASC")
    List<User> findClientsByStatusOrderedById(@Param("role") String role, @Param("status") String status);

    @Query("SELECT u FROM User u WHERE u.user_id = :userId AND u.role = :role")
    Optional<User> findByUser_idAndRole(@Param("userId") Long userId, @Param("role") String role);

    @Query("SELECT u FROM User u WHERE u.username = :username")
    Optional<User> findByUsername(@Param("username") String username);

    @Query("SELECT u FROM User u WHERE u.username = :username AND u.status = :status")
    Optional<User> findByUsernameAndStatus(@Param("username") String username, @Param("status") String status);

    @Query("SELECT u FROM User u WHERE u.email = :email")
    Optional<User> findByEmail(@Param("email") String email);

    @Query("SELECT u FROM User u WHERE u.email = :email AND u.status = :status")
    Optional<User> findByEmailAndStatus(@Param("email") String email, @Param("status") String status);

    // Método para verificar si existe un username
    @Query("SELECT COUNT(u) > 0 FROM User u WHERE u.username = :username")
    boolean existsByUsername(@Param("username") String username);
    
    // Método para verificar si existe un email
    @Query("SELECT COUNT(u) > 0 FROM User u WHERE u.email = :email")
    boolean existsByEmail(@Param("email") String email);

    // Método para buscar por documento
    @Query("SELECT u FROM User u WHERE u.document_type = :documentType AND u.document_number = :documentNumber")
    Optional<User> findByDocumentTypeAndDocumentNumber(@Param("documentType") String documentType,
            @Param("documentNumber") String documentNumber);

    // Método para buscar por teléfono
    @Query("SELECT u FROM User u WHERE u.phone = :phone")
    Optional<User> findByPhone(@Param("phone") String phone);
}
