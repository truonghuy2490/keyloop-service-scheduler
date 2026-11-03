package com.keyloop.scheduler.infrastructure.transaction;

import java.util.function.Supplier;

import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

import com.keyloop.scheduler.application.abstraction.transaction.TransactionExecutor;
import com.keyloop.scheduler.application.common.result.Result;

@Component
public class SpringTransactionExecutor implements TransactionExecutor {

    private final TransactionTemplate transactionTemplate;

    public SpringTransactionExecutor(
            TransactionTemplate transactionTemplate
    ) {
        this.transactionTemplate = transactionTemplate;
    }

    @Override
    public <T> T execute(Supplier<T> action) {
        return transactionTemplate.execute(
                status -> action.get()
        );
    }

    @Override
    public <T> Result<T> executeResult(Supplier<Result<T>> action) {
        return transactionTemplate.execute(status -> {

            Result<T> result = action.get();

            if (result == null) {
                throw new IllegalStateException(
                        "Transaction action returned null"
                );
            }

            if (result.isFailure()) {
                status.setRollbackOnly();
            }

            return result;
        });
    }
}