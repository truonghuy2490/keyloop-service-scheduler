"use client";

import { useEffect, useState } from "react";

import { useLazyGetDealershipServiceTypesQuery } from "../service-type.api";

import type { ServiceType } from "../service-type.types";

const PAGE_SIZE = 100;

export function useAllDealershipServiceTypes(dealershipId: string) {
    const [serviceTypes, setServiceTypes] = useState<ServiceType[]>([]);
    const [isLoading, setIsLoading] = useState(true);
    const [isError, setIsError] = useState(false);
    const [reloadKey, setReloadKey] = useState(0);

    const [getServiceTypes] =
        useLazyGetDealershipServiceTypesQuery();

    useEffect(() => {
        let cancelled = false;

        async function loadServiceTypes() {
            setIsLoading(true);
            setIsError(false);
            setServiceTypes([]);

            try {
                const allServices: ServiceType[] = [];
                let pageNumber = 1;
                let hasNextPage = true;

                while (hasNextPage) {
                    const response = await getServiceTypes({
                        dealershipId,
                        pageNumber,
                        pageSize: PAGE_SIZE,
                    }).unwrap();

                    allServices.push(...response.items);

                    hasNextPage = response.hasNextPage;
                    pageNumber += 1;
                }

                if (!cancelled) {
                    setServiceTypes(allServices);
                }
            } catch {
                if (!cancelled) {
                    setIsError(true);
                }
            } finally {
                if (!cancelled) {
                    setIsLoading(false);
                }
            }
        }

        if (dealershipId) {
            void loadServiceTypes();
        }

        return () => {
            cancelled = true;
        };
    }, [dealershipId, getServiceTypes, reloadKey]);

    return {
        serviceTypes,
        isLoading,
        isError,
        refetch: () => setReloadKey((previous) => previous + 1),
    };
}
