package pe.edu.vallegrande.importaciones_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseDetailDTO {
    private Long productId;
    private Integer quantity;
    private Double unitCost;
    private Double totalCost;
    private String productName;
}
