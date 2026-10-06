package com.example.novabank.identity.Repository;

import com.example.novabank.identity.Model.ProfileModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProfileRepository extends JpaRepository<ProfileModel, Long> {

    boolean existsByEmail(String email);

    Optional<ProfileModel> findByEmail (String email);

    Optional<ProfileModel> findByUserId (Long UserId);

}
