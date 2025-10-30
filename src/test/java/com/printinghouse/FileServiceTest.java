package com.printinghouse;

import com.printinghouse.model.FinancialReport;
import com.printinghouse.model.PrintingHouse;
import com.printinghouse.model.PrintingHouseConfig;
import com.printinghouse.model.publication.Book;
import com.printinghouse.model.publication.PageSize;
import com.printinghouse.service.FileService;
import com.printinghouse.service.PricingService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FileServiceTest {

    @Test
    void testSaveAndLoadReport(@TempDir Path tempDir) throws Exception {
        File file = tempDir.resolve("report.txt").toFile();
        String filename = file.getAbsolutePath();

        // 1. Setup House
        PricingService pricing = new PricingService(Map.of(), BigDecimal.ZERO);
        PrintingHouseConfig config = new PrintingHouseConfig(BigDecimal.ZERO, 0, BigDecimal.ZERO);
        PrintingHouse house = new PrintingHouse("Test House", pricing, config);

        Book book = new Book("Test Book", 100, PageSize.A4, new BigDecimal("10.00"));
        house.recordSale(book, 25); // Revenue = 250

        // This will set totalExpenses to 0 as there are no employees or paper
        house.calculateTotalExpenses();

        // 2. Save
        house.saveReport(filename);

        // 3. Load
        FinancialReport report = FileService.loadReport(filename);

        // 4. Assert
        assertEquals(0, new BigDecimal("250").compareTo(report.totalRevenue()));
        assertEquals(0, BigDecimal.ZERO.compareTo(report.totalExpenses()));
        assertEquals(1, report.publicationsSold().size());
        assertEquals(25, report.publicationsSold().get("Test Book").intValue());
    }
}
