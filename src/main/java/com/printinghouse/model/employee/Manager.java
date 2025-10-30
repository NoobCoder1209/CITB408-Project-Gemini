package com.printinghouse.model.employee;

import java.math.BigDecimal;

public class Manager extends Employee {
    @java.io.Serial
    private static final long serialVersionUID = 3L;

    private BigDecimal bonusPercentage; // e.g., 0.1 for 10%

    public Manager(String name, String employeeID, BigDecimal baseSalary, BigDecimal bonusPercentage) {
        super(name, employeeID, baseSalary);
        this.bonusPercentage = bonusPercentage;
    }

    /**
     * The default calculation returns the base salary, as required by the abstract class.
     * The PrintingHouse finance logic will call the overloaded method.
     */
    @Override
    public BigDecimal calculateSalary() {
        return this.baseSalary;
    }

    /**
     * Calculates salary including a potential bonus.
     *
     * @param currentRevenue     The printing house's current total revenue.
     * @param revenueThreshold   The threshold to exceed for a bonus.
     * @return Base salary, or base salary + bonus if revenue exceeds threshold.
     */
    public BigDecimal calculateSalary(BigDecimal currentRevenue, BigDecimal revenueThreshold) {
        // currentRevenue > revenueThreshold
        if (currentRevenue.compareTo(revenueThreshold) > 0) {
            BigDecimal bonus = this.baseSalary.multiply(this.bonusPercentage);
            return this.baseSalary.add(bonus);
        } else {
            return this.baseSalary;
        }
    }

    public BigDecimal getBonusPercentage() {
        return bonusPercentage;
    }
}
