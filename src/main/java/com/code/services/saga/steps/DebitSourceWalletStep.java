package com.code.services.saga.steps;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;

import com.code.entities.Wallet;
import com.code.repositories.WalletRepository;
import com.code.services.saga.SagaContext;
import com.code.services.saga.SagaStep;


import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class DebitSourceWalletStep implements SagaStep {

    private  final WalletRepository walletRepository;

    @Override
    @Transactional
    public boolean execute(SagaContext context) {
        //Step 1 : Fetch the source wallet id and amount from the context
        Long fromwalletId = context.getLong("fromWalletId");
        BigDecimal amount = context.getBigDecimal("amount");

        log.info("Executing {} for walletId: {} and amount: {}", getStepName(), fromwalletId, amount);

        //Step 2 : Fetch the source wallet from the database with lock
        Wallet fromWallet = walletRepository.findByWalletIdWithLock(fromwalletId)
                .orElseThrow(() -> new IllegalArgumentException("Source wallet not found"));
        log.info("Wallet fetched with balance {}", fromWallet.getBalance());
        
        context.put("originalSourceWalletBalance", fromWallet.getBalance()); // Store the wallet in context for compensation

        //Step 3 : Debit the amount from the source wallet and save it to the database 
        fromWallet.debit(amount);
        walletRepository.save(fromWallet);

        context.put("fromWalletBalanceAfterDebit", fromWallet.getBalance()); // Store the new balance in context for compensation
        log.info("Wallet debited. New balance: {}", fromWallet.getBalance());
        return true; 
    }

    @Override
    public boolean compensate(SagaContext context) {
        Long fromwalletId = context.getLong("fromWalletId");
        BigDecimal amount = context.getBigDecimal("amount");

        log.info("Compensating {} for walletId: {} and amount: {}", getStepName(), fromwalletId, amount);

        Wallet fromWallet = walletRepository.findByWalletIdWithLock(fromwalletId)
                .orElseThrow(() -> new IllegalArgumentException("Source wallet not found"));
        log.info("Wallet fetched for compensation with balance: {}", fromWallet.getBalance());
        context.put("sourceWalletBalanceBeforeCompensation", fromWallet.getBalance()); // Store the wallet balance before compensation for logging

        fromWallet.credit(amount);
        walletRepository.save(fromWallet);
        context.put("sourceWalletBalanceAfterCompensation", fromWallet.getBalance()); // Store the wallet balance after compensation for logging
        log.info("Wallet credited for compensation. New balance: {}", fromWallet.getBalance());

        log.info("Compensating source wallet step executed successfully");
        
        return true; 
    }

    @Override
    public String getStepName() {
        return "DebitSourceWallet";
    }

}
