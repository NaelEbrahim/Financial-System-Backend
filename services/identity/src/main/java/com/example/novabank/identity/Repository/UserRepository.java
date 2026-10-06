package com.example.novabank.identity.Repository;

import com.example.novabank.identity.Model.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserModel,Long> {

    Optional<UserModel> findByUsername(String userName);

    boolean existsByUsername(String username);

    Optional<UserModel> findByRefreshtoken (String refreshToken);

}
