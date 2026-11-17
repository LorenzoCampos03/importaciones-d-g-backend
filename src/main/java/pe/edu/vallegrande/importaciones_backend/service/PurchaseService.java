package pe.edu.vallegrande.importaciones_backend.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pe.edu.vallegrande.importaciones_backend.dto.PurchaseDTO;
import pe.edu.vallegrande.importaciones_backend.dto.PurchaseDetailDTO;
import pe.edu.vallegrande.importaciones_backend.model.Purchase;
import pe.edu.vallegrande.importaciones_backend.model.PurchaseDetail;
import pe.edu.vallegrande.importaciones_backend.repository.ProductRepository;
import pe.edu.vallegrande.importaciones_backend.repository.PurchaseDetailRepository;
import pe.edu.vallegrande.importaciones_backend.repository.PurchaseRepository;
import pe.edu.vallegrande.importaciones_backend.repository.UserRepository;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PurchaseService {

    private final PurchaseRepository purchaseRepository;
    private final PurchaseDetailRepository purchaseDetailRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;// <-- inyectado

    private final List<String> validStatuses = List.of("Pendiente", "Pagado", "Recibido", "Cancelado");

    public List<PurchaseDTO> getAllPurchases() {
        List<Purchase> purchases = purchaseRepository.findAll();

        return purchases.stream().map(purchase -> {
            PurchaseDTO dto = new PurchaseDTO();
            dto.setPurchaseId(purchase.getPurchaseId());
            dto.setEmployeeId(purchase.getEmployeeId());
            dto.setSupplierId(purchase.getSupplierId());
            dto.setPurchaseDate(purchase.getPurchaseDate());
            dto.setStatus(purchase.getStatus());
            dto.setTotalCost(purchase.getTotalCost());

            // Agregar nombre del usuario
            userRepository.findById(purchase.getEmployeeId()).ifPresent(user -> {
                dto.setUserName(user.getName() + " " + user.getLast_name());
            });
            // Obtener y asignar detalles
            List<PurchaseDetail> details = purchaseDetailRepository.findByPurchase_PurchaseId(purchase.getPurchaseId());
            List<PurchaseDetailDTO> detailDTOs = details.stream().map(detail -> {
                PurchaseDetailDTO d = new PurchaseDetailDTO();
                d.setProductId(detail.getProductId());
                d.setQuantity(detail.getQuantity());
                d.setUnitCost(detail.getUnitCost());
                d.setTotalCost(detail.getUnitCost() * detail.getQuantity()); // si deseas mostrar el total
                // Si tienes acceso al producto y su nombre:
                if (detail.getProduct() != null) {
                    d.setProductName(detail.getProduct().getName());
                }
                return d;
            }).toList();
            dto.setPurchaseDetails(detailDTOs);

            return dto;
        }).toList();
    }

    public List<PurchaseDTO> getPurchasesByStatus(String status) {
        if (!validStatuses.contains(status)) {
            throw new IllegalArgumentException("Estado inválido: " + status);
        }

        List<Purchase> purchases = purchaseRepository.findByStatus(status);

        return purchases.stream().map(purchase -> {
            PurchaseDTO dto = new PurchaseDTO();
            dto.setPurchaseId(purchase.getPurchaseId());
            dto.setEmployeeId(purchase.getEmployeeId());
            dto.setSupplierId(purchase.getSupplierId());
            dto.setPurchaseDate(purchase.getPurchaseDate());
            dto.setStatus(purchase.getStatus());
            dto.setTotalCost(purchase.getTotalCost());

            // Agregar nombre del usuario
            userRepository.findById(purchase.getEmployeeId()).ifPresent(user -> {
                dto.setUserName(user.getName() + " " + user.getLast_name());
            });

            // Agregar detalles
            List<PurchaseDetailDTO> detailDTOs = purchaseDetailRepository
                    .findByPurchase_PurchaseId(purchase.getPurchaseId())
                    .stream()
                    .map(detail -> {
                        PurchaseDetailDTO d = new PurchaseDetailDTO();
                        d.setProductId(detail.getProductId());
                        d.setQuantity(detail.getQuantity());
                        d.setUnitCost(detail.getUnitCost());
                        d.setTotalCost(detail.getTotalCost());

                        // Obtener nombre del producto
                        productRepository.findById(detail.getProductId()).ifPresent(product -> {
                            d.setProductName(product.getName());
                        });

                        return d;
                    }).toList();

            dto.setPurchaseDetails(detailDTOs);

            return dto;
        }).toList();
    }

    public PurchaseDTO getPurchaseById(Long purchaseId) {
        Purchase purchase = purchaseRepository.findById(purchaseId)
                .orElseThrow(() -> new IllegalArgumentException("Compra no encontrada con id: " + purchaseId));

        PurchaseDTO dto = new PurchaseDTO();
        dto.setPurchaseId(purchase.getPurchaseId());
        dto.setEmployeeId(purchase.getEmployeeId());
        dto.setSupplierId(purchase.getSupplierId());
        dto.setPurchaseDate(purchase.getPurchaseDate());
        dto.setStatus(purchase.getStatus());
        dto.setTotalCost(purchase.getTotalCost());

        // Agregar nombre del usuario
        userRepository.findById(purchase.getEmployeeId()).ifPresent(user -> {
            dto.setUserName(user.getName() + " " + user.getLast_name());
        });

        // Obtener detalles con nombre de producto
        List<PurchaseDetail> details = purchaseDetailRepository.findByPurchase_PurchaseId(purchaseId);
        List<PurchaseDetailDTO> detailDTOs = details.stream().map(detail -> {
            PurchaseDetailDTO d = new PurchaseDetailDTO();
            d.setProductId(detail.getProductId());
            d.setQuantity(detail.getQuantity());
            d.setUnitCost(detail.getUnitCost());
            d.setTotalCost(detail.getTotalCost());

            // Obtener nombre del producto
            productRepository.findById(detail.getProductId()).ifPresent(product -> {
                d.setProductName(product.getName());
            });

            return d;
        }).toList();

        dto.setPurchaseDetails(detailDTOs);

        return dto;
    }

    @Transactional
    public PurchaseDTO createPurchaseWithDetails(PurchaseDTO purchaseDTO) {
        // Validar que tenga al menos un detalle
        if (purchaseDTO.getPurchaseDetails() == null || purchaseDTO.getPurchaseDetails().isEmpty()) {
            throw new IllegalArgumentException("No se puede crear una compra sin detalles");
        }

        // Validar status válido
        if (purchaseDTO.getStatus() == null || !validStatuses.contains(purchaseDTO.getStatus())) {
            throw new IllegalArgumentException("Estado inválido: " + purchaseDTO.getStatus());
        }

        // Convertir DTO a entidad Purchase (sin ID para autogenerar)
        Purchase purchase = toEntity(purchaseDTO);
        purchase.setPurchaseId(null); // para autogenerar

        // Calcular totalCost sumando detalles si no está definido
        double totalCost = 0.0;
        if (purchaseDTO.getTotalCost() != null) {
            totalCost = purchaseDTO.getTotalCost();
        } else {
            totalCost = purchaseDTO.getPurchaseDetails().stream()
                    .mapToDouble(d -> {
                        if (d.getTotalCost() != null)
                            return d.getTotalCost();
                        return d.getUnitCost() * d.getQuantity();
                    }).sum();
        }
        purchase.setTotalCost(totalCost);

        // Guardar compra primero para obtener id generado
        Purchase savedPurchase = purchaseRepository.save(purchase);

        // Convertir detalles y asignar la compra guardada (FK)
        List<PurchaseDetail> details = purchaseDTO.getPurchaseDetails().stream()
                .map(this::toEntityDetail)
                .peek(detail -> {
                    detail.setPurchase(savedPurchase);
                    detail.setPurchaseDetailId(null); // para autogenerar
                }).toList();

        // Guardar detalles
        purchaseDetailRepository.saveAll(details);

        // Actualizar stock SIEMPRE, excepto si el estado es "Cancelado"
        if (!"Cancelado".equalsIgnoreCase(purchaseDTO.getStatus())) {
            for (PurchaseDetail detail : details) {
                productRepository.findById(detail.getProductId()).ifPresent(product -> {
                    Integer currentStock = product.getStock() != null ? product.getStock() : 0;
                    product.setStock(currentStock + detail.getQuantity());
                    productRepository.save(product);
                });
            }
        }

        // Actualizar DTO con ID y totalCost generado
        purchaseDTO.setPurchaseId(savedPurchase.getPurchaseId());
        purchaseDTO.setTotalCost(totalCost);

        // --- Aquí agregamos el nombre del usuario ---
        userRepository.findById(purchaseDTO.getEmployeeId()).ifPresent(user -> {
            String fullName = user.getName() + " " + user.getLast_name();
            purchaseDTO.setUserName(fullName);
        });

        return purchaseDTO;
    }

    @Transactional
    public PurchaseDTO updatePurchase(Long purchaseId, PurchaseDTO purchaseDTO) {
        Purchase existingPurchase = purchaseRepository.findById(purchaseId)
                .orElseThrow(() -> new IllegalArgumentException("Compra no encontrada con ID: " + purchaseId));

        // Validar status válido
        if (purchaseDTO.getStatus() == null || !validStatuses.contains(purchaseDTO.getStatus())) {
            throw new IllegalArgumentException("Estado inválido: " + purchaseDTO.getStatus());
        }

        String oldStatus = existingPurchase.getStatus();
        String newStatus = purchaseDTO.getStatus();

        // Obtener detalles anteriores para ajustar stock
        List<PurchaseDetail> oldDetails = purchaseDetailRepository.findByPurchase_PurchaseId(purchaseId);

        // Actualizar campos de la compra
        existingPurchase.setEmployeeId(purchaseDTO.getEmployeeId());
        existingPurchase.setSupplierId(purchaseDTO.getSupplierId());
        existingPurchase.setPurchaseDate(purchaseDTO.getPurchaseDate());
        existingPurchase.setStatus(newStatus);

        // Calcular totalCost
        double totalCost = 0.0;
        if (purchaseDTO.getTotalCost() != null) {
            totalCost = purchaseDTO.getTotalCost();
        } else if (purchaseDTO.getPurchaseDetails() != null) {
            totalCost = purchaseDTO.getPurchaseDetails().stream()
                    .mapToDouble(d -> {
                        if (d.getTotalCost() != null)
                            return d.getTotalCost();
                        return d.getUnitCost() * d.getQuantity();
                    }).sum();
        }
        existingPurchase.setTotalCost(totalCost);
        purchaseRepository.save(existingPurchase);

        // --- Ajustar stock según estado y diferencias ---
        // Mapear los detalles viejos por productId para fácil acceso
        Map<Long, Integer> oldQuantities = oldDetails.stream()
                .collect(Collectors.toMap(PurchaseDetail::getProductId, PurchaseDetail::getQuantity));

        // Mapear detalles nuevos por productId
        Map<Long, Integer> newQuantities = new HashMap<>();
        if (purchaseDTO.getPurchaseDetails() != null) {
            for (PurchaseDetailDTO d : purchaseDTO.getPurchaseDetails()) {
                newQuantities.put(d.getProductId(), d.getQuantity());
            }
        }

        if (!"Cancelado".equalsIgnoreCase(oldStatus) && "Cancelado".equalsIgnoreCase(newStatus)) {
            // Estado cambia de cualquier estado a Cancelado: reducir stock con cantidades
            // viejas
            for (Map.Entry<Long, Integer> entry : oldQuantities.entrySet()) {
                productRepository.findById(entry.getKey()).ifPresent(product -> {
                    Integer currentStock = product.getStock() != null ? product.getStock() : 0;
                    int newStock = currentStock - entry.getValue();
                    if (newStock < 0)
                        newStock = 0; // No permitir stock negativo
                    product.setStock(newStock);
                    productRepository.save(product);
                });
            }
        } else if ("Cancelado".equalsIgnoreCase(oldStatus) && !"Cancelado".equalsIgnoreCase(newStatus)) {
            // Estado cambia de Cancelado a cualquier otro: aumentar stock con cantidades
            // nuevas
            for (Map.Entry<Long, Integer> entry : newQuantities.entrySet()) {
                productRepository.findById(entry.getKey()).ifPresent(product -> {
                    Integer currentStock = product.getStock() != null ? product.getStock() : 0;
                    product.setStock(currentStock + entry.getValue());
                    productRepository.save(product);
                });
            }
        } else if (!"Cancelado".equalsIgnoreCase(oldStatus) && !"Cancelado".equalsIgnoreCase(newStatus)) {
            // Ambos estados NO son Cancelado: ajustar stock según diferencia en cantidades
            Set<Long> allProductIds = new HashSet<>();
            allProductIds.addAll(oldQuantities.keySet());
            allProductIds.addAll(newQuantities.keySet());

            for (Long productId : allProductIds) {
                int oldQty = oldQuantities.getOrDefault(productId, 0);
                int newQty = newQuantities.getOrDefault(productId, 0);
                int diff = newQty - oldQty; // positivo = aumentar stock, negativo = reducir stock

                if (diff != 0) {
                    productRepository.findById(productId).ifPresent(product -> {
                        Integer currentStock = product.getStock() != null ? product.getStock() : 0;
                        int newStock = currentStock + diff;
                        if (newStock < 0)
                            newStock = 0; // No permitir stock negativo
                        product.setStock(newStock);
                        productRepository.save(product);
                    });
                }
            }
        }
        // Si no es Recibido antes ni ahora, no cambia stock

        // Eliminar detalles anteriores
        purchaseDetailRepository.deleteAll(oldDetails);

        // Guardar nuevos detalles
        List<PurchaseDetailDTO> updatedDetailDTOs = new ArrayList<>();
        if (purchaseDTO.getPurchaseDetails() != null && !purchaseDTO.getPurchaseDetails().isEmpty()) {
            List<PurchaseDetail> details = purchaseDTO.getPurchaseDetails().stream()
                    .map(this::toEntityDetail)
                    .peek(detail -> {
                        detail.setPurchase(existingPurchase);
                        detail.setPurchaseDetailId(null);
                    }).toList();

            List<PurchaseDetail> savedDetails = purchaseDetailRepository.saveAll(details);

            // Convertir de vuelta a DTOs con nombre de producto y calcular totalCost si es
            // necesario
            for (PurchaseDetail detail : savedDetails) {
                PurchaseDetailDTO detailDTO = new PurchaseDetailDTO();
                detailDTO.setProductId(detail.getProductId());
                detailDTO.setQuantity(detail.getQuantity());
                detailDTO.setUnitCost(detail.getUnitCost());

                // Calcular totalCost si no estaba seteado
                Double detailTotalCost = detail.getTotalCost();
                if (detailTotalCost == null && detail.getUnitCost() != null && detail.getQuantity() != null) {
                    detailTotalCost = detail.getUnitCost() * detail.getQuantity();
                }
                detailDTO.setTotalCost(detailTotalCost);

                // Obtener nombre del producto
                productRepository.findById(detail.getProductId()).ifPresent(product -> {
                    detailDTO.setProductName(product.getName());
                });

                updatedDetailDTOs.add(detailDTO);
            }
        }

        // Setear datos finales del DTO
        purchaseDTO.setPurchaseId(purchaseId);
        purchaseDTO.setTotalCost(totalCost);
        purchaseDTO.setPurchaseDetails(updatedDetailDTOs);

        // Nombre del empleado
        userRepository.findById(purchaseDTO.getEmployeeId()).ifPresent(user -> {
            purchaseDTO.setUserName(user.getName() + " " + user.getLast_name());
        });

        return purchaseDTO;
    }

    @Transactional
    public void cancelPurchase(Long purchaseId) {
        Purchase purchase = purchaseRepository.findById(purchaseId)
                .orElseThrow(() -> new IllegalArgumentException("Compra no encontrada con ID: " + purchaseId));

        String oldStatus = purchase.getStatus();

        // Cambiar el estado a "Cancelado"
        purchase.setStatus("Cancelado");
        purchaseRepository.save(purchase);

        // Solo reducir stock si el estado anterior NO era "Cancelado"
        if (!"Cancelado".equalsIgnoreCase(oldStatus)) {
            List<PurchaseDetail> purchaseDetails = purchaseDetailRepository.findByPurchase_PurchaseId(purchaseId);

            for (PurchaseDetail detail : purchaseDetails) {
                Long productId = detail.getProductId();
                Integer quantity = detail.getQuantity();

                productRepository.findById(productId).ifPresent(product -> {
                    Integer currentStock = product.getStock();
                    if (currentStock != null) {
                        int newStock = currentStock - quantity;
                        if (newStock < 0)
                            newStock = 0; // No permitir stock negativo
                        product.setStock(newStock);
                        productRepository.save(product);
                    }
                });
            }
        }
    }

    private Purchase toEntity(PurchaseDTO dto) {
        Purchase purchase = new Purchase();
        purchase.setEmployeeId(dto.getEmployeeId());
        purchase.setSupplierId(dto.getSupplierId());
        purchase.setPurchaseDate(dto.getPurchaseDate());
        purchase.setStatus(dto.getStatus());
        return purchase;
    }

    private PurchaseDetail toEntityDetail(PurchaseDetailDTO dto) {
        PurchaseDetail detail = new PurchaseDetail();
        detail.setProductId(dto.getProductId());
        detail.setQuantity(dto.getQuantity());
        detail.setUnitCost(dto.getUnitCost());
        return detail;
    }
}
