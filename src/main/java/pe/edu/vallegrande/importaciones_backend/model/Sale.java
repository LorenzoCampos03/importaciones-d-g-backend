package pe.edu.vallegrande.importaciones_backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "sale", schema = "MARIA_SULCA")
public class Sale {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sale_id")
    private Long sale_id;

    @Column(name = "sale_date")
    private LocalDateTime sale_date;

    @Column(name = "total")
    private double total;

    @Column(name = "status")
    private String status = "Pendiente";

    @Column(name = "payment_method")
    private String payment_method;

    @Column(name = "delivery_method")
    private String delivery_method;

    @Column(name = "delivery_address")
    private String delivery_address;

    @OneToOne
    @JoinColumn(name = "client_id")
    private User clientId;

    @OneToOne
    @JoinColumn(name = "employee_id")
    private User employeeId;

    @OneToOne
    @JoinColumn(name = "office_id")
    private Office officeId;

    @OneToMany(mappedBy = "sale", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SaleDetail> saleDetails = new ArrayList<>();

}
