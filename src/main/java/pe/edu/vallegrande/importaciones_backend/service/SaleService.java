package pe.edu.vallegrande.importaciones_backend.service;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.edu.vallegrande.importaciones_backend.dto.SaleDetailRequestDTO;
import pe.edu.vallegrande.importaciones_backend.dto.SaleDetailResponseDTO;
import pe.edu.vallegrande.importaciones_backend.dto.SaleRequestDTO;
import pe.edu.vallegrande.importaciones_backend.dto.SaleResponseDTO;
import pe.edu.vallegrande.importaciones_backend.enums.Constants;
import pe.edu.vallegrande.importaciones_backend.model.*;
import pe.edu.vallegrande.importaciones_backend.repository.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SaleService {

    @Autowired
    private SaleRepository saleRepository;

    @Autowired
    private SaleDetailRepository saleDetailRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OfficeRepository officeRepository;

    public List<SaleResponseDTO> getAllSales() {
        List<Sale> sales = saleRepository.findAll();
        return sales.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public SaleResponseDTO getSaleById(Long id) {
        Optional<Sale> saleOptional = saleRepository.findById(id);
        if (saleOptional.isPresent()) {
            return convertToDTO(saleOptional.get());
        }
        return null;
    }

    @Transactional
    public SaleResponseDTO createSale(SaleRequestDTO saleRequestDTO) {
        User client = userRepository.findById(saleRequestDTO.getClient_id())
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));

        User employee = userRepository.findByUser_idAndRole(saleRequestDTO.getEmployee_id(), Constants.Vendedor.name())
                .orElseThrow(() -> new RuntimeException("Empleado no encontrado o no tiene el rol correcto"));

        Office office = officeRepository.findById(saleRequestDTO.getOffice_id())
                .orElseThrow(() -> new RuntimeException("Oficina no encontrada"));

        Sale sale = new Sale();
        sale.setSale_date(LocalDateTime.now());
        sale.setStatus("Pendiente");
        sale.setPayment_method(saleRequestDTO.getPayment_method());
        sale.setDelivery_method(saleRequestDTO.getDelivery_method());
        sale.setDelivery_address(saleRequestDTO.getDelivery_address());
        sale.setClientId(client);
        sale.setEmployeeId(employee);
        sale.setOfficeId(office);

        double total = 0.0;
        List<SaleDetail> saleDetails = new ArrayList<>();

        for (SaleDetailRequestDTO detailDTO : saleRequestDTO.getSaleDetails()) {
            Product product = productRepository.findById(detailDTO.getProductId())
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado: " + detailDTO.getProductId()));

            double unitPrice = product.getUnitPrice();
            int quantity = detailDTO.getQuantity();
            double subtotal = quantity * unitPrice;

            SaleDetail saleDetail = new SaleDetail();
            saleDetail.setSale(sale);
            saleDetail.setProduct(product);
            saleDetail.setQuantity(quantity);
            saleDetail.setUnit_price(unitPrice);
            saleDetail.setSubtotal(subtotal);

            saleDetails.add(saleDetail);

            product.setStock(product.getStock() - quantity);
            productRepository.save(product);

            total += subtotal;
        }

        sale.setTotal(total);
        Sale savedSale = saleRepository.save(sale);

        saleDetailRepository.saveAll(saleDetails);
        savedSale.setSaleDetails(saleDetails);

        return convertToDTO(savedSale);
    }

    private SaleResponseDTO convertToDTO(Sale sale) {
        SaleResponseDTO dto = new SaleResponseDTO();
        dto.setSaleId(sale.getSale_id());
        dto.setSaleDate(sale.getSale_date());
        dto.setTotal(sale.getTotal());
        dto.setStatus(sale.getStatus());
        dto.setPaymentMethod(sale.getPayment_method());
        dto.setDeliveryMethod(sale.getDelivery_method());
        dto.setDeliveryAddress(sale.getDelivery_address());

        if (sale.getClientId() != null) {
            dto.setClientId(sale.getClientId().getUser_id());
            dto.setClientName(sale.getClientId().getName() + " " + sale.getClientId().getLast_name());
        }

        if (sale.getEmployeeId() != null) {
            dto.setEmployeeId(sale.getEmployeeId().getUser_id());
            dto.setEmployeeName(sale.getEmployeeId().getName() + " " + sale.getEmployeeId().getLast_name());
        }

        if (sale.getOfficeId() != null) {
            dto.setOfficeId(sale.getOfficeId().getOffice_id());
            dto.setOfficeName(sale.getOfficeId().getOffice_name());
        }

        if (sale.getSaleDetails() != null) {
            List<SaleDetailResponseDTO> detailDTOs = sale.getSaleDetails().stream()
                    .map(this::convertDetailToDTO)
                    .collect(Collectors.toList());
            dto.setSaleDetails(detailDTOs);
        }

        return dto;
    }

    private SaleDetailResponseDTO convertDetailToDTO(SaleDetail detail) {
        SaleDetailResponseDTO dto = new SaleDetailResponseDTO();
        dto.setSaleDetailId(detail.getSale_detail_id());
        dto.setQuantity(detail.getQuantity());
        dto.setUnitPrice(detail.getUnit_price());
        dto.setSubtotal(detail.getSubtotal());

        if (detail.getProduct() != null) {
            dto.setProductId(detail.getProduct().getProductId());
            dto.setProductName(detail.getProduct().getName());
            dto.setProductDescription(detail.getProduct().getDescription());
        }

        return dto;
    }

    // 1. ELIMINAR LÓGICAMENTE UNA VENTA
    @Transactional
    public SaleResponseDTO deleteSaleLogically(Long saleId) {
        Sale sale = saleRepository.findById(saleId)
                .orElseThrow(() -> new RuntimeException("Venta no encontrada"));

        // Cambiar estado a "Eliminada" o "Cancelado"
        sale.setStatus("Cancelado");

        // Restaurar el stock de los productos
        for (SaleDetail detail : sale.getSaleDetails()) {
            Product product = detail.getProduct();
            product.setStock(product.getStock() + detail.getQuantity());
            productRepository.save(product);
        }

        Sale updatedSale = saleRepository.save(sale);
        return convertToDTO(updatedSale);
    }

    @Transactional
    public SaleResponseDTO restoreSale(Long saleId) {
        Sale sale = saleRepository.findById(saleId)
                .orElseThrow(() -> new RuntimeException("Venta no encontrada"));

        if (!"Cancelado".equals(sale.getStatus())) {
            throw new RuntimeException("La venta no está eliminada");
        }

        for (SaleDetail detail : sale.getSaleDetails()) {
            Product product = detail.getProduct();
            if (product.getStock() < detail.getQuantity()) {
                throw new RuntimeException("Stock insuficiente para el producto: " + product.getName());
            }
        }

        for (SaleDetail detail : sale.getSaleDetails()) {
            Product product = detail.getProduct();
            product.setStock(product.getStock() - detail.getQuantity());
            productRepository.save(product);
        }

        sale.setStatus("Pagado");
        Sale restoredSale = saleRepository.save(sale);
        return convertToDTO(restoredSale);
    }

    @Transactional
    public SaleResponseDTO updateSaleStatus(Long saleId, String newStatus) {
        Sale sale = saleRepository.findById(saleId)
                .orElseThrow(() -> new RuntimeException("Venta no encontrada"));

        List<String> validStatuses = Arrays.asList("Pendiente", "Pagado", "Enviado",
                "Enviada", "Entregada", "Cancelado", "Eliminada");
        if (!validStatuses.contains(newStatus)) {
            throw new RuntimeException("Estado no válido: " + newStatus);
        }

        String oldStatus = sale.getStatus();

        // Manejar cambios de stock según el cambio de estado
        if (!oldStatus.equals(newStatus)) {

            // Si cambia DE un estado activo A "Cancelado" -> restaurar stock
            if (!"Cancelado".equals(oldStatus) && "Cancelado".equals(newStatus)) {
                log.info("Cancelando venta {}: restaurando stock", saleId);
                for (SaleDetail detail : sale.getSaleDetails()) {
                    Product product = detail.getProduct();
                    product.setStock(product.getStock() + detail.getQuantity());
                    productRepository.save(product);
                    log.info("Producto {}: stock restaurado +{} = {}",
                            product.getName(), detail.getQuantity(), product.getStock());
                }
            }

            // Si cambia DE "Cancelado" A un estado activo -> reducir stock
            else if ("Cancelado".equals(oldStatus) && !"Cancelado".equals(newStatus)) {
                log.info("Restaurando venta {}: reduciendo stock", saleId);
                for (SaleDetail detail : sale.getSaleDetails()) {
                    Product product = detail.getProduct();

                    // Verificar que hay stock suficiente
                    if (product.getStock() < detail.getQuantity()) {
                        throw new RuntimeException("Stock insuficiente para el producto: " +
                                product.getName() + ". Stock disponible: " + product.getStock() +
                                ", cantidad requerida: " + detail.getQuantity());
                    }

                    product.setStock(product.getStock() - detail.getQuantity());
                    productRepository.save(product);
                    log.info("Producto {}: stock reducido -{} = {}",
                            product.getName(), detail.getQuantity(), product.getStock());
                }
            }
        }

        sale.setStatus(newStatus);
        Sale updatedSale = saleRepository.save(sale);
        log.info("Estado de venta {} cambiado de '{}' a '{}'", saleId, oldStatus, newStatus);
        return convertToDTO(updatedSale);
    }

    @Transactional
    public SaleResponseDTO removeSaleDetail(Long saleId, Long saleDetailId) {
        Sale sale = saleRepository.findById(saleId)
                .orElseThrow(() -> new RuntimeException("Venta no encontrada"));

        if ("Cancelado".equals(sale.getStatus())) {
            throw new RuntimeException("No se puede modificar una venta " + sale.getStatus().toLowerCase());
        }

        // Validar que la venta no quede vacía
        if (sale.getSaleDetails().size() <= 1) {
            throw new RuntimeException(
                    "No se puede eliminar el último producto. Una venta debe tener al menos un producto.");
        }

        SaleDetail detailToRemove = sale.getSaleDetails().stream()
                .filter(detail -> detail.getSale_detail_id().equals(saleDetailId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Detalle de venta no encontrado"));

        Product product = detailToRemove.getProduct();
        product.setStock(product.getStock() + detailToRemove.getQuantity());
        productRepository.save(product);

        sale.getSaleDetails().remove(detailToRemove);
        saleDetailRepository.delete(detailToRemove);

        double newTotal = sale.getSaleDetails().stream()
                .mapToDouble(SaleDetail::getSubtotal)
                .sum();
        sale.setTotal(newTotal);

        Sale updatedSale = saleRepository.save(sale);
        return convertToDTO(updatedSale);
    }

    // 5. AGREGAR NUEVO DETALLE A UNA VENTA EXISTENTE
    @Transactional
    public SaleResponseDTO addSaleDetail(Long saleId, SaleDetailRequestDTO detailDTO) {
        Sale sale = saleRepository.findById(saleId)
                .orElseThrow(() -> new RuntimeException("Venta no encontrada"));

        if ("Entregada".equals(sale.getStatus()) || "Cancelado".equals(sale.getStatus())) {
            throw new RuntimeException("No se puede modificar una venta " + sale.getStatus().toLowerCase());
        }

        Product product = productRepository.findById(detailDTO.getProductId())
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        // Verificar que el producto no esté ya en la venta
        boolean productExists = sale.getSaleDetails().stream()
                .anyMatch(detail -> detail.getProduct().getProductId().equals(detailDTO.getProductId()));

        if (productExists) {
            throw new RuntimeException(
                    "El producto ya está incluido en esta venta. Para modificar la cantidad, usa la opción de editar.");
        }

        if (product.getStock() < detailDTO.getQuantity()) {
            throw new RuntimeException("Stock insuficiente para el producto: " + product.getName());
        }

        double unitPrice = product.getUnitPrice();
        double subtotal = detailDTO.getQuantity() * unitPrice;

        SaleDetail newDetail = new SaleDetail();
        newDetail.setSale(sale);
        newDetail.setProduct(product);
        newDetail.setQuantity(detailDTO.getQuantity());
        newDetail.setUnit_price(unitPrice);
        newDetail.setSubtotal(subtotal);

        product.setStock(product.getStock() - detailDTO.getQuantity());
        productRepository.save(product);

        saleDetailRepository.save(newDetail);
        sale.getSaleDetails().add(newDetail);

        sale.setTotal(sale.getTotal() + subtotal);

        Sale updatedSale = saleRepository.save(sale);
        return convertToDTO(updatedSale);
    }

    // 6. ACTUALIZAR CANTIDAD DE UN DETALLE
    @Transactional
    public SaleResponseDTO updateSaleDetailQuantity(Long saleId, Long saleDetailId, int newQuantity) {
        Sale sale = saleRepository.findById(saleId)
                .orElseThrow(() -> new RuntimeException("Venta no encontrada"));

        if ("Entregada".equals(sale.getStatus()) || "Cancelado".equals(sale.getStatus())) {
            throw new RuntimeException("No se puede modificar una venta " + sale.getStatus().toLowerCase());
        }

        SaleDetail detail = sale.getSaleDetails().stream()
                .filter(d -> d.getSale_detail_id().equals(saleDetailId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Detalle de venta no encontrado"));

        Product product = detail.getProduct();
        int currentQuantity = detail.getQuantity();
        int quantityDifference = newQuantity - currentQuantity;

        if (quantityDifference > 0 && product.getStock() < quantityDifference) {
            throw new RuntimeException("Stock insuficiente para aumentar la cantidad");
        }

        product.setStock(product.getStock() - quantityDifference);
        productRepository.save(product);

        double oldSubtotal = detail.getSubtotal();
        detail.setQuantity(newQuantity);
        detail.setSubtotal(newQuantity * detail.getUnit_price());
        saleDetailRepository.save(detail);

        sale.setTotal(sale.getTotal() - oldSubtotal + detail.getSubtotal());
        Sale updatedSale = saleRepository.save(sale);

        return convertToDTO(updatedSale);
    }

    // 7. OBTENER VENTAS POR ESTADO
    public List<SaleResponseDTO> getSalesByStatus(String status) {
        List<Sale> sales = saleRepository.findByStatus(status);
        return sales.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    // 8. OBTENER VENTAS ACTIVAS (no eliminadas)
    public List<SaleResponseDTO> getActiveSales() {
        List<Sale> sales = saleRepository.findByStatusNot(Constants.Cancelado.name());
        return sales.stream().map(this::convertToDTO).collect(Collectors.toList());
    } // 10. OBTENER VENTAS POR CLIENTE // 10. OBTENER VENTAS POR CLIENTE

    public List<SaleResponseDTO> getSalesByClient(Long clientId) {
        // Verificar que el cliente existe
        userRepository.findById(clientId)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado con ID: " + clientId));

        // Buscar las ventas por el ID del cliente
        List<Sale> sales = saleRepository.findByClientIdUserId(clientId);
        log.info("Encontradas {} ventas para el cliente con ID: {}", sales.size(), clientId);

        return sales.stream().map(this::convertToDTO).collect(Collectors.toList());
    }
}
