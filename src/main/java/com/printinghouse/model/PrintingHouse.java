package com.printinghouse.model;

import com.printinghouse.model.employee.Employee;
import com.printinghouse.model.employee.Manager;
import com.printinghouse.model.machine.PrintingMachine;
import com.printinghouse.model.paper.Paper;
import com.printinghouse.model.publication.Publication;
import com.printinghouse.service.PricingService;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The main class representing the entire business.
 * It owns the employees, machines, inventory, and finances.
 */
public class PrintingHouse {
    private final String name;
    private final List<Employee> employees;
    private final List<PrintingMachine> machines;
    private final Map<Paper, Integer> paperInventory; // Tracks total stock
    private final Map<Publication, Integer> publicationsSold;
    private final PricingService pricingService;
    private final PrintingHouseConfig config;

    private BigDecimal totalRevenue;
    private BigDecimal totalExpenses;

    public PrintingHouse(String name, PricingService pricingService, PrintingHouseConfig config) {
        this.name = name;
        this.pricingService = pricingService;
        this.config = config;
        this.employees = new ArrayList<>();
        this.machines = new ArrayList<>();
        this.paperInventory = new HashMap<>();
        this.publicationsSold = new HashMap<>();
        this.totalRevenue = BigDecimal.ZERO;
        this.totalExpenses = BigDecimal.ZERO;
    }

    // --- Core Business Logic ---

    /**
     * Calculates the total sale price for a publication, applying discounts if applicable.
     */
    public BigDecimal calculateSalePrice(Publication publication, int copies) {
        BigDecimal basePricePerCopy = publication.getBasePricePerCopy();
        BigDecimal finalPricePerCopy = basePricePerCopy;

        if (copies > config.clientDiscountThreshold()) {
            BigDecimal discount = BigDecimal.ONE.subtract(config.clientDiscountPercentage());
            finalPricePerCopy = basePricePerCopy.multiply(discount);
        }

        return finalPricePerCopy.multiply(BigDecimal.valueOf(copies));
    }

    /**
     * Records a sale, updating revenue and the sales log.
     */
    public void recordSale(Publication publication, int copies) {
        BigDecimal salePrice = calculateSalePrice(publication, copies);
        this.totalRevenue = this.totalRevenue.add(salePrice);
        this.publicationsSold.put(publication, this.publicationsSold.getOrDefault(publication, 0) + copies);
    }

    /**
     * Calculates total expenses from salaries and paper costs.
     * This method updates the totalExpenses property.
     */
    public void calculateTotalExpenses() {
        // 1. Calculate Salary Costs
        BigDecimal totalSalaries = BigDecimal.ZERO;
        for (Employee emp : employees) {
            if (emp instanceof Manager manager) {
                // Use the manager-specific method with bonus logic
                totalSalaries = totalSalaries.add(
                        manager.calculateSalary(this.totalRevenue, config.managerRevenueThreshold())
                );
            } else {
                // Use the standard employee method
                totalSalaries = totalSalaries.add(emp.calculateSalary());
            }
        }

        // 2. Calculate Paper Costs (based on current inventory)
        // Note: A real system would track *consumed* paper. This calculates inventory *value*.
        BigDecimal totalPaperCosts = BigDecimal.ZERO;
        for (Map.Entry<Paper, Integer> entry : paperInventory.entrySet()) {
            Paper paper = entry.getKey();
            int quantity = entry.getValue();
            BigDecimal pricePerSheet = pricingService.calculatePaperPrice(paper.paperType(), paper.pageSize());
            BigDecimal cost = pricePerSheet.multiply(BigDecimal.valueOf(quantity));
            totalPaperCosts = totalPaperCosts.add(cost);
        }

        this.totalExpenses = totalSalaries.add(totalPaperCosts);
    }

    // --- I/O Methods ---

    /**
     * Saves a human-readable financial report to a text file.
     */
    public void saveReport(String filename) throws IOException {
        // Ensure expenses are up-to-date before saving
        calculateTotalExpenses();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            writer.write("--- Printing House Financial Report ---");
            writer.newLine();
            writer.write("Name: " + this.name);
            writer.newLine();
            writer.newLine();
            writer.write("Total Revenue: " + this.totalRevenue);
            writer.newLine();
            writer.write("Total Expenses: " + this.totalExpenses);
            writer.newLine();

            BigDecimal netProfit = this.totalRevenue.subtract(this.totalExpenses);
            writer.write("Net Profit: " + netProfit);
            writer.newLine();
            writer.newLine();
            writer.write("--- Publications Sold ---");
            writer.newLine();
            if (publicationsSold.isEmpty()) {
                writer.write("No sales recorded.");
                writer.newLine();
            } else {
                for (Map.Entry<Publication, Integer> entry : publicationsSold.entrySet()) {
                    writer.write(entry.getKey().getTitle() + " (Copies: " + entry.getValue() + ")");
                    writer.newLine();
                }
            }
        }
    }

    // --- Entity Management ---

    public void addEmployee(com.printinghouse.model.employee.Employee employee) {
        this.employees.add(employee);
    }

    public void addMachine(PrintingMachine machine) {
        this.machines.add(machine);
    }

    public void addPaperToInventory(com.printinghouse.model.paper.Paper paper, int amount) {
        this.paperInventory.put(paper, this.paperInventory.getOrDefault(paper, 0) + amount);
    }

    // Getters
    public String getName() { return name; }
    public java.util.List<com.printinghouse.model.employee.Employee> getEmployees() { return java.util.List.copyOf(employees); }
    public java.util.List<PrintingMachine> getMachines() { return java.util.List.copyOf(machines); }
    public java.util.Map<com.printinghouse.model.paper.Paper, Integer> getPaperInventory() { return java.util.Map.copyOf(paperInventory); }
    public java.util.Map<com.printinghouse.model.publication.Publication, Integer> getPublicationsSold() { return java.util.Map.copyOf(publicationsSold); }
    public BigDecimal getTotalRevenue() { return totalRevenue; }
    public BigDecimal getTotalExpenses() { return totalExpenses; }
    public PrintingHouseConfig getConfig() { return config; }
    public com.printinghouse.service.PricingService getPricingService() { return pricingService; }
}
