package com.printinghouse.model.employee;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Abstract base class for employees.
 * Must implement Serializable for I/O requirements.
 */
public abstract class Employee implements Serializable {
    // Ensures version compatibility during serialization
    @java.io.Serial
    private static final long serialVersionUID = 1L;

    protected String name;
    protected String employeeID;
    protected BigDecimal baseSalary;

    public Employee(String name, String employeeID, BigDecimal baseSalary) {
        this.name = name;
        this.employeeID = employeeID;
        this.baseSalary = baseSalary;
    }

    /**
     * Calculates the employee's final salary.
     * This may be overridden by subclasses with more complex logic.
     */
    public abstract BigDecimal calculateSalary();

    // Getters
    public String getName() {
        return name;
    }

    public String getEmployeeID() {
        return employeeID;
    }

    public BigDecimal getBaseSalary() {
        return baseSalary;
    }

    @Override
    public String toString() {
        return String.format("Employee[ID=%s, Name=%s, BaseSalary=%.2f]", employeeID, name, baseSalary);
    }
}
