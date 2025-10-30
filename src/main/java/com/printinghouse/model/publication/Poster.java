package com.printinghouse.model.publication;

import java.math.BigDecimal;

public class Poster extends Publication {
    public Poster(String title, PageSize pageSize, BigDecimal basePricePerCopy) {
        // A poster is typically one page
        super(title, 1, pageSize, basePricePerCopy);
    }
}
