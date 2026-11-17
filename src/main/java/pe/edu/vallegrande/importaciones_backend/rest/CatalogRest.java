    package pe.edu.vallegrande.importaciones_backend.rest;

    import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
    import org.springframework.web.bind.annotation.GetMapping;
    import org.springframework.web.bind.annotation.RequestMapping;
    import org.springframework.web.bind.annotation.RestController;
    import pe.edu.vallegrande.importaciones_backend.model.Brand;
    import pe.edu.vallegrande.importaciones_backend.model.Category;
    import pe.edu.vallegrande.importaciones_backend.model.Supplier;
    import pe.edu.vallegrande.importaciones_backend.repository.BrandRepository;
    import pe.edu.vallegrande.importaciones_backend.repository.CategoryRepository;
    import pe.edu.vallegrande.importaciones_backend.repository.SupplierRepository;

    import java.util.List;

    @CrossOrigin("*")
    @RestController
    @RequestMapping("/api/v1")
    public class CatalogRest {

        @Autowired
        private CategoryRepository categoryRepository;

        @Autowired
        private SupplierRepository supplierRepository;

        @Autowired
        private BrandRepository brandRepository;

        // Listar categorías
        @GetMapping("/categories")
        @PreAuthorize("hasAnyRole('Administrador', 'Vendedor')")
        public List<Category> getCategories() {
            return categoryRepository.findAll();
        }

        // Listar proveedores
        @GetMapping("/suppliers")
        @PreAuthorize("hasAnyRole('Administrador', 'Vendedor')")
        public List<Supplier> getSuppliers() {
            return supplierRepository.findAll();
        }

        // Listar marcas
        @GetMapping("/brands")
        @PreAuthorize("hasAnyRole('Administrador', 'Vendedor')")
        public List<Brand> getBrands() {
            return brandRepository.findAll();
        }
    }
