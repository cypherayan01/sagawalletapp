package com.code.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.web.servlet.tags.TransformTag;

import com.code.entities.Transaction;
import com.code.entities.TransactionStatus;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findByFromWalletId(Long fromWalletId);

    List<Transaction> findByToWalletId(Long toWalletId);

    @Query("SELECT t FROM Transaction t WHERE t.fromWallet.id = :walletId OR t.toWallet.id = :walletId")
    List<Transaction> findByWalletId(@Param("walletId") Long walletId);

    List<Transaction> findByStatus(TransactionStatus status);

    List<Transaction> findBySagaInstanceId(Long sagaInstanceId);
    

}
