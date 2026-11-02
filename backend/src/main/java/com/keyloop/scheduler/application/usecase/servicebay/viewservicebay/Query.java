package com.keyloop.scheduler.application.usecase.servicebay.viewservicebay;


public record Query(
        String dealershipId,
        int pageNumber,
        int pageSize
) {
    public Query {
        if (pageNumber < 1) {
            pageNumber = 1;
        }

        if (pageSize < 1) {
            pageSize = 10;
        }
    }
}
