// SaleRequestDTO.java
package pe.edu.vallegrande.importaciones_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SaleRequestDTO {
    private Long sale_id;
    private LocalDateTime sale_date;
    private double total;
    private String status;
    private String payment_method;
    private String delivery_method;
    private String delivery_address;
    private Long client_id;
    private Long employee_id;
    private Long office_id;
    private List<SaleDetailRequestDTO> saleDetails;
}