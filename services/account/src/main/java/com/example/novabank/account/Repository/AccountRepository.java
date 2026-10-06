package com.example.novabank.account.Repository;

import com.example.novabank.account.Model.AccountModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<AccountModel, Long> {

    boolean existsByUserId(Long userId);

    boolean existsByWalletKey(String walletKey);

    Optional<AccountModel> findByUserId(Long userId);

    Optional<AccountModel> findByWalletKey(String walletKey);

}
