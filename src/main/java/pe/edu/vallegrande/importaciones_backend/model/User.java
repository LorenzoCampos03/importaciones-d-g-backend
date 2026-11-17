package pe.edu.vallegrande.importaciones_backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users", schema = "MARIA_SULCA")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long user_id;

    @Column(name = "username")
    private String username;

    @Column(name = "password")
    private String password;

    @Column(name = "role")
    private String role;

    @Column(name = "name")
    private String name;

    @Column(name = "last_name")
    private String last_name;

    @Column(name = "document_type")
    private String document_type;

    @Column(name = "document_number")
    private String document_number;

    @Column(name = "phone")
    private String phone;

    @Column(name = "email")
    private String email;

    @Column(name = "address")
    private String address;

    @Column(name = "registration_date")
    private LocalDate registration_date;

    @Column(name = "update_date")
    private LocalDate update_date;

    @Column(name = "razon_social", nullable = true)
    private String razon_social;

    @Column(name = "hire_date", nullable = true)
    private LocalDate hire_date;

    @Column(name = "office_id", nullable = true)
    private Integer office_id;
    @Column(name = "status")
    private String status;
}
