package com.code.services.saga.steps;

import org.springframework.stereotype.Service;

import com.code.entities.Transaction;
import com.code.entities.TransactionStatus;
import com.code.repositories.TransactionRepository;
import com.code.services.saga.SagaContext;
import com.code.services.saga.SagaStep;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class UpdateTransactionStatusStep implements SagaStep {

    private final TransactionRepository transactionRepository;
    
    
    @Override
    public boolean execute(SagaContext context) {
        Long transactionId = context.getLong("transactionId");
        log.info("Updating transaction status id for transactionId: {}", transactionId);

        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new IllegalArgumentException("Transaction not found with id: " + transactionId));

        context.put("originalTransactionStatus", transaction.getStatus()); // Store the original status for compensation

        transaction.setStatus(TransactionStatus.SUCCESS);
        transactionRepository.save(transaction);
        log.info("Transaction status updated to SUCCESS for transactionId: {}", transactionId);

        context.put("transactionStatusAfterUpdate", transaction.getStatus()); // Store the new status for compensation

        log.info("Update transaction status step executed successfully for transactionId: {}", transactionId);
        return true;
    }

    @Override
    public boolean compensate(SagaContext context) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'compensate'");
    }

    @Override
    public String getStepName() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getStepName'");
    }

    


}
