package pe.edu.vallegrande.importaciones_backend.rest;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.vallegrande.importaciones_backend.service.PasswordMigrationService;

@RestController
@RequestMapping("/api/v1/migration")
@CrossOrigin(origins = "*")
@AllArgsConstructor
public class MigrationRest {

     private final PasswordMigrationService passwordMigrationService;

     @PostMapping("/migrate-passwords")
     public ResponseEntity<String> migratePasswords() {
          try {
               passwordMigrationService.migratePasswords();
               return ResponseEntity.ok("Migración de contraseñas completada exitosamente");
          } catch (Exception e) {
               return ResponseEntity.internalServerError()
                         .body("Error durante la migración: " + e.getMessage());
          }
     }
}
