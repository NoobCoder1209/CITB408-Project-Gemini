package com.printinghouse;

import com.printinghouse.exception.InvalidPrintRequestException;
import com.printinghouse.exception.MachineCapacityExceededException;
import com.printinghouse.exception.NotEnoughPaperException;
import com.printinghouse.model.machine.PrintingMachine;
import com.printinghouse.model.paper.Paper;
import com.printinghouse.model.paper.PaperType;
import com.printinghouse.model.publication.Book;
import com.printinghouse.model.publication.PageSize;
import com.printinghouse.model.publication.Publication;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

public class PrintingMachineTest {

    private PrintingMachine machine;
    private final Paper a4Plain = new Paper(PaperType.PLAIN, PageSize.A4);
    private final Publication book = new Book("Test Book", 100, PageSize.A4, BigDecimal.ONE);

    @BeforeEach
    void setUp() {
        machine = new PrintingMachine("M-01", true, 100, 1000);
    }

    @Test
    void testLoadPaperSuccess() throws Exception {
        machine.loadPaper(a4Plain, 500);
        assertEquals(500, machine.getCurrentPaperLoad());
        assertEquals(a4Plain, machine.getLoadedPaper());
    }

    @Test
    void testLoadPaperThrowsCapacityException() {
        assertThrows(MachineCapacityExceededException.class, () -> {
            machine.loadPaper(a4Plain, 1001);
        });
    }

    @Test
    void testLoadPaperThrowsMismatchedType() throws Exception {
        machine.loadPaper(a4Plain, 100);
        Paper a4Glossy = new Paper(PaperType.GLOSSY, PageSize.A4);
        assertThrows(InvalidPrintRequestException.class, () -> {
            machine.loadPaper(a4Glossy, 100);
        });
    }

    @Test
    void testPrintSuccess() throws Exception {
        machine.loadPaper(a4Plain, 500);
        machine.printPublication(book, 3, false); // 3 * 100 = 300 sheets

        assertEquals(200, machine.getCurrentPaperLoad());
        assertEquals(1, machine.getPrintedJobs().size());
        assertEquals(3, machine.getPrintedJobs().get(book).intValue());
        assertEquals(300, machine.getTotalPagesPrinted());
    }

    @Test
    void testPrintThrowsNotEnoughPaper() throws Exception {
        machine.loadPaper(a4Plain, 200); // Only 200 sheets
        assertThrows(NotEnoughPaperException.class, () -> {
            machine.printPublication(book, 3, false); // Needs 300
        });
    }

    @Test
    void testPrintThrowsInvalidColor() {
        PrintingMachine bwMachine = new PrintingMachine("M-02", false, 100, 1000);
        assertThrows(InvalidPrintRequestException.class, () -> {
            bwMachine.printPublication(book, 1, true); // Requesting color on B/W
        });
    }

    @Test
    void testPrintThrowsWrongPaperSize() throws Exception {
        machine.loadPaper(a4Plain, 500);
        Publication a3Book = new Book("A3 Book", 50, PageSize.A3, BigDecimal.ONE);
        assertThrows(InvalidPrintRequestException.class, () -> {
            machine.printPublication(a3Book, 1, false); // Machine has A4, book is A3
        });
    }
}
