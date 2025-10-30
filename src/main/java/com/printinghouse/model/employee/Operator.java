package com.printinghouse.model.employee;

import java.math.BigDecimal;

public class Operator extends Employee {
    @java.io.Serial
    private static final long serialVersionUID = 2L;

    public Operator(String name, String employeeID, BigDecimal baseSalary) {
        super(name, employeeID, baseSalary);
    }

    /**
     * An Operator's salary is just their base salary.
     */
    @Override
    public BigDecimal calculateSalary() {
        return this.baseSalary;
    }
}
