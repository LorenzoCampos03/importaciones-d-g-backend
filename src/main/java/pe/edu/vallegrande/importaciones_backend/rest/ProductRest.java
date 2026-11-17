package pe.edu.vallegrande.importaciones_backend.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import pe.edu.vallegrande.importaciones_backend.dto.ProductDTO;
import pe.edu.vallegrande.importaciones_backend.service.ProductService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
@CrossOrigin("*")
public class ProductRest {

    private final ProductService productService;

    @GetMapping
    @PreAuthorize("hasAnyRole('Administrador', 'Vendedor')")
    public List<ProductDTO> findAll() {
        return productService.findAll();
    }

    @GetMapping("/{productId}")
    @PreAuthorize("hasAnyRole('Administrador', 'Vendedor')")
    public ResponseEntity<ProductDTO> findById(@PathVariable Long productId) {
        return productService.findById(productId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/status/{status}")
    @PreAuthorize("hasAnyRole('Administrador', 'Vendedor')")
    public List<ProductDTO> findByStatus(@PathVariable String status) {
        return productService.findByStatus(status);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('Administrador', 'Vendedor')")
    public ResponseEntity<ProductDTO> create(@RequestBody ProductDTO productDTO) {
        ProductDTO created = productService.create(productDTO);
        return ResponseEntity.ok(created);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('Administrador', 'Vendedor')")
    public ResponseEntity<ProductDTO> update(@PathVariable Long id, @RequestBody ProductDTO productDTO) {
        productDTO.setProductId(id);
        ProductDTO updated = productService.update(productDTO);
        return ResponseEntity.ok(updated);
    }

    @PutMapping("/delete/{id}")
    @PreAuthorize("hasAnyRole('Administrador', 'Vendedor')")
    public ResponseEntity<Void> deleteLogic(@PathVariable Long id) {
        productService.deleteLogic(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/restore/{id}")
    @PreAuthorize("hasAnyRole('Administrador', 'Vendedor')")
    public ResponseEntity<Void> restore(@PathVariable Long id) {
        productService.restoreLogic(id);
        return ResponseEntity.noContent().build();
    }

    // Endpoints para clientes
    @GetMapping("/available")
    @PreAuthorize("hasAnyRole('Administrador', 'Vendedor', 'Cliente')")
    public List<ProductDTO> findAvailableProducts() {
        return productService.findByStatus("A")
                .stream()
                .filter(product -> product.getStock() > 0)
                .toList();
    }

    @GetMapping("/available/{productId}")
    @PreAuthorize("hasAnyRole('Administrador', 'Vendedor', 'Cliente')")
    public ResponseEntity<ProductDTO> findAvailableProductById(@PathVariable Long productId) {
        return productService.findById(productId)
                .filter(product -> "A".equals(product.getStatus()) && product.getStock() > 0)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
