package pe.edu.vallegrande.importaciones_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {
    private Long productId;
    private String name;
    private String description;
    private double unitPrice;
    private Integer stock;
    private String status;
    private LocalDate registrationDate;

    private String categoryName;
    private String supplierName;
    private String brandName;

    private Long categoryId;
    private Long supplierId;
    private Long brandId;
}
