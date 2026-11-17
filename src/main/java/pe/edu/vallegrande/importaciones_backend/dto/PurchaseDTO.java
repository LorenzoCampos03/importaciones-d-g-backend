package pe.edu.vallegrande.importaciones_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseDTO {
    private Long purchaseId;
    private Long employeeId;    // Cambiado a employeeId
    private Long supplierId;
    private String userName;
    private LocalDate purchaseDate;
    private String status;
    private Double totalCost;
    private List<PurchaseDetailDTO> purchaseDetails;
}
