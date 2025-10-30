package com.printinghouse.exception;

public class MachineCapacityExceededException extends Exception {
    public MachineCapacityExceededException(String message) {
        super(message);
    }
}
