package pe.edu.vallegrande.importaciones_backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pe.edu.vallegrande.importaciones_backend.enums.Constants;
import pe.edu.vallegrande.importaciones_backend.model.Product;
import pe.edu.vallegrande.importaciones_backend.model.Category;
import pe.edu.vallegrande.importaciones_backend.model.Supplier;
import pe.edu.vallegrande.importaciones_backend.model.Brand;
import pe.edu.vallegrande.importaciones_backend.repository.ProductRepository;
import pe.edu.vallegrande.importaciones_backend.repository.CategoryRepository;
import pe.edu.vallegrande.importaciones_backend.repository.SupplierRepository;
import pe.edu.vallegrande.importaciones_backend.repository.BrandRepository;
import pe.edu.vallegrande.importaciones_backend.dto.ProductDTO;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final SupplierRepository supplierRepository;
    private final BrandRepository brandRepository;

    private ProductDTO toDTO(Product product) {
        if (product == null)
            return null;
        ProductDTO dto = new ProductDTO();
        dto.setProductId(product.getProductId());
        dto.setName(product.getName());
        dto.setDescription(product.getDescription());
        dto.setUnitPrice(product.getUnitPrice());
        dto.setStock(product.getStock());
        dto.setStatus(product.getStatus());
        dto.setRegistrationDate(product.getRegistrationDate());
        dto.setCategoryName(product.getCategoryName());
        dto.setSupplierName(product.getSupplierName());
        dto.setBrandName(product.getBrandName());
        dto.setCategoryId(product.getCategoryId());
        dto.setSupplierId(product.getSupplierId());
        dto.setBrandId(product.getBrandId());
        return dto;
    }

    // Conversión DTO -> Entity
    private Product toEntity(ProductDTO dto) {
        if (dto == null)
            return null;
        Product product = new Product();
        product.setProductId(dto.getProductId());
        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setUnitPrice(dto.getUnitPrice());
        product.setStock(dto.getStock());
        product.setStatus(dto.getStatus());
        product.setRegistrationDate(dto.getRegistrationDate());
        product.setCategoryName(dto.getCategoryName());
        product.setSupplierName(dto.getSupplierName());
        product.setBrandName(dto.getBrandName());
        product.setCategoryId(dto.getCategoryId());
        product.setSupplierId(dto.getSupplierId());
        product.setBrandId(dto.getBrandId());
        return product;
    }

    public List<ProductDTO> findAll() {
        List<Object[]> rawData = productRepository.findAllWithNames();
        List<Product> products = new ArrayList<>();

        for (Object[] row : rawData) {
            Product product = new Product();
            product.setProductId(((Number) row[0]).longValue());
            product.setName((String) row[1]);
            product.setDescription((String) row[2]);
            product.setUnitPrice(((BigDecimal) row[3]).doubleValue());
            product.setStock(((BigDecimal) row[4]).intValue());
            product.setStatus(((Character) row[5]).toString());

            // IDs de relaciones
            product.setCategoryId(((Number) row[6]).longValue());
            product.setSupplierId(((Number) row[7]).longValue());
            product.setBrandId(((Number) row[8]).longValue());

            // Nombres de relaciones
            product.setCategoryName((String) row[9]);
            product.setSupplierName((String) row[10]);
            product.setBrandName((String) row[11]);

            product.setRegistrationDate(((java.sql.Timestamp) row[12]).toLocalDateTime().toLocalDate());

            products.add(product);
        }

        return products.stream().map(this::toDTO).collect(Collectors.toList());
    }

    // Buscar por ID, devuelve Optional<ProductDTO>
    public Optional<ProductDTO> findById(Long productId) {
        System.out.println("Buscando producto con ID: " + productId);
        List<Object[]> rawDataList = productRepository.findByIdWithNames(productId);

        if (rawDataList == null || rawDataList.isEmpty()) {
            return Optional.empty();
        }

        // Get the first (and should be only) result
        Object[] rawData = rawDataList.get(0);
        System.out.println("Datos crudos procesados: " + java.util.Arrays.toString(rawData));

        try {
            Product product = new Product();
            product.setProductId(((Number) rawData[0]).longValue());
            product.setName((String) rawData[1]);
            product.setDescription((String) rawData[2]);
            product.setUnitPrice(((Number) rawData[3]).doubleValue());
            product.setStock(((Number) rawData[4]).intValue());
            product.setStatus(rawData[5].toString());

            // IDs de relaciones
            product.setCategoryId(((Number) rawData[6]).longValue());
            product.setSupplierId(((Number) rawData[7]).longValue());
            product.setBrandId(((Number) rawData[8]).longValue());

            // Nombres de relaciones
            product.setCategoryName((String) rawData[9]);
            product.setSupplierName((String) rawData[10]);
            product.setBrandName((String) rawData[11]);

            product.setRegistrationDate(((java.sql.Timestamp) rawData[12]).toLocalDateTime().toLocalDate());

            return Optional.of(toDTO(product));
        } catch (Exception e) {
            System.err.println("Error procesando rawData: " + java.util.Arrays.toString(rawData));
            e.printStackTrace();
            return Optional.empty();
        }
    }

    // Buscar por estado, devuelve lista de DTOs
    public List<ProductDTO> findByStatus(String status) {
        return findAll().stream()
                .filter(dto -> dto.getStatus().equalsIgnoreCase(status))
                .collect(Collectors.toList());
    }

    // Crear producto a partir de DTO, retorna DTO
    public ProductDTO create(ProductDTO dto) {
        Product product = toEntity(dto);

        Long categoryId = findCategoryIdByName(product.getCategoryName());
        Long supplierId = findSupplierIdByName(product.getSupplierName());
        Long brandId = findBrandIdByName(product.getBrandName());

        product.setCategoryId(categoryId);
        product.setSupplierId(supplierId);
        product.setBrandId(brandId);

        product.setProductId(null);
        product.setStatus(Constants.A.name());
        product.setRegistrationDate(java.time.LocalDate.now());
        Product saved = productRepository.save(product);
        return toDTO(saved);
    }

    // Actualizar producto a partir de DTO, retorna DTO
    public ProductDTO update(ProductDTO dto) {
        Product product = toEntity(dto);

        Product updated = productRepository.findById(product.getProductId()).map(existing -> {
            existing.setName(product.getName());
            existing.setDescription(product.getDescription());
            existing.setUnitPrice(product.getUnitPrice());
            existing.setStock(product.getStock());
            existing.setStatus(product.getStatus());

            existing.setCategoryId(findCategoryIdByName(product.getCategoryName()));
            existing.setSupplierId(findSupplierIdByName(product.getSupplierName()));
            existing.setBrandId(findBrandIdByName(product.getBrandName()));
            existing.setCategoryName(product.getCategoryName()); // <-- agregar
            existing.setSupplierName(product.getSupplierName()); // <-- agregar
            existing.setBrandName(product.getBrandName());
            return productRepository.save(existing);
        }).orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + product.getProductId()));

        return toDTO(updated);
    }

    // Eliminación lógica (sin cambio)
    public void deleteLogic(Long id) {
        productRepository.findById(id).ifPresent(product -> {
            product.setStatus(Constants.I.name());
            productRepository.save(product);
        });
    }

    // Restauración lógica (sin cambio)
    public void restoreLogic(Long id) {
        productRepository.findById(id).ifPresent(product -> {
            product.setStatus(Constants.A.name());
            productRepository.save(product);
        });
    }

    // Métodos para obtener los IDs a partir de los nombres (sin cambio)
    private Long findCategoryIdByName(String categoryName) {
        Category category = categoryRepository.findByCategoryName(categoryName);
        if (category != null) {
            return category.getCategoryId();
        }
        throw new RuntimeException("Categoría no encontrada: " + categoryName);
    }

    private Long findSupplierIdByName(String supplierName) {
        Supplier supplier = supplierRepository.findBySupplierName(supplierName);
        if (supplier != null) {
            return supplier.getSupplierId();
        }
        throw new RuntimeException("Proveedor no encontrado: " + supplierName);
    }

    private Long findBrandIdByName(String brandName) {
        Brand brand = brandRepository.findByBrandName(brandName);
        if (brand != null) {
            return brand.getBrandId();
        }
        throw new RuntimeException("Marca no encontrada: " + brandName);
    }
}
