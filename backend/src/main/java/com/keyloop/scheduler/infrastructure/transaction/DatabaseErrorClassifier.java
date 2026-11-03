
package com.keyloop.scheduler.infrastructure.transaction;

import java.sql.SQLException;

import org.springframework.stereotype.Component;

@Component
public class DatabaseErrorClassifier {

    public boolean isRetryableConcurrencyError(
            Throwable exception
    ) {
        String sqlState = findSqlState(exception);

        return "40001".equals(sqlState)
                || "40P01".equals(sqlState);
    }

    public boolean isExclusionViolation(
            Throwable exception
    ) {
        return "23P01".equals(findSqlState(exception));
    }

    private String findSqlState(Throwable exception) {
        Throwable current = exception;

        while (current != null) {
            if (current instanceof SQLException sqlException) {
                return sqlException.getSQLState();
            }

            current = current.getCause();
        }

        return null;
    }
}
