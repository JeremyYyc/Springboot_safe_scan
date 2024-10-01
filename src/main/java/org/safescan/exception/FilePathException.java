package org.safescan.exception;

public class FilePathException extends RuntimeException{
    /**
     * Constructs a new runtime exception called MazeMalformedException with null as its detail message.
     * The cause is not initialized, and may subsequently be initialized by a call to initCause.
     * @param message the detail message. The detail message is saved for later retrieval by the getMessage() method.
     * */
    public FilePathException(String message) {
        super(message);
    }
}
