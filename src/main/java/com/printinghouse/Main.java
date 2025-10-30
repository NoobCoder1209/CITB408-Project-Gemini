package com.printinghouse;

import com.printinghouse.model.PrintingHouse;
import com.printinghouse.model.PrintingHouseConfig;
import com.printinghouse.model.employee.Manager;
import com.printinghouse.model.employee.Operator;
import com.printinghouse.model.machine.PrintingMachine;
import com.printinghouse.model.paper.Paper;
import com.printinghouse.model.paper.PaperType;
import com.printinghouse.model.publication.Book;
import com.printinghouse.model.publication.Newspaper;
import com.printinghouse.model.publication.PageSize;
import com.printinghouse.service.PricingService;

import java.math.BigDecimal;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        System.out.println("--- Initializing Printing House ---");

        // 1. Setup Pricing
        Map<PaperType, BigDecimal> basePrices = Map.of(
                PaperType.PLAIN, new BigDecimal("0.05"),
                PaperType.GLOSSY, new BigDecimal("0.10"),
                PaperType.NEWSPAPER, new BigDecimal("0.03")
        );
        PricingService pricing = new PricingService(basePrices, new BigDecimal("0.15")); // 15% increase per size

        // 2. Setup Config
        PrintingHouseConfig config = new PrintingHouseConfig(
                new BigDecimal("10000.00"), // Manager bonus threshold
                1000,                       // Client discount copy threshold
                new BigDecimal("0.05")      // 5% client discount
        );

        // 3. Create Printing House
        PrintingHouse house = new PrintingHouse("QuickPrint Solutions", pricing, config);

        // 4. Hire Staff
        house.addEmployee(new Manager("Jane Doe", "M-001", new BigDecimal("60000.00"), new BigDecimal("0.10")));
        house.addEmployee(new Operator("John Smith", "O-001", new BigDecimal("40000.00")));

        // 5. Buy Machines
        PrintingMachine machineA4 = new PrintingMachine("M-A4-BW", false, 120, 10000);
        PrintingMachine machineA3 = new PrintingMachine("M-A3-Color", true, 60, 5000);
        house.addMachine(machineA4);
        house.addMachine(machineA3);

        // 6. Stock Inventory
        Paper a4Plain = new Paper(PaperType.PLAIN, PageSize.A4);
        Paper a3Glossy = new Paper(PaperType.GLOSSY, PageSize.A3);
        house.addPaperToInventory(a4Plain, 20000);
        house.addPaperToInventory(a3Glossy, 10000);

        // 7. Define Publications
        Book manual = new Book("Server Manual", 150, PageSize.A4, new BigDecimal("2.50"));
        Newspaper daily = new Newspaper("The Daily News", 40, PageSize.A3, new BigDecimal("1.00"));

        try {
            System.out.println("\n--- Starting Print Jobs ---");
            // Load paper
            machineA4.loadPaper(a4Plain, 8000);
            machineA3.loadPaper(a3Glossy, 3000);

            // Print
            machineA4.printPublication(manual, 50, false); // 50 * 150 = 7500 sheets
            machineA3.printPublication(daily, 75, true);   // 75 * 40 = 3000 sheets

            // Record sales
            house.recordSale(manual, 50);
            house.recordSale(daily, 75);

            // A large sale that triggers a discount
            System.out.println("Printing large order...");
            machineA4.loadPaper(a4Plain, 1500); // Load remaining paper
            // This next job needs 1100 * 150 = 165,000 sheets. It will fail.
            // machineA4.printPublication(manual, 1100, false);
            
            // Let's print a smaller "large" order
            machineA4.printPublication(manual, 10, false); // 10 * 150 = 1500 sheets
            
            // Now record a large sale (simulating it was printed elsewhere or over time)
            System.out.println("Recording large sale (1100 copies)...");
            house.recordSale(manual, 1100);

            BigDecimal smallPrice = house.calculateSalePrice(manual, 50);
            BigDecimal largePrice = house.calculateSalePrice(manual, 1100);
            System.out.println("Price for 50 copies: $" + smallPrice);
            System.out.println("Price for 1100 copies (with discount): $" + largePrice);


        } catch (Exception e) {
            System.err.println("!! Print Job Failed: " + e.getMessage());
        }

        // 8. Run Finances
        System.out.println("\n--- Running Financial Report ---");
        house.calculateTotalExpenses(); // Update expenses
        System.out.println("Total Revenue: $" + house.getTotalRevenue());
        System.out.println("Total Expenses: $" + house.getTotalExpenses());

        // 9. Save Report
        try {
            String reportFile = "financial_report.txt";
            house.saveReport(reportFile);
            System.out.println("\nSuccessfully saved report to " + reportFile);
        } catch (Exception e) {
            System.err.println("!! Failed to save report: " + e.getMessage());
        }
    }
}
