package pe.edu.vallegrande.importaciones_backend.rest;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pe.edu.vallegrande.importaciones_backend.model.User;
import pe.edu.vallegrande.importaciones_backend.service.EmployeeService;

import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "*")
@AllArgsConstructor
@RequestMapping("/api/v1/employee")
public class EmployeeRest {

    private EmployeeService employeeService;

    // Listar todos los vendedores activos - Solo Admin
    @GetMapping("/listEmployee")
    @PreAuthorize("hasRole('Administrador')")
    public ResponseEntity<List<User>> listAllEmployees() {
        return ResponseEntity.ok(employeeService.listAllEmployees());
    }

    // Listar todos los vendedores (activos e inactivos) - Solo Admin
    @GetMapping("/all")
    @PreAuthorize("hasRole('Administrador')")
    public ResponseEntity<List<User>> listAllEmployeesIncludingInactive() {
        return ResponseEntity.ok(employeeService.listAllEmployeesIncludingInactive());
    }

    // Obtener vendedor por ID - Admin y Vendedor (pueden ver su propia info)
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('Administrador', 'Vendedor')")
    public ResponseEntity<User> getEmployeeById(@PathVariable Long id) {
        Optional<User> employee = employeeService.getEmployeeById(id);
        return employee.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Crear nuevo vendedor - Solo Admin
    @PostMapping("/saveEmployee")
    @PreAuthorize("hasRole('Administrador')")
    public ResponseEntity<?> saveEmployee(@RequestBody User user) {
        try {
            User savedEmployee = employeeService.saveEmployee(user);
            savedEmployee.setPassword(null);
            return ResponseEntity.ok(savedEmployee);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(new ErrorResponse("Error interno del servidor"));
        }
    }

    // Actualizar vendedor - Solo Admin
    @PutMapping("/update")
    @PreAuthorize("hasRole('Administrador')")
    public ResponseEntity<?> updateEmployee(@RequestBody User user) {
        try {
            User updatedEmployee = employeeService.updateEmployee(user);
            updatedEmployee.setPassword(null);
            return ResponseEntity.ok(updatedEmployee);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(new ErrorResponse("Error al actualizar vendedor"));
        }
    }

    // Actualización parcial - Solo Admin
    @PatchMapping("/partialUpdate/{id}")
    @PreAuthorize("hasRole('Administrador')")
    public ResponseEntity<?> partialUpdateEmployee(
            @PathVariable Long id,
            @RequestBody User partialUser) {
        try {
            User updatedEmployee = employeeService.updatePartialEmployee(id, partialUser);
            updatedEmployee.setPassword(null);
            return ResponseEntity.ok(updatedEmployee);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Eliminación lógica - Solo Admin
    @PutMapping("/logicalDelete/{id}")
    @PreAuthorize("hasRole('Administrador')")
    public ResponseEntity<Void> deactivateEmployee(@PathVariable Long id) {
        employeeService.deleteEmployee(id);
        return ResponseEntity.noContent().build();
    }

    // Restauración lógica - Solo Admin
    @PatchMapping("/restore/{id}")
    @PreAuthorize("hasRole('Administrador')")
    public ResponseEntity<Void> restoreEmployee(@PathVariable Long id) {
        employeeService.restoreEmployee(id);
        return ResponseEntity.noContent().build();
    }

    // Eliminación física - Solo Admin
    @DeleteMapping("/permanent/{id}")
    @PreAuthorize("hasRole('Administrador')")
    public ResponseEntity<Void> permanentlyDeleteEmployee(@PathVariable Long id) {
        employeeService.permanentlyDeleteEmployee(id);
        return ResponseEntity.noContent().build();
    }

    public static class ErrorResponse {
        private String message;

        public ErrorResponse(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}
