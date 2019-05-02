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
    private void parseImportDeclarationOptional()
    {
        //TODO
    }
    private void parsePackagePath()
    {
        //TODO
    }
    private void parseDotIdentifierOptional()
    {
        //TODO
    }
    private void parseDotAsterixOptional()
    {
        //TODO
    }
    private void parseClassOrInterfaceDefinition()
    {
        //TODO
    }
    private void parseClassOrInterfaceDefinitionOptional()
    {
        //TODO
    }
    private void parseClassDefinition()
    {
        //TODO
    }
    private void parseInterfaceDefinition()
    {
        //TODO
    }
    private void parseClassHeader()
    {
        //TODO
    }
    private void parseInterfaceHeader()
    {
        //TODO
    }
    private void parseExtendsInterfaceOptional()
    {
        //TODO
    }
    private void parseNextExtensionsOptional()
    {
        //TODO
    }
    private void parseAbstractOptional()
    {
        //TODO
    }
    private void parseExtends()
    {
        //TODO
    }
    private void parseImplements()
    {
        //TODO
    }
    private void parseNextIdentifier()
    {
        //TODO
    }
    private void parseClassBody()
    {
        //TODO
    }
    private void parseDefinition()
    {
        //TODO
    }
    private void parseMethodOrPrimitiveFieldInitialization()
    {
        //TODO
    }
    private void parseObjectFieldOrConstructorDefinitionOrMethod()
    {
        //TODO
    }
    private void parseMainMethod()
    {
        //TODO
    }
    private void parseInterfaceBody()
    {
        //todo
    }
    private void parseConstructorDefinition()
    {
        //TODO
    }
    private void parsePrimitiveFieldInitialization()
    {
        //TODO
    }
    private void parseObjectFieldInitialization()
    {
        //Todo
    }
    private void parseMethodBlock()
    {
        //TODO
    }
    private void parseMethodDeclaration()
    {
        //TODO
    }
    private void parseMethodDefinition()
    {
        //TODO
    }
    private void parseAbstractMethodDeclaration()
    {
        //TODO
    }
    private void parseSuperOptional()
    {
        //TODO
    }
    private void parseMethodBody()
    {
        //TODO
    }
    private void parseMethodStatementsOptional()
    {
        //TODO
    }
    private void parseIdentifierMethodOrAssignment()
    {
        //TODO
    }
    private void parseReturnStatement()
    {
        //TODO
    }
    private void parseFetchedOrNumberOptional()
    {
        //TODO
    }
    private void parseMethodCall()
    {
        //TODO
    }
    private void parseIdentifierOrMethodCallDotOptional()
    {
        //TODO
    }
    private void parseCallParameters()
    {
        //TODO
    }
    private void parseNextParametersOptional()
    {
        //TODO
    }
    private void parseFetchedObject()
    {
        //TODO
    }
    private void parseThisOptional()
    {
        //TODO
    }
    private void parseFetched()
    {
        //TODO
    }
    private void parseDotFetchedOptional()
    {
        //TODO
    }
    private void parseAssignment()
    {
        //TODO
    }
    private void parseFetchedObjectOptional()
    {
        //TODO
    }
    private void parseAssignmentRight()
    {
        //TODO
    }

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
        parseNextIdentifierOptional();
    }

    private void parseNextIdentifierOptional() {
        previousToken = currentToken;
        currentToken = scanner.getNextLexem();
        if(currentToken.getLexemType()!=Lexem.LexemType.COMMA)
            return;
        System.out.println("Got comma");
        parseParameterList();
    }
    private void parsePublicOptional()
    {
        //TODO
    }
    private void parseProtectedOptional()
    {
        //TODO
    }
}
