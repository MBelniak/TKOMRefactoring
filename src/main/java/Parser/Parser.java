package Parser;

import FilesManagement.FileSource;
import Scanner.Lexem;
import Scanner.Scanner;
import javafx.util.Pair;

import java.util.Map;
import java.util.Set;

public class Parser {
    private Scanner scanner;
    private Lexem currentToken;
    private Lexem previousToken;
    private FileSource fileSource;
    private Set<Production> productionSet;
    private Map<Pair<Lexem.LexemType, NonTerminalSymbol>, Integer> productionTable;

    public Parser(Scanner scanner) {

        this.scanner = scanner;
        //TODO
    }

    public void parseFile()
    {
        parseParameterList();

    }
    private void parsePackageDeclarationOptional()
    {
        //TODO
    }
    private void
    private void parseParameterList() {
        previousToken = currentToken;
        currentToken = scanner.getNextLexem();
        if (currentToken.getLexemType() != Lexem.LexemType.IDENTIFIER
                && currentToken.getLexemType() != Lexem.LexemType.INT) {
            System.out.println("Parse error");
            return;
        }
        System.out.println("Got identifier or int");
        previousToken = currentToken;
        currentToken = scanner.getNextLexem();
        if (currentToken.getLexemType() != Lexem.LexemType.IDENTIFIER)
        {
            System.out.println("Parse error");
            return;
        }
        System.out.println("Got identifier");
        parseNextIdentOpt();
    }

    private void parseNextIdentOpt() {
        previousToken = currentToken;
        currentToken = scanner.getNextLexem();
        if(currentToken.getLexemType()!=Lexem.LexemType.COMMA)
            return;
        System.out.println("Got comma");
        parseParameterList();
    }
}
