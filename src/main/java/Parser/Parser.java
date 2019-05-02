package Parser;

import Exceptions.ParsingException;
import Scanner.Lexem;
import Scanner.Scanner;

import static Scanner.Lexem.LexemType.*;

public class Parser {
    private Scanner scanner;
    private Lexem currentToken;

    public Parser(Scanner scanner) {

        this.scanner = scanner;
        //TODO
    }

    public void parseFile() throws ParsingException{
        parsePackageDeclarationOptional();
        parseImportDeclarationOptional();
        parseClassOrInterfaceDefinitionOptional();
        if (currentToken.getLexemType() == PUBLIC) {
            advance();
        }
        else
        {
            if(currentToken.getLexemType() == EOF)
                throw new ParsingException("No public class in file.", 0, 0);
            else
                throw new ParsingException("Unexpected identifier.", currentToken.getLine(), currentToken.getColumn());
        }
        parseClassOrInterfaceDefinition();
        parseClassOrInterfaceDefinitionOptional();
    }
    private void parsePackageDeclarationOptional() throws ParsingException {
        if(currentToken.getLexemType() == PACKAGE)
        {
            advance();
        }
        else
        {
            return;
        }
        parsePackagePath();
        if(currentToken.getLexemType() == SEMICOLON)
            advance();
        else
            throw new ParsingException("Semicolon expected", currentToken.getLine(), currentToken.getColumn());
    }
    private void parseImportDeclarationOptional() throws ParsingException {
        if(currentToken.getLexemType() == IMPORT)
            advance();
        else return;

        parsePackagePath();

        if(currentToken.getLexemType() == SEMICOLON)
            advance();
        else throw new ParsingException("Semicolon expected", currentToken.getLine(), currentToken.getColumn());

    }
    private void parsePackagePath() throws ParsingException {
        if(currentToken.getLexemType()==IDENTIFIER)
            advance();
        else
            throw new ParsingException("Identifier expected", currentToken.getLine(), currentToken.getColumn());
        if(currentToken.getLexemType() == DOT)
        {
            advance();
            if(currentToken.getLexemType() == IDENTIFIER)
                parseIdentifierOptional();
            else if(currentToken.getLexemType() == ASTERIX)
                advance();
            else
                throw new ParsingException("Identifier or '*' expected", currentToken.getLine(), currentToken.getColumn());
        }

        if(currentToken.getLexemType()==SEMICOLON)
            advance();
        else
            throw new ParsingException("Semicolon expected", currentToken.getLine(), currentToken.getColumn());
    }
    private void parseIdentifierOptional() throws ParsingException {
        if(currentToken.getLexemType() == IDENTIFIER)
            advance();
        else
            throw new ParsingException("Identifier expected.", currentToken.getLine(), currentToken.getColumn());

        if(currentToken.getLexemType() == DOT)
            advance();
        else
            return;

        parseIdentifierOptional();
    }
    private void parseClassOrInterfaceDefinition() throws ParsingException {
        if(currentToken.getLexemType() == ABSTRACT) {
            advance();
            parseClassDefinition();
        }
        else
        {
            if(currentToken.getLexemType() == CLASS)
                parseClassDefinition();
            else if(currentToken.getLexemType() == PUBLIC || currentToken.getLexemType() == INTERFACE)
                parseInterfaceDefinition();
            else
                throw new ParsingException("Class or interface definition expected", currentToken.getLine(), currentToken.getColumn());
        }

    }
    private void parseClassOrInterfaceDefinitionOptional() throws ParsingException {
        Scanner.Lexem.LexemType currentLexem = currentToken.getLexemType();
        if(currentLexem == ABSTRACT || currentLexem == CLASS || currentLexem == INTERFACE) {
            parseClassOrInterfaceDefinition();
        }
        else
            return;
        parseClassOrInterfaceDefinitionOptional();
    }
    private void parseClassDefinition() throws ParsingException {
        parseClassHeader();
        if(currentToken.getLexemType() == LEFT_CURLY_BRACKET)
            advance();
        else
            throw new ParsingException("'{' expected", currentToken.getLine(), currentToken.getColumn());
        parseClassBody();
        if(currentToken.getLexemType() == RIGHT_CURLY_BRACKET)
            advance();
        else
            throw new ParsingException("'}' expected", currentToken.getLine(), currentToken.getColumn());
    }
    private void parseInterfaceDefinition() throws ParsingException {
        parseInterfaceHeader();
        if(currentToken.getLexemType() == LEFT_CURLY_BRACKET)
            advance();
        else
            throw new ParsingException("'{' expected.", currentToken.getLine(), currentToken.getColumn());
        parseInterfaceBody();
        if(currentToken.getLexemType() == RIGHT_CURLY_BRACKET)
            advance();
        else
            throw new ParsingException("'}' expected.", currentToken.getLine(), currentToken.getColumn());
    }
    private void parseClassHeader() throws ParsingException {
        if(currentToken.getLexemType() == CLASS)
            advance();
        else
            throw new ParsingException("'class' keyword expected.", currentToken.getLine(), currentToken.getColumn());
        if(currentToken.getLexemType() == IDENTIFIER)
            advance();
        else
            throw new ParsingException("Identifier expected.", currentToken.getLine(), currentToken.getColumn());
        parseExtends();
        parseImplements();
    }
    private void parseInterfaceHeader() throws ParsingException {
        parsePublicOptional();
        if(currentToken.getLexemType() == INTERFACE)
            advance();
        else
            throw new ParsingException("'interface' keyword expected", currentToken.getLine(), currentToken.getColumn());
        if(currentToken.getLexemType() == IDENTIFIER)
            advance();
        else
            throw new ParsingException("Identifier expected.", currentToken.getLine(), currentToken.getColumn());

        parseExtendsInterfaceOptional();
    }
    private void parseExtendsInterfaceOptional() throws ParsingException {
        if(currentToken.getLexemType() == EXTENDS)
            advance();
        else
            return;

        if(currentToken.getLexemType() == IDENTIFIER)
            advance();
        else
            throw new ParsingException("Identifier expected.", currentToken.getLine(), currentToken.getColumn());

        parseNextExtensionsOptional();
    }

    private void parseNextExtensionsOptional() throws ParsingException {
        if(currentToken.getLexemType() == COMMA)
            advance();
        else
            return;

        if(currentToken.getLexemType() == IDENTIFIER)
            advance();
        else
            throw new ParsingException("Identifier expected.", currentToken.getLine(), currentToken.getColumn());

        parseNextExtensionsOptional();
    }

    private void parseExtends() throws ParsingException {
        if(currentToken.getLexemType() == EXTENDS)
            advance();
        else
            return;

        if(currentToken.getLexemType() == IDENTIFIER)
            advance();
        else
            throw new ParsingException("Identifier expected.", currentToken.getLine(), currentToken.getColumn());
    }
    private void parseImplements() throws ParsingException {
        if(currentToken.getLexemType() == IMPLEMENTS)
            advance();
        else
            return;

        if(currentToken.getLexemType() == IDENTIFIER)
            advance();
        else
            throw new ParsingException("Identifier expected.", currentToken.getLine(), currentToken.getColumn());
        parseNextIdentifier();
    }
    private void parseNextIdentifier() throws ParsingException {
        if(currentToken.getLexemType() == COMMA)
            advance();
        else
            return;

        if(currentToken.getLexemType() == IDENTIFIER)
            advance();
        else
            throw new ParsingException("Identifier expected.", currentToken.getLine(), currentToken.getColumn());
    }
    private void parseClassBody() throws ParsingException {
        if(currentToken.getLexemType() == PUBLIC)
        {
            advance();
            if(currentToken.getLexemType() == ABSTRACT)
            {
                advance();
                if(currentToken.getLexemType() == CLASS)
                {
                    parseClassDefinition();
                }
                else if(currentToken.getLexemType() == IDENTIFIER
                        || currentToken.getLexemType() == INT
                        || currentToken.getLexemType() == VOID)
                {
                    parseAbstractMethodDeclaration();
                    if(currentToken.getLexemType() == SEMICOLON)
                        advance();
                    else
                        throw new ParsingException("Semicolon expected.", currentToken.getLine(), currentToken.getColumn());
                }
                else
                    throw new ParsingException("Class or method declaration expected", currentToken.getLine(), currentToken.getColumn());
            }
            else if(currentToken.getLexemType() == INT
                    || currentToken.getLexemType() == IDENTIFIER
                    || currentToken.getLexemType() == CLASS)
            {
                parseDefinition();
            }
            else if(currentToken.getLexemType() == STATIC)
            {
                parseMainMethod();
            }
            else
                throw new ParsingException("Incorrect definition.", currentToken.getLine(), currentToken.getColumn());

            parseClassBody();
        }
        else if(currentToken.getLexemType() == PROTECTED
                ||currentToken.getLexemType() == ABSTRACT
                || currentToken.getLexemType() == INT
                || currentToken.getLexemType() == IDENTIFIER
                || currentToken.getLexemType() == CLASS)
        {
            if(currentToken.getLexemType() == ABSTRACT)
            {
                if(currentToken.getLexemType() == CLASS)
                    parseClassDefinition();
                else if(currentToken.getLexemType() == IDENTIFIER
                        || currentToken.getLexemType() == INT
                        || currentToken.getLexemType() == VOID)
                {
                    parseAbstractMethodDeclaration();
                    if(currentToken.getLexemType() == SEMICOLON)
                        advance();
                    else
                        throw new ParsingException("Semicolon expected.", currentToken.getLine(), currentToken.getColumn());
                }
            }
            else if(currentToken.getLexemType() == INT
                    || currentToken.getLexemType() == IDENTIFIER
                    || currentToken.getLexemType() == CLASS)
            {
                parseDefinition();
            }
            parseClassBody();
        }
        else if(currentToken.getLexemType() == PRIVATE)
        {
            advance();
            parseDefinition();
            parseClassBody();
        }
        else
            return;
    }
    private void parseDefinition() throws ParsingException {
        if(currentToken.getLexemType() == INT)
        {
            advance();
            if(currentToken.getLexemType() == IDENTIFIER)
                advance();
            else
                throw new ParsingException("Identifier expected.", currentToken.getLine(), currentToken.getColumn());
            parseMethodOrPrimitiveFieldInitialization();
        }
        else if(currentToken.getLexemType() == IDENTIFIER)
        {
            advance();
            parseObjectFieldOrConstructorDefinitionOrMethod();
            if(currentToken.getLexemType() == SEMICOLON)
                advance();
            else
                throw new ParsingException("Semicolon expected.", currentToken.getLine(), currentToken.getColumn());
        }
        else if(currentToken.getLexemType() == CLASS)
        {
            parseClassDefinition();
        }
        else
            throw new ParsingException("Incorrect definition.", currentToken.getLine(), currentToken.getColumn());

    }
    private void parseMethodOrPrimitiveFieldInitialization() throws ParsingException {
        if(currentToken.getLexemType() == LEFT_BRACKET)
        {
            parseMethodDeclaration();
            if(currentToken.getLexemType() == SEMICOLON)
                advance();
            else if(currentToken.getLexemType() == LEFT_CURLY_BRACKET)
            {
                parseMethodBlock();
            }
            else
                throw new ParsingException("Semicolon or block expected", currentToken.getLine(), currentToken.getColumn());
        }
        else if(currentToken.getLexemType() == ASSIGNMENT
                || currentToken.getLexemType() == SEMICOLON)
        {
            parsePrimitiveFieldInitialization();
        }
        else
            throw new ParsingException("Incorrect definition.", currentToken.getLine(), currentToken.getColumn());
    }
    private void parseObjectFieldOrConstructorDefinitionOrMethod() throws ParsingException {
        if(currentToken.getLexemType() == LEFT_BRACKET)
        {
            parseConstructorDefinition();
        }
        else if(currentToken.getLexemType() == IDENTIFIER)
        {
            advance();
            if(currentToken.getLexemType() == ASSIGNMENT)
            {
                parseObjectFieldInitialization();
            }
            else if(currentToken.getLexemType() == LEFT_BRACKET)
            {
                parseMethodDeclaration();
            }
            else
                throw new ParsingException("Initialization or method declaration expected.", currentToken.getLine(), currentToken.getColumn());
        }
        else
            throw new ParsingException("Incorrect definition.", currentToken.getLine(), currentToken.getColumn());
    }
    private void parseMainMethod() throws ParsingException {
        if(currentToken.getLexemType() == STATIC)
        {
            advance();
            if(currentToken.getLexemType() == VOID)
            {
                advance();
                if(currentToken.getLexemType() == MAIN)
                {
                    advance();
                    if(currentToken.getLexemType() == LEFT_BRACKET)
                    {
                        advance();
                        if(currentToken.getLexemType() == STRING)
                        {
                            advance();
                            if(currentToken.getLexemType() == LEFT_SQUARE_BRACKET)
                            {
                                advance();
                                if(currentToken.getLexemType() == RIGHT_SQUARE_BRACKET)
                                {
                                    advance();
                                    if(currentToken.getLexemType() == IDENTIFIER)
                                    {
                                        advance();
                                        if(currentToken.getLexemType() == RIGHT_BRACKET)
                                        {
                                            advance();
                                            if(currentToken.getLexemType() == LEFT_CURLY_BRACKET)
                                            {
                                                advance();
                                                parseMethodBody();
                                                if(currentToken.getLexemType() == RIGHT_CURLY_BRACKET)
                                                {
                                                    advance();
                                                }
                                                else
                                                    throw new ParsingException("'}' expected", currentToken.getLine(), currentToken.getColumn());
                                            }
                                            else
                                                throw new ParsingException("'{' expected", currentToken.getLine(), currentToken.getColumn());
                                        }
                                        else
                                            throw new ParsingException("')' expected", currentToken.getLine(), currentToken.getColumn());
                                    }
                                    else
                                        throw new ParsingException("Identifier expected", currentToken.getLine(), currentToken.getColumn());
                                }
                                else
                                    throw new ParsingException("']' expected", currentToken.getLine(), currentToken.getColumn());
                            }
                            else
                                throw new ParsingException("'[' expected", currentToken.getLine(), currentToken.getColumn());
                        }
                        else
                            throw new ParsingException("'String' keyword expected.", currentToken.getLine(), currentToken.getColumn());
                    }
                    else
                        throw new ParsingException("'(' expected", currentToken.getLine(), currentToken.getColumn());
                }
                else
                    throw new ParsingException("'main' keyword expected.", currentToken.getLine(), currentToken.getColumn());            }
            else
                throw new ParsingException("'void' keyword expected.", currentToken.getLine(), currentToken.getColumn());
        }
        else
            throw new ParsingException("Main method declaration expected", currentToken.getLine(), currentToken.getColumn());
    }
    private void parseInterfaceBody() throws ParsingException {
        if(currentToken.getLexemType() == INT)
        {
            advance();
            if(currentToken.getLexemType() == IDENTIFIER)
            {
                advance();
                if(currentToken.getLexemType() == ASSIGNMENT)
                {
                    parsePrimitiveFieldInitialization();
                }
                else if(currentToken.getLexemType() == LEFT_BRACKET)
                {
                    parseMethodDeclaration();
                }
                else
                    throw new ParsingException("'=' or '(' expected.", currentToken.getLine(), currentToken.getColumn());

                parseInterfaceBody();
            }
            else
                throw new ParsingException("Identifier expected.", currentToken.getLine(), currentToken.getColumn());
        }
        else if(currentToken.getLexemType() == IDENTIFIER)
        {
            advance();
            if(currentToken.getLexemType() == IDENTIFIER)
            {
                advance();
                if(currentToken.getLexemType() == ASSIGNMENT)
                {
                    parseObjectFieldInitialization();
                }
                else if(currentToken.getLexemType() == LEFT_BRACKET)
                {
                    parseMethodDeclaration();
                }
                else
                    throw new ParsingException("'=' or '(' expected.", currentToken.getLine(), currentToken.getColumn());

                if(currentToken.getLexemType() == SEMICOLON)
                {
                    advance();
                    parseInterfaceBody();
                }
                else
                    throw new ParsingException("Semicolon expected.", currentToken.getLine(), currentToken.getColumn());
            }
            else
                throw new ParsingException("Identifier expected.", currentToken.getLine(), currentToken.getColumn());
        }
        else if(currentToken.getLexemType() == VOID)
        {
            advance();
            if(currentToken.getLexemType() == IDENTIFIER)
            {
                advance();
                parseMethodDeclaration();
                if(currentToken.getLexemType() == SEMICOLON)
                {
                    advance();
                    parseInterfaceBody();
                }
                else
                    throw new ParsingException("Semicolon expected.", currentToken.getLine(), currentToken.getColumn());
            }
            else
                throw new ParsingException("Identifier expected.", currentToken.getLine(), currentToken.getColumn());

        }
        else if(currentToken.getLexemType() == DEFAULT
                || currentToken.getLexemType() == PRIVATE)
        {
            advance();
            parseMethodDefinition();
            parseInterfaceBody();
        }
        else
            return;
    }
    private void parseConstructorDefinition() throws ParsingException {
        parseMethodDeclaration();
        if(currentToken.getLexemType() == LEFT_CURLY_BRACKET)
        {
            advance();
            parseSuperOptional();
            parseMethodBody();
            if(currentToken.getLexemType() == RIGHT_CURLY_BRACKET)
            {
                advance();
            }
            else
                throw new ParsingException("'}' expected.", currentToken.getLine(), currentToken.getColumn());
        }
        else
            throw new ParsingException("'{' expected.", currentToken.getLine(), currentToken.getColumn());
    }
    private void parsePrimitiveFieldInitialization()
    {

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
        currentToken = scanner.getNextLexem();
        if (currentToken.getLexemType() != Lexem.LexemType.IDENTIFIER
                && currentToken.getLexemType() != Lexem.LexemType.INT) {
            System.out.println("Parse error");
            return;
        }
        System.out.println("Got identifier or int");
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

    private void advance()
    {
        currentToken = scanner.getNextLexem();
    }
}
