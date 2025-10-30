package com.printinghouse.model.publication;

import java.math.BigDecimal;

public class Newspaper extends Publication {
    public Newspaper(String title, int pageCount, PageSize pageSize, BigDecimal basePricePerCopy) {
        super(title, pageCount, pageSize, basePricePerCopy);
    }
}
