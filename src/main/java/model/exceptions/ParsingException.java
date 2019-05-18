package model.exceptions;

public class ParsingException extends Exception {
    private String errorMessage;
    private int line;
    private int column;

    public ParsingException(String errorMessage, int line, int column) {
        this.errorMessage = errorMessage;
        this.line = line;
        this.column = column;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public int getLine() {
        return line;
    }

    public int getColumn() {
        return column;
    }
}
