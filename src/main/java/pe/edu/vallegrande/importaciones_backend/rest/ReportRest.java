package pe.edu.vallegrande.importaciones_backend.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pe.edu.vallegrande.importaciones_backend.service.ReportService;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
public class ReportRest {

    private final ReportService reportService;

    @GetMapping("/sale/{sale_id}")
    @PreAuthorize("hasAnyRole('Administrador', 'Cliente', 'Vendedor')")
    public ResponseEntity<byte[]> generateJasperPdfReport(@PathVariable("sale_id") Long saleId) {
        try {
            byte[] pdf = reportService.generateReportSale(saleId);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=reporte_venta_" + saleId + ".pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(pdf);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/product")
    @PreAuthorize("hasAnyRole('Administrador', 'Cliente', 'Vendedor')")
    public ResponseEntity<byte[]> generateJasperPdfProduct() {
        try {
            byte[] pdf = reportService.generateReportProduct();
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=reporte_productos.pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(pdf);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/purchase/{purchase_id}")
    @PreAuthorize("hasAnyRole('Administrador', 'Cliente', 'Vendedor')")
    public ResponseEntity<byte[]> generateJasperPdfPurchase(@PathVariable("purchase_id") Long purchaseId) {
        try {
            System.out.println("=== DEBUG BACKEND PURCHASE REPORT ===");
            System.out.println("Purchase ID recibido: " + purchaseId);
            System.out.println("Tipo de dato: " + purchaseId.getClass().getSimpleName());
            System.out.println("====================================");
            byte[] pdf = reportService.generateReportPurchase(purchaseId);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=reporte_compra_" + purchaseId + ".pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(pdf);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }


}
