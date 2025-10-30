package com.printinghouse;

import com.printinghouse.model.PrintingHouse;
import com.printinghouse.model.PrintingHouseConfig;
import com.printinghouse.model.employee.Manager;
import com.printinghouse.model.employee.Operator;
import com.printinghouse.model.paper.Paper;
import com.printinghouse.model.paper.PaperType;
import com.printinghouse.model.publication.Book;
import com.printinghouse.model.publication.PageSize;
import com.printinghouse.model.publication.Publication;
import com.printinghouse.service.PricingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PrintingHouseTest {

    private PrintingHouse house;
    private Publication book;

    @BeforeEach
    void setUp() {
        // 1. Pricing
        Map<PaperType, BigDecimal> basePrices = Map.of(PaperType.PLAIN, new BigDecimal("0.10")); // A5
        PricingService pricing = new PricingService(basePrices, new BigDecimal("1.00")); // Doubles per size
        // A4 price = 0.10 * (1+1)^1 = 0.20

        // 2. Config
        PrintingHouseConfig config = new PrintingHouseConfig(
                new BigDecimal("1000"), // Manager threshold
                100,                    // Discount threshold
                new BigDecimal("0.10")  // 10% discount
        );

        // 3. House
        house = new PrintingHouse("Test House", pricing, config);

        // 4. Staff & Inventory
        house.addEmployee(new Operator("Op", "O-1", new BigDecimal("100")));
        house.addEmployee(new Manager("Mgr", "M-1", new BigDecimal("200"), new BigDecimal("0.50"))); // 50% bonus
        house.addPaperToInventory(new Paper(PaperType.PLAIN, PageSize.A4), 1000); // 1000 sheets @ 0.20/ea = 200

        // 5. Publication
        book = new Book("Test Book", 100, PageSize.A4, new BigDecimal("10.00")); // 10.00 base price
    }

    private BigDecimal scale(BigDecimal val) {
        return val.setScale(2, RoundingMode.HALF_UP);
    }

    @Test
    void testCalculateSalePriceNoDiscount() {
        BigDecimal price = house.calculateSalePrice(book, 50); // 50 copies < 100 threshold
        // 50 * 10.00 = 500
        assertEquals(scale(new BigDecimal("500.00")), scale(price));
    }

    @Test
    void testCalculateSalePriceWithDiscount() {
        BigDecimal price = house.calculateSalePrice(book, 200); // 200 copies > 100 threshold
        // Price per copy = 10.00 * (1 - 0.10) = 9.00
        // Total = 200 * 9.00 = 1800
        assertEquals(scale(new BigDecimal("1800.00")), scale(price));
    }

    @Test
    void testCalculateTotalExpenses_NoBonus() {
        // No revenue yet
        house.calculateTotalExpenses();

        // Salaries: Op=100, Mgr=200 (no bonus)
        // Paper: 1000 sheets * 0.20/ea = 200
        // Total = (100 + 200) + 200 = 500
        assertEquals(scale(new BigDecimal("500.00")), scale(house.getTotalExpenses()));
    }

    @Test
    void testCalculateTotalExpenses_WithBonus() {
        // Record a sale to get revenue
        house.recordSale(book, 200); // Revenue = 1800 (from other test)
        // Revenue 1800 > Threshold 1000

        house.calculateTotalExpenses();

        // Salaries:
        // Op = 100
        // Mgr = 200 + (200 * 0.50 bonus) = 300
        // Paper: 1000 sheets * 0.20/ea = 200
        // Total = (100 + 300) + 200 = 600
        assertEquals(scale(new BigDecimal("600.00")), scale(house.getTotalExpenses()));
    }
}
