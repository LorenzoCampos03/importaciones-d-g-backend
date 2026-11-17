package pe.edu.vallegrande.importaciones_backend.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pe.edu.vallegrande.importaciones_backend.dto.SaleDetailRequestDTO;
import pe.edu.vallegrande.importaciones_backend.dto.SaleRequestDTO;
import pe.edu.vallegrande.importaciones_backend.dto.SaleResponseDTO;
import pe.edu.vallegrande.importaciones_backend.service.SaleService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/sale")
@CrossOrigin("*")
@RequiredArgsConstructor
public class SaleRest {

    private final SaleService saleService;

    // todas las ventas
    @GetMapping
    @PreAuthorize("hasAnyRole('Administrador', 'Vendedor')")
    public ResponseEntity<List<SaleResponseDTO>> getAllSales() {
        List<SaleResponseDTO> sales = saleService.getAllSales();
        return ResponseEntity.ok(sales);
    }

    // venta por su ID
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('Administrador', 'Vendedor', 'Cliente')")
    public ResponseEntity<SaleResponseDTO> getSaleById(@PathVariable Long id) {
        SaleResponseDTO sale = saleService.getSaleById(id);
        if (sale != null) {
            return ResponseEntity.ok(sale);
        }
        return ResponseEntity.notFound().build();
    }

    // nueva venta
    @PostMapping
    @PreAuthorize("hasAnyRole('Administrador', 'Vendedor')")
    public ResponseEntity<SaleResponseDTO> createSale(@RequestBody SaleRequestDTO saleRequestDTO) {
        SaleResponseDTO createdSale = saleService.createSale(saleRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdSale);
    }

    // Eliminar lógicamente una venta
    @DeleteMapping("/{id}")
    public ResponseEntity<SaleResponseDTO> deleteSale(@PathVariable Long id) {
        try {
            SaleResponseDTO deletedSale = saleService.deleteSaleLogically(id);
            return ResponseEntity.ok(deletedSale);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Restaurar una venta eliminada
    @PutMapping("/{id}/restore")
    @PreAuthorize("hasAnyRole('Administrador', 'Vendedor')")
    public ResponseEntity<SaleResponseDTO> restoreSale(@PathVariable Long id) {
        try {
            SaleResponseDTO restoredSale = saleService.restoreSale(id);
            return ResponseEntity.ok(restoredSale);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Actualizar estado de venta
    @PutMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('Administrador', 'Vendedor', 'Cliente')")
    public ResponseEntity<SaleResponseDTO> updateSaleStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        try {
            SaleResponseDTO updatedSale = saleService.updateSaleStatus(id, status);
            return ResponseEntity.ok(updatedSale);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Eliminar un detalle de venta
    @DeleteMapping("/{saleId}/details/{detailId}")
    @PreAuthorize("hasAnyRole('Administrador', 'Vendedor', 'Cliente')")
    public ResponseEntity<SaleResponseDTO> removeSaleDetail(
            @PathVariable Long saleId,
            @PathVariable Long detailId) {
        try {
            SaleResponseDTO updatedSale = saleService.removeSaleDetail(saleId, detailId);
            return ResponseEntity.ok(updatedSale);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Agregar detalle a una venta
    @PostMapping("/{saleId}/details")
    @PreAuthorize("hasAnyRole('Administrador', 'Vendedor', 'Cliente')")
    public ResponseEntity<SaleResponseDTO> addSaleDetail(
            @PathVariable Long saleId,
            @RequestBody SaleDetailRequestDTO detailDTO) {
        try {
            SaleResponseDTO updatedSale = saleService.addSaleDetail(saleId, detailDTO);
            return ResponseEntity.ok(updatedSale);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Actualizar cantidad de un detalle
    @PutMapping("/{saleId}/details/{detailId}")
    @PreAuthorize("hasAnyRole('Administrador', 'Vendedor', 'Cliente')")
    public ResponseEntity<SaleResponseDTO> updateSaleDetailQuantity(
            @PathVariable Long saleId,
            @PathVariable Long detailId,
            @RequestParam int quantity) {
        try {
            SaleResponseDTO updatedSale = saleService.updateSaleDetailQuantity(saleId, detailId, quantity);
            return ResponseEntity.ok(updatedSale);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Obtener ventas por estado
    @GetMapping("/status/{status}")
    @PreAuthorize("hasAnyRole('Administrador', 'Vendedor', 'Cliente')")
    public ResponseEntity<List<SaleResponseDTO>> getSalesByStatus(@PathVariable String status) {
        List<SaleResponseDTO> sales = saleService.getSalesByStatus(status);
        return ResponseEntity.ok(sales);
    }

    // Obtener ventas activas
    @GetMapping("/active")
    public ResponseEntity<List<SaleResponseDTO>> getActiveSales() {
        List<SaleResponseDTO> sales = saleService.getActiveSales();
        return ResponseEntity.ok(sales);
    } // Obtener ventas por cliente

    @GetMapping("/client/{clientId}")
    public ResponseEntity<List<SaleResponseDTO>> getSalesByClient(@PathVariable Long clientId) {
        try {
            List<SaleResponseDTO> sales = saleService.getSalesByClient(clientId);
            return ResponseEntity.ok(sales);
        } catch (RuntimeException e) {
            // Log del error para debugging
            System.err.println("Error al obtener ventas del cliente " + clientId + ": " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }
}
