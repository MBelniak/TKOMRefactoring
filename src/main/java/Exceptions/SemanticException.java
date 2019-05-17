package Exceptions;

public class SemanticException extends Exception {
    private String message;

    public SemanticException(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
