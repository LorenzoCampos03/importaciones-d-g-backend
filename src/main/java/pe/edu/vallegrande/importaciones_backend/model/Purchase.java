package pe.edu.vallegrande.importaciones_backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "purchase", schema = "LORENZO_CAMPOS")
public class Purchase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "purchase_id")
    private Long purchaseId;

    @Column(name = "employee_id")
    private Long employeeId;

    @Column(name = "supplier_id")
    private Long supplierId;

    @Column(name = "purchase_date")
    private LocalDate purchaseDate;

    @Column(name = "status")
    private String status;

    @Column(name = "total_cost")
    private Double totalCost;

    // Relaciones (NO mapeamos directamente con User para simplificar)
    @Transient
    private String userName;

    @OneToMany(mappedBy = "purchase", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PurchaseDetail> details;
}
