package pe.edu.vallegrande.importaciones_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SaleDetailResponseDTO {
    private Long saleDetailId;
    private int quantity;
    private double unitPrice;
    private double subtotal;

    private Long productId;
    private String productName;
    private String productDescription;

    private Long saleId;
}