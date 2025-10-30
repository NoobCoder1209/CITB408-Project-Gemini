package com.printinghouse.model.machine;

import com.printinghouse.exception.InvalidPrintRequestException;
import com.printinghouse.exception.MachineCapacityExceededException;
import com.printinghouse.exception.NotEnoughPaperException;
import com.printinghouse.model.paper.Paper;
import com.printinghouse.model.publication.Publication;

import java.util.HashMap;
import java.util.Map;

public class PrintingMachine {
    private final String machineID;
    private final boolean isColor;
       private final int pagesPerMinute;
    private final int maxPaperCapacity;

    private int currentPaperLoad;
    private Paper loadedPaper; // Describes the type and size loaded
    private final Map<Publication, Integer> printedJobs;

    public PrintingMachine(String machineID, boolean isColor, int pagesPerMinute, int maxPaperCapacity) {
        this.machineID = machineID;
        this.isColor = isColor;
        this.pagesPerMinute = pagesPerMinute;
        this.maxPaperCapacity = maxPaperCapacity;
        this.currentPaperLoad = 0;
        this.loadedPaper = null;
        this.printedJobs = new HashMap<>();
    }

    /**
     * Loads paper into the machine.
     *
     * @param paper  The type and size of paper to load.
     * @param amount The number of sheets.
     * @throws MachineCapacityExceededException if amount exceeds capacity.
     * @throws InvalidPrintRequestException     if wrong paper type is loaded.
     */
    public void loadPaper(Paper paper, int amount) throws MachineCapacityExceededException, InvalidPrintRequestException {
        if (this.loadedPaper != null && !this.loadedPaper.equals(paper)) {
            throw new InvalidPrintRequestException("Cannot load " + paper + ". Machine is already loaded with " + this.loadedPaper);
        }

        if (this.currentPaperLoad + amount > this.maxPaperCapacity) {
            throw new MachineCapacityExceededException("Cannot load " + amount + " sheets. " +
                    "Max capacity is " + maxPaperCapacity + ", current load is " + currentPaperLoad);
        }

        this.loadedPaper = paper;
        this.currentPaperLoad += amount;
    }

    /**
     * Prints a given number of copies of a publication.
     *
     * @param publication The publication to print.
     * @param copies      The number of copies.
     * @param useColor    Whether the job requires color.
     * @throws InvalidPrintRequestException if color is requested on B/W machine or paper size mismatch.
     * @throws NotEnoughPaperException      if not enough paper is loaded.
     */
    public void printPublication(Publication publication, int copies, boolean useColor)
            throws InvalidPrintRequestException, NotEnoughPaperException {

        if (useColor && !this.isColor) {
            throw new InvalidPrintRequestException("Machine " + machineID + " is black and white. Cannot print in color.");
        }

        if (this.loadedPaper == null) {
            throw new NotEnoughPaperException("No paper is loaded in machine " + machineID);
        }

        if (publication.getPageSize() != this.loadedPaper.pageSize()) {
            throw new InvalidPrintRequestException("Wrong paper size. Publication requires " +
                    publication.getPageSize() + ", but machine is loaded with " + this.loadedPaper.pageSize());
        }

        int sheetsNeeded = publication.getPageCount() * copies;
        if (sheetsNeeded > this.currentPaperLoad) {
            throw new NotEnoughPaperException("Not enough paper. Job requires " + sheetsNeeded +
                    " sheets, but only " + this.currentPaperLoad + " are available.");
        }

        // Simulate printing
        this.currentPaperLoad -= sheetsNeeded;
        this.printedJobs.put(publication, this.printedJobs.getOrDefault(publication, 0) + copies);
        System.out.println("Machine " + machineID + ": Successfully printed " + copies + " copies of '" + publication.getTitle() + "'.");
    }

    /**
     * Calculates the total number of individual pages (sheets) printed by this machine.
     */
    public long getTotalPagesPrinted() {
        long total = 0;
        for (Map.Entry<Publication, Integer> entry : printedJobs.entrySet()) {
            long pagesPerCopy = entry.getKey().getPageCount();
            long copies = entry.getValue();
            total += (pagesPerCopy * copies);
        }
        return total;
    }

    // Getters
    public String getMachineID() { return machineID; }
    public boolean isColor() { return isColor; }
    public int getCurrentPaperLoad() { return currentPaperLoad; }
    public Paper getLoadedPaper() { return loadedPaper; }
    public Map<Publication, Integer> getPrintedJobs() { return Map.copyOf(printedJobs); }
}
