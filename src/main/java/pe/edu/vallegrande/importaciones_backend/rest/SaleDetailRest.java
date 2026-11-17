package pe.edu.vallegrande.importaciones_backend.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.vallegrande.importaciones_backend.dto.SaleDetailRequestDTO;
import pe.edu.vallegrande.importaciones_backend.dto.SaleDetailResponseDTO;
import pe.edu.vallegrande.importaciones_backend.service.SaleDetailService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/sale-details")
@CrossOrigin("*")
public class SaleDetailRest {

    @Autowired
    private SaleDetailService saleDetailService;

    /**
     * Obtener todos los detalles de venta
     */
    @GetMapping
    public ResponseEntity<List<SaleDetailResponseDTO>> getAllSaleDetails() {
        try {
            List<SaleDetailResponseDTO> saleDetails = saleDetailService.findAll();
            return ResponseEntity.ok(saleDetails);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Obtener detalles de venta por ID de venta (PRINCIPAL)
     */
    @GetMapping("/sale/{saleId}")
    public ResponseEntity<List<SaleDetailResponseDTO>> getSaleDetailsBySaleId(@PathVariable Long saleId) {
        try {
            List<SaleDetailResponseDTO> saleDetails = saleDetailService.findBySaleId(saleId);
            if (saleDetails.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(saleDetails);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Obtener un detalle de venta por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<SaleDetailResponseDTO> getSaleDetailById(@PathVariable Long id) {
        try {
            return saleDetailService.findById(id)
                    .map(saleDetail -> ResponseEntity.ok(saleDetail))
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Crear un nuevo detalle de venta
     */
    @PostMapping("/sale/{saleId}")
    public ResponseEntity<SaleDetailResponseDTO> createSaleDetail(
            @PathVariable Long saleId,
            @RequestBody SaleDetailRequestDTO saleDetailDTO) {
        try {
            // Validaciones básicas
            if (saleDetailDTO.getQuantity() <= 0) {
                return ResponseEntity.badRequest().build();
            }

            if (saleDetailDTO.getProductId() == null) {
                return ResponseEntity.badRequest().build();
            }

            SaleDetailResponseDTO createdDetail = saleDetailService.save(saleDetailDTO, saleId);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdDetail);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Actualizar un detalle de venta
     */
    @PutMapping("/{id}")
    public ResponseEntity<SaleDetailResponseDTO> updateSaleDetail(
            @PathVariable Long id,
            @RequestBody SaleDetailRequestDTO saleDetailDTO) {
        try {
            if (saleDetailDTO.getQuantity() <= 0) {
                return ResponseEntity.badRequest().build();
            }

            return saleDetailService.update(id, saleDetailDTO)
                    .map(updatedDetail -> ResponseEntity.ok(updatedDetail))
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Eliminar un detalle de venta (físico)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSaleDetail(@PathVariable Long id) {
        try {
            if (saleDetailService.deleteById(id)) {
                return ResponseEntity.noContent().build();
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


}