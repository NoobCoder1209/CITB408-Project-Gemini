package com.printinghouse;

import com.printinghouse.model.paper.PaperType;
import com.printinghouse.model.publication.PageSize;
import com.printinghouse.service.PricingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PricingServiceTest {

    private PricingService pricingService;

    @BeforeEach
    void setUp() {
        Map<PaperType, BigDecimal> basePrices = Map.of(
                PaperType.PLAIN, new BigDecimal("0.10"), // 10 cents for A5
                PaperType.GLOSSY, new BigDecimal("0.50")  // 50 cents for A5
        );
        // 100% markup per size (doubles)
        BigDecimal sizeIncrease = new BigDecimal("1.00");
        pricingService = new PricingService(basePrices, sizeIncrease);
    }

    private BigDecimal scale(BigDecimal val) {
        return val.setScale(2, RoundingMode.HALF_UP);
    }

    @Test
    void testA5Price() {
        BigDecimal price = pricingService.calculatePaperPrice(PaperType.PLAIN, PageSize.A5);
        assertEquals(scale(new BigDecimal("0.10")), scale(price));
    }

    @Test
    void testA4Price() {
        // A4 is 1 size up from A5. Price = 0.10 * (1 + 1.00)^1 = 0.20
        BigDecimal price = pricingService.calculatePaperPrice(PaperType.PLAIN, PageSize.A4);
        assertEquals(scale(new BigDecimal("0.20")), scale(price));
    }

    @Test
    void testA3Price() {
        // A3 is 2 sizes up from A5. Price = 0.50 * (1 + 1.00)^2 = 0.50 * 4 = 2.00
        BigDecimal price = pricingService.calculatePaperPrice(PaperType.GLOSSY, PageSize.A3);
        assertEquals(scale(new BigDecimal("2.00")), scale(price));
    }

    @Test
    void testA1Price() {
        // A1 is 4 sizes up from A5. Price = 0.10 * (1 + 1.00)^4 = 0.10 * 16 = 1.60
        BigDecimal price = pricingService.calculatePaperPrice(PaperType.PLAIN, PageSize.A1);
        assertEquals(scale(new BigDecimal("1.60")), scale(price));
    }
}
