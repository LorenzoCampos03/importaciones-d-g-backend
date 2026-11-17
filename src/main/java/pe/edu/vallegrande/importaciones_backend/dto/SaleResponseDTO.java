package pe.edu.vallegrande.importaciones_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SaleResponseDTO {
    private Long saleId;
    private LocalDateTime saleDate;
    private double total;
    private String status;
    private String paymentMethod;
    private String deliveryMethod;
    private String deliveryAddress;

    private Long clientId;
    private String clientName;

    private Long employeeId;
    private String employeeName;

    private Long officeId;
    private String officeName;

    private List<SaleDetailResponseDTO> saleDetails;
}