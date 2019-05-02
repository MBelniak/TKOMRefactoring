package Scanner;

public class Lexem
{
    public enum LexemType {
        CLASS(0), INTERFACE(1), PUBLIC(2), PRIVATE(3), PROTECTED(4), PACKAGE(5), IMPORT(6), EXTENDS(7), IMPLEMENTS(8), ABSTRACT(9), THIS(10),
        SUPER(11), NEW(12), RETURN(13), IDENTIFIER(14), LEFT_BRACKET(15), RIGHT_BRACKET(16), LEFT_CURLY_BRACKET(17), RIGHT_CURLY_BRACKET(18),
        LEFT_SQUARE_BRACKET(19), RIGHT_SQUARE_BRACKET(20), DOT(21), SEMICOLON(22), COMMA(23), ASSIGNMENT(24), NUMBER(25), VOID(26), INT(27),
        MAIN(28), STATIC(29), STRING(30), ILLEGAL_IDENT(31), ILLEGAL_CHAR(32), ASTERIX(33), EOF(34);


        private final int value;
        LexemType(int value) {
            this.value = value;
        }
        public int getValue() {
            return value;
        }
    }

    private LexemType lexemType;
    private int line;
    private int column;
    private String value;

    public Lexem(LexemType lexemType, int line, int column, String value) {
        this.lexemType = lexemType;
        this.line = line;
        this.column = column;
        this.value = value;
    }

    public LexemType getLexemType() {
        return lexemType;
    }

    public int getLine() {
        return line;
    }

    public int getColumn() {
        return column;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        String result = "Lexem{" +
                "lexemType=" + lexemType +
                ", line=" + line +
                ", column=" + column;

        if(value==null)
            return result + '}';

        return result + ", value='" + value + '\'' + '}';
    }
}