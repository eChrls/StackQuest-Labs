package com.lab10.advanced;

public class TransferNotFoundException extends RuntimeException {
    public TransferNotFoundException(long id) { super("Transfer " + id + " was not found"); }
}
