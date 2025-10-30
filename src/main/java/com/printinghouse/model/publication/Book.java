package com.printinghouse.model.publication;

import java.math.BigDecimal;

public class Book extends Publication {
    // Additional book-specific properties could go here (e.g., author, ISBN)
    public Book(String title, int pageCount, PageSize pageSize, BigDecimal basePricePerCopy) {
        super(title, pageCount, pageSize, basePricePerCopy);
    }
}
