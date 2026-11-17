package pe.edu.vallegrande.importaciones_backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import pe.edu.vallegrande.importaciones_backend.enums.Constants;
import pe.edu.vallegrande.importaciones_backend.model.User;
import pe.edu.vallegrande.importaciones_backend.repository.UserRepository;

import java.util.Arrays;
import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

     @Autowired
     private UserRepository userRepository;

     @Override
     public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
          Optional<User> userOpt = userRepository.findByUsernameAndStatus(username, Constants.A.name());

          if (userOpt.isEmpty()) {
               throw new UsernameNotFoundException("Usuario no encontrado: " + username);
          }

          User user = userOpt.get();

          return org.springframework.security.core.userdetails.User.builder()
                    .username(user.getUsername())
                    .password(user.getPassword())
                    .authorities(Arrays.asList(new SimpleGrantedAuthority("ROLE_" + user.getRole())))
                    .accountExpired(false)
                    .accountLocked(false)
                    .credentialsExpired(false)
                    .disabled(false)
                    .build();
     }
}
