package com.printinghouse.model.publication;

/**
 * Represents standard paper sizes.
 * Includes a size index for pricing calculations (A5=0, A4=1, etc.).
 */
public enum PageSize {
    A5(0),
    A4(1),
    A3(2),
    A2(3),
    A1(4);

    private final int sizeIndex;

    PageSize(int sizeIndex) {
        this.sizeIndex = sizeIndex;
    }

    public int getSizeIndex() {
        return sizeIndex;
    }
}
