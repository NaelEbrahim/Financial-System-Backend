package com.example.novabank.identity.Repository;

import com.example.novabank.identity.Enum.UserRole;
import com.example.novabank.identity.Model.User_RoleModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface User_RoleRepository extends JpaRepository<User_RoleModel, Long> {

    List<UserRole> findByUserId(long UserId);

}
