package pe.edu.vallegrande.importaciones_backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pe.edu.vallegrande.importaciones_backend.model.User;
import pe.edu.vallegrande.importaciones_backend.repository.UserRepository;

import java.util.List;

@Service
public class PasswordMigrationService {

     @Autowired
     private UserRepository userRepository;

     @Autowired
     private PasswordEncoder passwordEncoder;

     /**
      * Migra todas las contraseñas en texto plano a BCrypt
      * EJECUTAR SOLO UNA VEZ después de implementar la seguridad
      */
     public void migratePasswords() {
          List<User> users = userRepository.findAll();

          for (User user : users) {
               if (user.getPassword() != null && !user.getPassword().startsWith("$2a$")) {
                    String hashedPassword = passwordEncoder.encode(user.getPassword());
                    user.setPassword(hashedPassword);
                    userRepository.save(user);
                    System.out.println("Contraseña migrada para usuario: " + user.getUsername());
               }
          }

          System.out.println("Migración de contraseñas completada");
     }
}
