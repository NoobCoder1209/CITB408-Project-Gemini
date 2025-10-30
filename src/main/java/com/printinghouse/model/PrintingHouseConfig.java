package com.printinghouse.model;

import java.math.BigDecimal;

/**
 * A configuration object for the PrintingHouse.
 * Implemented as a record for simplicity and immutability.
 */
public record PrintingHouseConfig(
        BigDecimal managerRevenueThreshold,
        int clientDiscountThreshold,
        BigDecimal clientDiscountPercentage
) {
}
