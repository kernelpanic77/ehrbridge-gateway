package com.ehrbridge.gateway.repository;

import com.ehrbridge.gateway.entity.Transactions;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transactions, String> {
    Optional<Transactions> findByTxnId(String txnId);
}
