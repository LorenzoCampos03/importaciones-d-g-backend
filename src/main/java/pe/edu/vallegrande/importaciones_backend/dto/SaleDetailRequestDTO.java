package pe.edu.vallegrande.importaciones_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SaleDetailRequestDTO {
    private Long productId;
    private int quantity;

}