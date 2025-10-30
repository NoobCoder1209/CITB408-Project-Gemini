package com.printinghouse.model.publication;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Abstract base class for all publications.
 * Defines core properties and ensures equals/hashCode for use in maps.
 */
public abstract class Publication {
    protected String title;
    protected int pageCount;
    protected PageSize pageSize;
    protected BigDecimal basePricePerCopy;

    public Publication(String title, int pageCount, PageSize pageSize, BigDecimal basePricePerCopy) {
        this.title = title;
        this.pageCount = pageCount;
        this.pageSize = pageSize;
        this.basePricePerCopy = basePricePerCopy;
    }

    // Getters
    public String getTitle() {
        return title;
    }

    public int getPageCount() {
        return pageCount;
    }

    public PageSize getPageSize() {
        return pageSize;
    }

    public BigDecimal getBasePricePerCopy() {
        return basePricePerCopy;
    }

    @Override
    public String toString() {
        return String.format("%s (%s, %d pages)", title, pageSize, pageCount);
    }

    // Needed to be used as a key in maps (e.g., printedJobs, publicationsSold)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Publication that = (Publication) o;
        return pageCount == that.pageCount &&
                Objects.equals(title, that.title) &&
                pageSize == that.pageSize;
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, pageCount, pageSize);
    }
}
