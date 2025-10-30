package com.printinghouse.service;

import com.printinghouse.model.paper.PaperType;
import com.printinghouse.model.publication.PageSize;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Manages the pricing model for paper.
 * Each PrintingHouse can own an instance of this with its own unique prices.
 */
public class PricingService {
    // Base price for A5 for each paper type
    private final Map<PaperType, BigDecimal> basePricesA5;
    // Percentage increase for each size up, e.g., 0.20 for 20%
    private final BigDecimal sizeIncreasePercentage;

    public PricingService(Map<PaperType, BigDecimal> basePricesA5, BigDecimal sizeIncreasePercentage) {
        this.basePricesA5 = basePricesA5;
        this.sizeIncreasePercentage = sizeIncreasePercentage;
    }

    /**
     * Calculates the price per sheet for a given paper type and size.
     * Starts with the A5 base price and applies the percentage increase cumulatively.
     */
    public BigDecimal calculatePaperPrice(PaperType paperType, PageSize pageSize) {
        BigDecimal basePrice = basePricesA5.get(paperType);
        if (basePrice == null) {
            throw new IllegalArgumentException("No base price set for " + paperType);
        }

        int sizeIndex = pageSize.getSizeIndex(); // A5=0, A4=1, ...
        if (sizeIndex == 0) {
            return basePrice;
        }

        // Formula: price = basePrice * (1 + increasePercentage)^sizeIndex
        BigDecimal multiplier = BigDecimal.ONE.add(sizeIncreasePercentage).pow(sizeIndex);
        return basePrice.multiply(multiplier);
    }
}
