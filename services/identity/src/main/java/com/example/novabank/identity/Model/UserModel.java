package com.example.novabank.identity.Model;

import com.example.novabank.identity.Enum.UserStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


@Setter
@Getter
@Entity(name = "client")
public class UserModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String pin;

    private String refreshtoken;

    @Column(nullable = false)
    private UserStatus status = UserStatus.PENDING;

}
