package pe.edu.vallegrande.importaciones_backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "product", schema = "LORENZO_CAMPOS")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long productId;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "unit_price")
    private double unitPrice;

    @Column(name = "stock")
    private Integer stock;

    @Column(name = "status")
    private String status; // 'A' o 'I'

    @Column(name = "registration_date")
    private LocalDate registrationDate;

    // Campos adicionales para mostrar los nombres relacionados
    @Transient
    private String categoryName;

    @Transient
    private String supplierName;

    @Transient
    private String brandName;

    // Nuevas propiedades para los IDs de las relaciones
    @Column(name = "category_id")
    private Long categoryId; // ID de la categoría

    @Column(name = "supplier_id")
    private Long supplierId; // ID del proveedor

    @Column(name = "brand_id")
    private Long brandId; // ID de la marca
}
