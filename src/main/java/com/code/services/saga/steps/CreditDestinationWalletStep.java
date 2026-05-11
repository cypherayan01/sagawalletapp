package com.code.services.saga.steps;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.code.entities.Wallet;
import com.code.repositories.WalletRepository;
import com.code.services.saga.SagaContext;
import com.code.services.saga.SagaStep;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class CreditDestinationWalletStep implements SagaStep {

    private final WalletRepository walletRepository;

    @Override
    @Transactional
    public boolean execute(SagaContext context) {
        //Step 1 : Fetch the destination wallet id from the context
        Long toWalletId = context.getLong("toWalletId");
        BigDecimal amount = context.getBigDecimal("amount");

        log.info("Executing {} for walletId: {} and amount: {}", getStepName(), toWalletId, amount);
        
        //Step 2 : Fetch the destination wallet from the database with lock
        Wallet toWallet = walletRepository.findByWalletIdWithLock(toWalletId)
                .orElseThrow(() -> new IllegalArgumentException("Destination wallet not found"));

        log.info("Wallet fetched with balance: {}", toWallet.getBalance());
        context.put("originalToWalletBalance", toWallet.getBalance()); // Store the wallet in context for compensation

        //Step 3 : Credit the amount to the destination wallet
        toWallet.credit(amount);
        walletRepository.save(toWallet);
        log.info("Wallet credited. New balance: {}", toWallet.getBalance());
        context.put("toWalletBalanceAfterCredit", toWallet.getBalance()); // Store the new balance in context for compensation
        
        log.info("{} executed successfully for walletId: {} and amount: {}", getStepName(), toWalletId, amount);

        return true;
    }

    @Override
    @Transactional
    public boolean compensate(SagaContext context) {
        //Step 1 : Fetch the destination wallet id from the context
        Long toWalletId = context.getLong("toWalletId");
        BigDecimal amount = context.getBigDecimal("amount");

        log.info("Executing {} for walletId: {} and amount: {}", getStepName(), toWalletId, amount);
        
        //Step 2 : Fetch the destination wallet from the database with lock
        Wallet toWallet = walletRepository.findByWalletIdWithLock(toWalletId)
                .orElseThrow(() -> new IllegalArgumentException("Destination wallet not found"));

        log.info("Wallet fetched with balance: {}", toWallet.getBalance());
        

        //Step 3 : Credit the amount to the destination wallet
        toWallet.debit(amount);
        walletRepository.save(toWallet);
        log.info("Wallet debited. New balance: {}", toWallet.getBalance());
        context.put("toWalletBalanceAfterCreditCompensation", toWallet.getBalance()); 
        
        log.info("Credit compensation of destination wallet executed successfully");

        return true;
    }

    @Override
    public String getStepName() {
        return "CreditDestinationWalletStep";
    }

}
