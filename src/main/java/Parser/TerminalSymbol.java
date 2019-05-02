package Parser;

import Scanner.Lexem;

public class TerminalSymbol implements Symbol{
    private Lexem.LexemType lexem;

    public TerminalSymbol(Lexem.LexemType lexem) {
        this.lexem = lexem;
    }
}
