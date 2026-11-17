package pe.edu.vallegrande.importaciones_backend.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.vallegrande.importaciones_backend.dto.PurchaseDTO;
import pe.edu.vallegrande.importaciones_backend.service.PurchaseService;

import java.util.List;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/v1/purchases")
@RequiredArgsConstructor
public class PurchaseRest {

    private final PurchaseService purchaseService;

    @GetMapping
    public ResponseEntity<?> getAllPurchases() {
        try {
            return ResponseEntity.ok(purchaseService.getAllPurchases());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error al obtener las compras");
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getPurchaseById(@PathVariable Long id) {
        try {
            PurchaseDTO purchaseDTO = purchaseService.getPurchaseById(id);
            return ResponseEntity.ok(purchaseDTO);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body("Compra no encontrada: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error al obtener la compra: " + e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<?> createPurchase(@RequestBody PurchaseDTO purchaseDTO) {
        try {
            PurchaseDTO createdPurchase = purchaseService.createPurchaseWithDetails(purchaseDTO);
            return ResponseEntity.ok(createdPurchase);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace(); // Esto imprimirá el stack trace en la consola
            return ResponseEntity.status(500).body("Error al crear la compra: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updatePurchase(@PathVariable Long id, @RequestBody PurchaseDTO purchaseDTO) {
        try {
            PurchaseDTO updatedPurchase = purchaseService.updatePurchase(id, purchaseDTO);
            return ResponseEntity.ok(updatedPurchase);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error al actualizar la compra: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> cancelPurchase(@PathVariable("id") Long id) {
        try {
            purchaseService.cancelPurchase(id);
            return ResponseEntity.ok("Compra Cancelado exitosamente.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<PurchaseDTO>> getPurchasesByStatus(@PathVariable String status) {
        try {
            List<PurchaseDTO> purchases = purchaseService.getPurchasesByStatus(status);
            return ResponseEntity.ok(purchases);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

}
