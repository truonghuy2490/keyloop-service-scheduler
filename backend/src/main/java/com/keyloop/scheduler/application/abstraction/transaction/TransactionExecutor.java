package com.keyloop.scheduler.application.abstraction.transaction;

import java.util.function.Supplier;
import com.keyloop.scheduler.application.common.result.Result;

public interface TransactionExecutor {

    <T> T execute(Supplier<T> action);

    <T> Result<T> executeResult(Supplier<Result<T>> action);
}