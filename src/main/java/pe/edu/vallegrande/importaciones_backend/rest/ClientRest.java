package pe.edu.vallegrande.importaciones_backend.rest;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pe.edu.vallegrande.importaciones_backend.model.User;
import pe.edu.vallegrande.importaciones_backend.service.ClientService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/client")
@CrossOrigin(origins = "*")
@AllArgsConstructor
public class ClientRest {

    private final ClientService clientService;

    // Listar todos los clientes activos - Solo Admin y Vendedor
    @GetMapping("/active")
    @PreAuthorize("hasAnyRole('Administrador', 'Vendedor')")
    public ResponseEntity<List<User>> listAllActiveClients() {
        return ResponseEntity.ok(clientService.listAllClients());
    }

    // Listar todos los clientes (activos e inactivos) - Solo Admin
    @GetMapping("/all")
    @PreAuthorize("hasRole('Administrador')")
    public ResponseEntity<List<User>> listAllClientsIncludingInactive() {
        return ResponseEntity.ok(clientService.listAllClientsIncludingInactive());
    }

    // Listar clientes por estado - Solo Admin y Vendedor
    @GetMapping("/status/{status}")
    @PreAuthorize("hasAnyRole('Administrador', 'Vendedor')")
    public ResponseEntity<List<User>> listClientsByStatus(@PathVariable String status) {
        return ResponseEntity.ok(clientService.listClientsByStatus(status));
    }

    // Obtener cliente por ID - Todos los roles autenticados
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('Cliente', 'Administrador', 'Vendedor')")
    public ResponseEntity<User> getClientById(@PathVariable Long id) {
        Optional<User> client = clientService.getClientById(id);
        return client.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Crear nuevo cliente - Solo Admin y Vendedor
    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('Administrador', 'Vendedor')")
    public ResponseEntity<User> createClient(@RequestBody User user) {
        User savedClient = clientService.saveClient(user);
        return ResponseEntity.ok(savedClient);
    } // Actualizar cliente existente - Solo Admin y Vendedor

    @PutMapping("/update")
    @PreAuthorize("hasAnyRole('Administrador', 'Vendedor')")
    public ResponseEntity<User> updateClient(@RequestBody User user) {
        User updatedClient = clientService.updateClient(user);
        return ResponseEntity.ok(updatedClient);
    }

    // Actualizar parcialmente - Solo Admin y Vendedor
    @PatchMapping("/partialUpdate/{id}")
    @PreAuthorize("hasAnyRole('Administrador', 'Vendedor', 'Cliente')")
    public ResponseEntity<User> partialUpdateClient(
            @PathVariable Long id,
            @RequestBody User partialUser) {
        try {
            User updatedClient = clientService.updatePartialClient(id, partialUser);
            return ResponseEntity.ok(updatedClient);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Eliminación lógica - Solo Admin
    @PutMapping("/logicalDelete/{id}")
    @PreAuthorize("hasRole('Administrador')")
    public ResponseEntity<Void> desactivateClient(@PathVariable Long id) {
        clientService.deleteClient(id);
        return ResponseEntity.noContent().build();
    }

    // Restauración lógica - Solo Admin
    @PatchMapping("/restore/{id}")
    @PreAuthorize("hasRole('Administrador')")
    public ResponseEntity<Void> restoreClient(@PathVariable Long id) {
        clientService.restoreClient(id);
        return ResponseEntity.noContent().build();
    }

    // Eliminación física - Solo Admin
    @DeleteMapping("/permanent/{id}")
    @PreAuthorize("hasRole('Administrador')")
    public ResponseEntity<Void> permanentlyDeleteClient(@PathVariable Long id) {
        clientService.permanentlyDeleteClient(id);
        return ResponseEntity.noContent().build();
    }
}
