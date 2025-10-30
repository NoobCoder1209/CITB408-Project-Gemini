package com.printinghouse.service;

import com.printinghouse.model.FinancialReport;
import com.printinghouse.model.employee.Employee;

import java.io.*;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Handles serialization and text file I/O operations.
 */
public class FileService {

    /**
     * Serializes a list of employees to a binary file.
     */
    public static void saveEmployees(List<Employee> employees, String filename) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
            oos.writeObject(employees);
        }
    }

    /**
     * Deserializes a list of employees from a binary file.
     */
    @SuppressWarnings("unchecked")
    public static List<Employee> loadEmployees(String filename) throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename))) {
            return (List<Employee>) ois.readObject();
        }
    }

    /**
     * Loads a financial report from a text file.
     *
     * @param filename The file to read from.
     * @return A FinancialReport DTO.
     * @throws IOException If the file cannot be read.
     */
    public static FinancialReport loadReport(String filename) throws IOException {
        BigDecimal revenue = BigDecimal.ZERO;
        BigDecimal expenses = BigDecimal.ZERO;
        Map<String, Integer> sales = new HashMap<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            String section = "";
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("Total Revenue:")) {
                    revenue = new BigDecimal(line.split(": ")[1]);
                } else if (line.startsWith("Total Expenses:")) {
                    expenses = new BigDecimal(line.split(": ")[1]);
                } else if (line.equals("--- Publications Sold ---")) {
                    section = "sales";
                } else if (section.equals("sales") && !line.isBlank()) {
                    String[] parts = line.split(" \(Copies: ");
                    String title = parts[0];
                    int copies = Integer.parseInt(parts[1].replace(")", ""));
                    sales.put(title, copies);
                }
            }
        }
        return new FinancialReport(revenue, expenses, sales);
    }
}
