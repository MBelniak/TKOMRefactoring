package Exceptions;

public class ParsingException extends Exception {
    private String errorMessage;
    private int line;
    private int column;

    public ParsingException(String errorMessage, int line, int column) {
        this.errorMessage = errorMessage;
        this.line = line;
        this.column = column;
    }
}
