package pe.edu.vallegrande.importaciones_backend.service;

import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pe.edu.vallegrande.importaciones_backend.enums.Constants;
import pe.edu.vallegrande.importaciones_backend.model.User;
import pe.edu.vallegrande.importaciones_backend.repository.UserRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class EmployeeService {
    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;

    // Listar todos los vendedores activos
    public List<User> listAllEmployees() {
        return userRepository.findAll().stream()
                .filter(user -> user.getRole().equals(Constants.Vendedor.name()))
                .filter(user -> user.getStatus().equals(Constants.A.name()))
                .collect(Collectors.toList());
    }

    // Listar todos los vendedores (activos e inactivos)
    public List<User> listAllEmployeesIncludingInactive() {
        return userRepository.findAll().stream()
                .filter(user -> user.getRole().equals(Constants.Vendedor.name()))
                .collect(Collectors.toList());
    }

    // Obtener vendedor por ID
    public Optional<User> getEmployeeById(Long id) {
        return userRepository.findById(id)
                .filter(user -> user.getRole().equals(Constants.Vendedor.name()));
    }

    // Crear nuevo vendedor
    public User saveEmployee(User user) {
        Optional<User> existingUser = userRepository.findByUsername(user.getUsername());
        if (existingUser.isPresent()) {
            throw new RuntimeException("El nombre de usuario ya existe");
        }

        Optional<User> existingEmail = userRepository.findByEmail(user.getEmail());
        if (existingEmail.isPresent()) {
            throw new RuntimeException("El email ya está registrado");
        }

        user.setRole(Constants.Vendedor.name());
        user.setRegistration_date(LocalDate.now());
        user.setStatus(Constants.A.name());

        if (user.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }

        return userRepository.save(user);
    }

    public User updateEmployee(User user) {
        user.setUpdate_date(LocalDate.now());
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        return userRepository.save(user);
    }

    // Actualización parcial de vendedor
    public User updatePartialEmployee(Long id, User partialUser) {
        return userRepository.findById(id)
                .filter(user -> user.getRole().equals(Constants.Vendedor.name()))
                .map(existingEmployee -> {
                    if (partialUser.getName() != null) {
                        existingEmployee.setName(partialUser.getName());
                    }
                    if (partialUser.getLast_name() != null) {
                        existingEmployee.setLast_name(partialUser.getLast_name());
                    }
                    if (partialUser.getDocument_type() != null) {
                        existingEmployee.setDocument_type(partialUser.getDocument_type());
                    }
                    if (partialUser.getDocument_number() != null) {
                        existingEmployee.setDocument_number(partialUser.getDocument_number());
                    }
                    if (partialUser.getPhone() != null) {
                        existingEmployee.setPhone(partialUser.getPhone());
                    }
                    if (partialUser.getEmail() != null) {
                        existingEmployee.setEmail(partialUser.getEmail());
                    }
                    if (partialUser.getAddress() != null) {
                        existingEmployee.setAddress(partialUser.getAddress());
                    }
                    if (partialUser.getHire_date() != null) {
                        existingEmployee.setHire_date(partialUser.getHire_date());
                    }
                    if (partialUser.getOffice_id() != null) {
                        existingEmployee.setOffice_id(partialUser.getOffice_id());
                    }
                    if (partialUser.getPassword() != null && !partialUser.getPassword().isEmpty()) {
                        existingEmployee.setPassword(passwordEncoder.encode(partialUser.getPassword()));
                    }

                    existingEmployee.setUpdate_date(LocalDate.now());
                    return userRepository.save(existingEmployee);
                })
                .orElseThrow(() -> new RuntimeException("Vendedor no encontrado con id: " + id));
    }

    // Eliminación lógica
    public void deleteEmployee(Long id) {
        userRepository.findById(id)
                .filter(user -> user.getRole().equals(Constants.Vendedor.name()))
                .ifPresent(user -> {
                    user.setStatus(Constants.I.name());
                    user.setUpdate_date(LocalDate.now());
                    userRepository.save(user);
                });
    }

    // Restauración lógica
    public void restoreEmployee(Long id) {
        userRepository.findById(id)
                .filter(user -> user.getRole().equals(Constants.Vendedor.name()))
                .ifPresent(user -> {
                    user.setStatus(Constants.A.name());
                    user.setUpdate_date(LocalDate.now());
                    userRepository.save(user);
                });
    }

    // Eliminación física
    public void permanentlyDeleteEmployee(Long id) {
        userRepository.findById(id)
                .filter(user -> user.getRole().equals(Constants.Vendedor.name()))
                .ifPresent(user -> userRepository.deleteById(id));
    }

}
