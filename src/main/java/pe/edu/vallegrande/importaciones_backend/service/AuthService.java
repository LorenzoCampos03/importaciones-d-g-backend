package pe.edu.vallegrande.importaciones_backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pe.edu.vallegrande.importaciones_backend.dto.LoginRequest;
import pe.edu.vallegrande.importaciones_backend.dto.LoginResponse;
import pe.edu.vallegrande.importaciones_backend.enums.Constants;
import pe.edu.vallegrande.importaciones_backend.model.User;
import pe.edu.vallegrande.importaciones_backend.repository.UserRepository;

import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    public LoginResponse authenticate(LoginRequest loginRequest) {
        Optional<User> userOpt = userRepository.findByUsernameAndStatus(
                loginRequest.getUsername(),
                Constants.A.name());

        if (userOpt.isEmpty()) {
            return LoginResponse.builder()
                    .success(false)
                    .message("Usuario no encontrado o inactivo")
                    .build();
        }

        User user = userOpt.get();

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            return LoginResponse.builder()
                    .success(false)
                    .message("Contraseña incorrecta")
                    .build();
        }
        String accessToken = jwtService.generateToken(user.getUsername(), user.getRole(), user.getUser_id());
        String refreshToken = jwtService.generateRefreshToken(user.getUsername());

        return LoginResponse.builder()
                .success(true)
                .message("Login exitoso")
                .token(accessToken)
                .refreshToken(refreshToken)
                .userId(user.getUser_id())
                .username(user.getUsername())
                .role(user.getRole())
                .name(user.getName())
                .lastName(user.getLast_name())
                .email(user.getEmail())
                .build();
    }

    public User registerUser(User user) {
        // username ya existe
        Optional<User> existingUser = userRepository.findByUsername(user.getUsername());
        if (existingUser.isPresent()) {
            throw new RuntimeException("El nombre de usuario ya existe");
        }

        // email ya existe
        Optional<User> existingEmail = userRepository.findByEmail(user.getEmail());
        if (existingEmail.isPresent()) {
            throw new RuntimeException("El email ya está registrado");
        }

        user.setStatus(Constants.A.name());
        user.setRegistration_date(java.time.LocalDate.now());

        if (user.getRole() == null || user.getRole().isEmpty()) {
            user.setRole(Constants.Cliente.name());
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        return userRepository.save(user);
    }

    public boolean changePassword(Long userId, String oldPassword, String newPassword) {
        Optional<User> userOpt = userRepository.findById(userId);

        if (userOpt.isEmpty()) {
            return false;
        }

        User user = userOpt.get();

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            return false;
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setUpdate_date(java.time.LocalDate.now());
        userRepository.save(user);
        return true;
    }

    public LoginResponse refreshToken(String refreshToken) {
        try {
            String username = jwtService.extractUsername(refreshToken);

            // Validar el refresh token con JWT
            if (!jwtService.validateRefreshToken(refreshToken, username)) {
                return LoginResponse.builder()
                        .success(false)
                        .message("Refresh token inválido")
                        .build();
            }

            // Buscar usuario para obtener datos actuales
            Optional<User> userOpt = userRepository.findByUsernameAndStatus(username, Constants.A.name());
            if (userOpt.isEmpty()) {
                return LoginResponse.builder()
                        .success(false)
                        .message("Usuario no encontrado")
                        .build();
            }

            User user = userOpt.get();

            // Generar nuevos tokens
            String newAccessToken = jwtService.generateToken(user.getUsername(), user.getRole(), user.getUser_id());
            String newRefreshToken = jwtService.generateRefreshToken(user.getUsername());

            return LoginResponse.builder()
                    .success(true)
                    .message("Token renovado exitosamente")
                    .token(newAccessToken)
                    .refreshToken(newRefreshToken)
                    .userId(user.getUser_id())
                    .username(user.getUsername())
                    .role(user.getRole())
                    .name(user.getName())
                    .lastName(user.getLast_name())
                    .email(user.getEmail())
                    .build();

        } catch (Exception e) {
            return LoginResponse.builder()
                    .success(false)
                    .message("Error al renovar token: " + e.getMessage())
                    .build();
        }
    }

    public boolean logout(String username) {
        return true;
    }

    public boolean usernameExists(String username) {
        return userRepository.findByUsername(username).isPresent();
    }

    public boolean emailExists(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    public boolean documentExists(String documentType, String documentNumber) {
        return userRepository.findByDocumentTypeAndDocumentNumber(documentType, documentNumber).isPresent();
    }

    public boolean phoneExists(String phone) {
        return userRepository.findByPhone(phone).isPresent();
    }
}
