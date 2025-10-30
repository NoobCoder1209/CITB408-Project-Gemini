package com.printinghouse.model.paper;

import com.printinghouse.model.publication.PageSize;

/**
 * Represents a type of paper.
 * Implemented as a record for immutability and automatic
 * equals(), hashCode(), and toString() - perfect for Map keys.
 */
public record Paper(PaperType paperType, PageSize pageSize) {
}
