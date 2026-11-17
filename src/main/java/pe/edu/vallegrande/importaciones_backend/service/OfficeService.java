package pe.edu.vallegrande.importaciones_backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.edu.vallegrande.importaciones_backend.enums.Constants;
import pe.edu.vallegrande.importaciones_backend.model.Office;
import pe.edu.vallegrande.importaciones_backend.repository.OfficeRepository;

import java.util.List;
import java.util.Optional;

@Service
public class OfficeService {

     @Autowired
     private OfficeRepository officeRepository;

     public List<Office> listAllOffices() {
          return officeRepository.findByStatusOrderedById(Constants.A.name());
     }

     public List<Office> listAllOfficesIncludingInactive() {
          return officeRepository.findAllOrderedById();
     }

     public List<Office> listOfficesByStatus(String status) {
          return officeRepository.findByStatusOrderedById(status);
     }

     public Optional<Office> getOfficeById(Long id) {
          return officeRepository.findByOffice_id(id);
     }

     public Office saveOffice(Office office) {
          office.setOffice_id(null);
          office.setStatus(Constants.A.name());
          return officeRepository.save(office);
     }

     public Office updateOffice(Office office) {
          if (office.getOffice_id() == null) {
               throw new RuntimeException("ID de oficina requerido para actualización");
          }
          return officeRepository.findByOffice_id(office.getOffice_id())
                    .map(existingOffice -> {
                         existingOffice.setOffice_name(office.getOffice_name());
                         existingOffice.setAddress(office.getAddress());
                         existingOffice.setCity(office.getCity());
                         existingOffice.setPhone(office.getPhone());
                         return officeRepository.save(existingOffice);
                    })
                    .orElseThrow(() -> new RuntimeException("Oficina no encontrada con ID: " + office.getOffice_id()));
     }

     public void deleteOffice(Long id) {
          officeRepository.findByOffice_id(id).ifPresent(office -> {
               office.setStatus(Constants.I.name());
               officeRepository.save(office);
          });
     }

     public void restoreOffice(Long id) {
          officeRepository.findByOffice_id(id).ifPresent(office -> {
               office.setStatus(Constants.A.name());
               officeRepository.save(office);
          });
     }

     public void permanentlyDeleteOffice(Long id) {
          officeRepository.deleteById(id);
     }
}
