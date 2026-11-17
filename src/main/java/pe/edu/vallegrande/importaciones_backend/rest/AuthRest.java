package pe.edu.vallegrande.importaciones_backend.rest;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.vallegrande.importaciones_backend.dto.ChangePasswordRequest;
import pe.edu.vallegrande.importaciones_backend.dto.LoginRequest;
import pe.edu.vallegrande.importaciones_backend.dto.LoginResponse;
import pe.edu.vallegrande.importaciones_backend.dto.RefreshTokenRequest;
import pe.edu.vallegrande.importaciones_backend.model.User;
import pe.edu.vallegrande.importaciones_backend.service.AuthService;

@RestController
@RequestMapping("/api/v1/auth")
@CrossOrigin(origins = "*")
@AllArgsConstructor
public class AuthRest {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        try {
            LoginResponse response = authService.authenticate(loginRequest);

            if (response.isSuccess()) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
        } catch (Exception e) {
            LoginResponse errorResponse = LoginResponse.builder()
                    .success(false)
                    .message("Error interno del servidor")
                    .build();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        try {
            User savedUser = authService.registerUser(user);

            savedUser.setPassword(null);

            return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Error interno del servidor"));
        }
    }

    @PutMapping("/change-password/{userId}")
    public ResponseEntity<?> changePassword(
            @PathVariable Long userId,
            @RequestBody ChangePasswordRequest request) {
        try {
            boolean success = authService.changePassword(
                    userId,
                    request.getOldPassword(),
                    request.getNewPassword());

            if (success) {
                return ResponseEntity.ok(new SuccessResponse("Contraseña actualizada correctamente"));
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ErrorResponse("Contraseña actual incorrecta o usuario no encontrado"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Error interno del servidor"));
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<LoginResponse> refresh(@RequestBody RefreshTokenRequest request) {
        try {
            LoginResponse response = authService.refreshToken(request.getRefreshToken());

            if (response.isSuccess()) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
        } catch (Exception e) {
            LoginResponse errorResponse = LoginResponse.builder()
                    .success(false)
                    .message("Error interno del servidor")
                    .build();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestParam String username) {
        try {
            boolean success = authService.logout(username);

            if (success) {
                return ResponseEntity.ok(new SuccessResponse("Logout exitoso"));
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ErrorResponse("Error en logout"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Error interno del servidor"));
        }
    }

    @GetMapping("/validate/username/{username}")
    public ResponseEntity<?> validateUsername(@PathVariable String username) {
        try {
            boolean exists = authService.usernameExists(username);
            return ResponseEntity.ok(new ValidationResponse("username", exists));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Error interno del servidor"));
        }
    }

    @GetMapping("/validate/email/{email}")
    public ResponseEntity<?> validateEmail(@PathVariable String email) {
        try {
            boolean exists = authService.emailExists(email);
            return ResponseEntity.ok(new ValidationResponse("email", exists));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Error interno del servidor"));
        }
    }

    @GetMapping("/validate/document")
    public ResponseEntity<?> validateDocument(@RequestParam String documentType, @RequestParam String documentNumber) {
        try {
            boolean exists = authService.documentExists(documentType, documentNumber);
            return ResponseEntity.ok(new ValidationResponse("document", exists));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Error interno del servidor"));
        }
    }

    @GetMapping("/validate/phone/{phone}")
    public ResponseEntity<?> validatePhone(@PathVariable String phone) {
        try {
            boolean exists = authService.phoneExists(phone);
            return ResponseEntity.ok(new ValidationResponse("phone", exists));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Error interno del servidor"));
        }
    }

    @Data
    @AllArgsConstructor
    public static class ErrorResponse {
        private String message;
    }

    @Data
    @AllArgsConstructor
    public static class SuccessResponse {
        private String message;
    }

    @Data
    @AllArgsConstructor
    public static class ValidationResponse {
        private String field;
        private boolean exists;
        private String message;

        public ValidationResponse(String field, boolean exists) {
            this.field = field;
            this.exists = exists;
            this.message = exists ? field + " ya está en uso" : field + " está disponible";
        }
    }
}
