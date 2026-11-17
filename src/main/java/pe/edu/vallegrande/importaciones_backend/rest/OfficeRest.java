package pe.edu.vallegrande.importaciones_backend.rest;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pe.edu.vallegrande.importaciones_backend.model.Office;
import pe.edu.vallegrande.importaciones_backend.service.OfficeService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/office")
@CrossOrigin(origins = "*")
@AllArgsConstructor
public class OfficeRest {

     private final OfficeService officeService;

     @GetMapping("/active")
     @PreAuthorize("hasAnyRole('Administrador', 'Vendedor')")
     public ResponseEntity<List<Office>> listAllActiveOffices() {
          return ResponseEntity.ok(officeService.listAllOffices());
     }

     @GetMapping("/all")
     @PreAuthorize("hasAnyRole('Administrador', 'Vendedor')")
     public ResponseEntity<List<Office>> listAllOfficesIncludingInactive() {
          return ResponseEntity.ok(officeService.listAllOfficesIncludingInactive());
     }

     @GetMapping("/status/{status}")
     @PreAuthorize("hasAnyRole('Administrador', 'Vendedor')")
     public ResponseEntity<List<Office>> listOfficesByStatus(@PathVariable String status) {
          return ResponseEntity.ok(officeService.listOfficesByStatus(status));
     }

     @GetMapping("/{id}")
     @PreAuthorize("hasAnyRole('Administrador', 'Vendedor')")
     public ResponseEntity<Office> getOfficeById(@PathVariable Long id) {
          Optional<Office> office = officeService.getOfficeById(id);
          return office.map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.notFound().build());
     }

     @PostMapping("/create")
     @PreAuthorize("hasAnyRole('Administrador', 'Vendedor')")
     public ResponseEntity<Office> createOffice(@RequestBody Office office) {
          Office savedOffice = officeService.saveOffice(office);
          return ResponseEntity.ok(savedOffice);
     }

     @PutMapping("/update")
     @PreAuthorize("hasAnyRole('Administrador', 'Vendedor')")
     public ResponseEntity<Office> updateOffice(@RequestBody Office office) {
          Office updatedOffice = officeService.updateOffice(office);
          return ResponseEntity.ok(updatedOffice);
     }

     @PutMapping("/logicalDelete/{id}")
     @PreAuthorize("hasAnyRole('Administrador', 'Vendedor')")
     public ResponseEntity<Void> desactivateOffice(@PathVariable Long id) {
          officeService.deleteOffice(id);
          return ResponseEntity.noContent().build();
     }

     @PatchMapping("/restore/{id}")
     @PreAuthorize("hasAnyRole('Administrador', 'Vendedor')")
     public ResponseEntity<Void> restoreOffice(@PathVariable Long id) {
          officeService.restoreOffice(id);
          return ResponseEntity.noContent().build();
     }

     @DeleteMapping("/permanent/{id}")
     @PreAuthorize("hasAnyRole('Administrador', 'Vendedor')")
     public ResponseEntity<Void> permanentlyDeleteOffice(@PathVariable Long id) {
          officeService.permanentlyDeleteOffice(id);
          return ResponseEntity.noContent().build();
     }
}
