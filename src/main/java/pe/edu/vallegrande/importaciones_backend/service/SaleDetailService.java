package pe.edu.vallegrande.importaciones_backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.edu.vallegrande.importaciones_backend.dto.SaleDetailRequestDTO;
import pe.edu.vallegrande.importaciones_backend.dto.SaleDetailResponseDTO;
import pe.edu.vallegrande.importaciones_backend.model.Product;
import pe.edu.vallegrande.importaciones_backend.model.Sale;
import pe.edu.vallegrande.importaciones_backend.model.SaleDetail;
import pe.edu.vallegrande.importaciones_backend.repository.ProductRepository;
import pe.edu.vallegrande.importaciones_backend.repository.SaleDetailRepository;
import pe.edu.vallegrande.importaciones_backend.repository.SaleRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SaleDetailService {

    @Autowired
    private SaleDetailRepository saleDetailRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private SaleRepository saleRepository;

    public List<SaleDetailResponseDTO> findAll() {
        List<SaleDetail> saleDetails = saleDetailRepository.findAll();
        return saleDetails.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    public List<SaleDetailResponseDTO> findBySaleId(Long saleId) {
        List<SaleDetail> saleDetails = saleDetailRepository.findBySaleId(saleId);
        return saleDetails.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    // Obtener un detalle por ID
    public Optional<SaleDetailResponseDTO> findById(Long id) {
        return saleDetailRepository.findById(id)
                .map(this::convertToResponseDTO);
    }


    public SaleDetailResponseDTO save(SaleDetailRequestDTO saleDetailDTO, Long saleId) {
        Product product = productRepository.findById(saleDetailDTO.getProductId())
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
        Sale sale = saleRepository.findById(saleId)
                .orElseThrow(() -> new RuntimeException("Venta no encontrada"));

        double unitPrice = product.getUnitPrice();

        double subtotal = saleDetailDTO.getQuantity() * unitPrice;

        SaleDetail saleDetail = new SaleDetail();
        saleDetail.setQuantity(saleDetailDTO.getQuantity());
        saleDetail.setUnit_price(unitPrice);
        saleDetail.setSubtotal(subtotal);
        saleDetail.setProduct(product);
        saleDetail.setSale(sale);

        SaleDetail savedDetail = saleDetailRepository.save(saleDetail);

        sale.setTotal(sale.getTotal() + subtotal);
        saleRepository.save(sale);

        return convertToResponseDTO(savedDetail);
    }

    // Eliminar detalle
    public boolean deleteById(Long id) {
        return saleDetailRepository.findById(id)
                .map(saleDetail -> {
                    Sale sale = saleDetail.getSale();

                    sale.setTotal(sale.getTotal() - saleDetail.getSubtotal());
                    saleRepository.save(sale);

                    saleDetailRepository.delete(saleDetail);
                    return true;
                })
                .orElse(false);
    }

    // Actualizar detalle
    public Optional<SaleDetailResponseDTO> update(Long id, SaleDetailRequestDTO saleDetailDTO) {
        return saleDetailRepository.findById(id)
                .map(existingDetail -> {
                    double oldSubtotal = existingDetail.getSubtotal();

                    Product product = saleDetailDTO.getProductId() != null ?
                            productRepository.findById(saleDetailDTO.getProductId())
                                    .orElseThrow(() -> new RuntimeException("Producto no encontrado")) :
                            existingDetail.getProduct();

                    double unitPrice = product.getUnitPrice();
                    double newSubtotal = saleDetailDTO.getQuantity() * unitPrice;

                    existingDetail.setQuantity(saleDetailDTO.getQuantity());
                    existingDetail.setUnit_price(unitPrice);
                    existingDetail.setSubtotal(newSubtotal);
                    existingDetail.setProduct(product);

                    SaleDetail updatedDetail = saleDetailRepository.save(existingDetail);

                    Sale sale = existingDetail.getSale();
                    sale.setTotal(sale.getTotal() - oldSubtotal + newSubtotal);
                    saleRepository.save(sale);

                    return convertToResponseDTO(updatedDetail);
                });
    }

    private SaleDetailResponseDTO convertToResponseDTO(SaleDetail saleDetail) {
        SaleDetailResponseDTO dto = new SaleDetailResponseDTO();
        dto.setSaleDetailId(saleDetail.getSale_detail_id());
        dto.setQuantity(saleDetail.getQuantity());
        dto.setUnitPrice(saleDetail.getUnit_price());
        dto.setSubtotal(saleDetail.getSubtotal());

        if (saleDetail.getProduct() != null) {
            dto.setProductId(saleDetail.getProduct().getProductId());
            dto.setProductName(saleDetail.getProduct().getName());
            dto.setProductDescription(saleDetail.getProduct().getDescription());
        }
        if (saleDetail.getSale() != null) {
            dto.setSaleId(saleDetail.getSale().getSale_id());
        }
        return dto;
    }
}