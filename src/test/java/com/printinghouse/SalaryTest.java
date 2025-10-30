package com.printinghouse;

import com.printinghouse.model.employee.Employee;
import com.printinghouse.model.employee.Manager;
import com.printinghouse.model.employee.Operator;
import com.printinghouse.service.FileService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SalaryTest {

    @Test
    void testOperatorSalary() {
        Operator op = new Operator("Test Op", "O-1", new BigDecimal("50000"));
        assertEquals(0, new BigDecimal("50000").compareTo(op.calculateSalary()));
    }

    @Test
    void testManagerSalaryNoBonus() {
        Manager mgr = new Manager("Test Mgr", "M-1", new BigDecimal("80000"), new BigDecimal("0.10"));
        BigDecimal revenue = new BigDecimal("10000");
        BigDecimal threshold = new BigDecimal("20000");

        // Uses the overloaded method
        BigDecimal salary = mgr.calculateSalary(revenue, threshold);
        assertEquals(0, new BigDecimal("80000").compareTo(salary));
    }

    @Test
    void testManagerSalaryWithBonus() {
        Manager mgr = new Manager("Test Mgr", "M-1", new BigDecimal("80000"), new BigDecimal("0.10"));
        BigDecimal revenue = new BigDecimal("25000");
        BigDecimal threshold = new BigDecimal("20000");

        // Bonus = 80000 * 0.10 = 8000
        // Total = 80000 + 8000 = 88000
        BigDecimal expected = new BigDecimal("88000");
        BigDecimal salary = mgr.calculateSalary(revenue, threshold);
        assertEquals(0, expected.compareTo(salary));
    }

    @Test
    void testEmployeeSerialization(@TempDir Path tempDir) throws Exception {
        File file = tempDir.resolve("employees.dat").toFile();
        Manager originalManager = new Manager("Serial Mgr", "S-1", new BigDecimal("90000"), new BigDecimal("0.20"));
        List<Employee> employees = List.of(originalManager);

        // Act: Save
        FileService.saveEmployees(employees, file.getAbsolutePath());

        // Act: Load
        List<Employee> loadedEmployees = FileService.loadEmployees(file.getAbsolutePath());

        // Assert
        assertEquals(1, loadedEmployees.size());
        Employee loaded = loadedEmployees.get(0);

        assertTrue(loaded instanceof Manager);
        Manager loadedManager = (Manager) loaded;

        assertEquals(originalManager.getName(), loadedManager.getName());
        assertEquals(originalManager.getEmployeeID(), loadedManager.getEmployeeID());
        assertEquals(0, originalManager.getBaseSalary().compareTo(loadedManager.getBaseSalary()));
        assertEquals(0, originalManager.getBonusPercentage().compareTo(loadedManager.getBonusPercentage()));
    }
}
