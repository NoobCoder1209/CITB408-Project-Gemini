package com.printinghouse.model;

import java.math.BigDecimal;
import java.util.Map;

/**
 * A DTO (Data Transfer Object) to hold data loaded from a report file.
 */
public record FinancialReport(
        BigDecimal totalRevenue,
        BigDecimal totalExpenses,
        Map<String, Integer> publicationsSold
) {
}
