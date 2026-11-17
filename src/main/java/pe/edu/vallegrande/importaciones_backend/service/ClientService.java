package pe.edu.vallegrande.importaciones_backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pe.edu.vallegrande.importaciones_backend.enums.Constants;
import pe.edu.vallegrande.importaciones_backend.model.User;
import pe.edu.vallegrande.importaciones_backend.repository.UserRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class ClientService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Listar todos los clientes activos
    public List<User> listAllClients() {
        return userRepository.findAllActiveClientsOrderedById(Constants.Cliente.name(), Constants.A.name());
    }

    // Listar todos los clientes (activos e inactivos)
    public List<User> listAllClientsIncludingInactive() {
        return userRepository.findAllClientsIncludingInactiveOrderedById(Constants.Cliente.name());
    }

    // Listar clientes por estado (A/I)
    public List<User> listClientsByStatus(String status) {
        return userRepository.findClientsByStatusOrderedById(Constants.Cliente.name(), status);
    }

    // Obtener cliente por ID
    public Optional<User> getClientById(Long id) {
        return userRepository.findById(id)
                .filter(user -> user.getRole().equals(Constants.Cliente.name()));
    }

    // Crear nuevo cliente
    public User saveClient(User user) {
        // Verificar si el username ya existe
        Optional<User> existingUser = userRepository.findByUsername(user.getUsername());
        if (existingUser.isPresent()) {
            throw new RuntimeException("El nombre de usuario ya existe");
        }

        // Verificar si el email ya existe
        Optional<User> existingEmail = userRepository.findByEmail(user.getEmail());
        if (existingEmail.isPresent()) {
            throw new RuntimeException("El email ya está registrado");
        }

        user.setStatus(Constants.A.name());
        user.setRole(Constants.Cliente.name());
        user.setRegistration_date(LocalDate.now());

        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }

        return userRepository.save(user);
    }

    // Actualizar cliente existente
    public User updateClient(User user) {
        user.setUpdate_date(LocalDate.now());
        return userRepository.save(user);
    }

    // Actualizar parcialmente un cliente
    public User updatePartialClient(Long id, User partialUser) {
        return userRepository.findById(id)
                .map(existingClient -> {
                    if (partialUser.getName() != null) {
                        existingClient.setName(partialUser.getName());
                    }
                    if (partialUser.getLast_name() != null) {
                        existingClient.setLast_name(partialUser.getLast_name());
                    }
                    if (partialUser.getDocument_type() != null) {
                        existingClient.setDocument_type(partialUser.getDocument_type());
                    }
                    if (partialUser.getDocument_number() != null) {
                        existingClient.setDocument_number(partialUser.getDocument_number());
                    }
                    if (partialUser.getPhone() != null) {
                        existingClient.setPhone(partialUser.getPhone());
                    }
                    if (partialUser.getEmail() != null) {
                        existingClient.setEmail(partialUser.getEmail());
                    }
                    if (partialUser.getAddress() != null) {
                        existingClient.setAddress(partialUser.getAddress());
                    }
                    if (partialUser.getRazon_social() != null) {
                        existingClient.setRazon_social(partialUser.getRazon_social());
                    }

                    existingClient.setUpdate_date(LocalDate.now());
                    return userRepository.save(existingClient);
                })
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado con id: " + id));
    }

    // Eliminación lógica
    public void deleteClient(Long id) {
        userRepository.findById(id).ifPresent(user -> {
            user.setStatus(Constants.I.name());
            user.setUpdate_date(LocalDate.now());
            userRepository.save(user);
        });
    }

    // Restauración lógica
    public void restoreClient(Long id) {
        userRepository.findById(id).ifPresent(user -> {
            user.setStatus(Constants.A.name());
            user.setUpdate_date(LocalDate.now());
            userRepository.save(user);
        });
    }

    // Eliminación física
    public void permanentlyDeleteClient(Long id) {
        userRepository.deleteById(id);
    }
}
