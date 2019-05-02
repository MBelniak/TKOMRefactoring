package Parser;

import Exceptions.ParsingException;
import Scanner.Lexem;
import Scanner.Scanner;

import javax.swing.*;

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
    private void parsePrimitiveFieldInitialization() throws ParsingException //this can also be a declaration, nomenclature mistake
    {
        if(currentToken.getLexemType() == SEMICOLON)
        {
            advance();
            return;
        }
        else if(currentToken.getLexemType() == ASSIGNMENT)
        {
            advance();
            parseAssignment();
        }
        else
            throw new ParsingException("Semicolon or '=' expected.", currentToken.getLine(), currentToken.getColumn());

    }
    private void parseObjectFieldInitialization() throws ParsingException {
        if(currentToken.getLexemType() == ASSIGNMENT)
        {
            advance();
            if(currentToken.getLexemType() == NEW)
            {
                advance();
                if(currentToken.getLexemType() == IDENTIFIER)
                {
                    advance();
                    parseMethodCall();
                }
                else
                    throw new ParsingException("Identifier expected.", currentToken.getLine(), currentToken.getColumn());
            }
            else if(currentToken.getLexemType() == THIS
                    || currentToken.getLexemType() == IDENTIFIER)
            {
                parseFetchedCallOrIdentifier();
            }
            else
                throw new ParsingException("Object initialization expected.", currentToken.getLine(), currentToken.getColumn());
        }
        else
            return;
    }
    private void parseMethodBlock() throws ParsingException {
        if(currentToken.getLexemType() == LEFT_CURLY_BRACKET)
        {
            advance();
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
    private void parseMethodDeclaration() throws ParsingException {
        if(currentToken.getLexemType() == LEFT_BRACKET)
        {
            advance();
            parseParameterList();
            if(currentToken.getLexemType() == RIGHT_BRACKET)
            {
                advance();
            }
            else
                throw new ParsingException("')' expected.", currentToken.getLine(), currentToken.getColumn());
        }
        else
            throw new ParsingException("'(' expected.", currentToken.getLine(), currentToken.getColumn());
    }
    private void parseMethodDefinition() throws ParsingException {
        if(currentToken.getLexemType() == INT
                || currentToken.getLexemType() == VOID
                || currentToken.getLexemType() == IDENTIFIER)
        {
            advance();
            if(currentToken.getLexemType() == IDENTIFIER)
            {
                advance();
                parseMethodDeclaration();
                parseMethodBlock();
            }
            else
                throw new ParsingException("Identifier expected.", currentToken.getLine(), currentToken.getColumn());
        }
        else
            throw new ParsingException("Type expected.", currentToken.getLine(), currentToken.getColumn());
    }
    private void parseAbstractMethodDeclaration() throws ParsingException {
        if(currentToken.getLexemType() == INT
                || currentToken.getLexemType() == VOID
                || currentToken.getLexemType() == IDENTIFIER)
        {
            advance();
            if(currentToken.getLexemType() == IDENTIFIER)
            {
                advance();
                parseMethodDeclaration();
            }
            else
                throw new ParsingException("Identifier expected.", currentToken.getLine(), currentToken.getColumn());
        }
        else
            throw new ParsingException("Type expected.", currentToken.getLine(), currentToken.getColumn());
    }
    private void parseSuperOptional() throws ParsingException {
        if(currentToken.getLexemType() == SUPER)
        {
            advance();
            if(currentToken.getLexemType() == LEFT_BRACKET)
            {
                advance();
                parseCallParameters();
                if(currentToken.getLexemType() == RIGHT_BRACKET)
                {
                    advance();
                    if(currentToken.getLexemType() == SEMICOLON)
                    {
                        advance();
                    }
                    else
                        throw new ParsingException("Semicolon expected.", currentToken.getLine(), currentToken.getColumn());
                }
                else
                    throw new ParsingException("')' expected.", currentToken.getLine(), currentToken.getColumn());
            }
            else
                throw new ParsingException("'(' expected.", currentToken.getLine(), currentToken.getColumn());
        }
        else
            return;
    }
    private void parseMethodBody() throws ParsingException {
        parseMethodStatementsOptional();
        if(currentToken.getLexemType() == RETURN)
        {
            parseReturnStatement();
        }
        else
            return;
    }
    private void parseMethodStatementsOptional() throws ParsingException
    {
        if(currentToken.getLexemType() == IDENTIFIER)
        {
            advance();
            if(currentToken.getLexemType() == IDENTIFIER)
            {
                advance();
                if(currentToken.getLexemType() == SEMICOLON)
                {
                    advance();
                }
                else if(currentToken.getLexemType() == ASSIGNMENT)
                {
                    advance();
                    parseAssignmentRight();
                    if(currentToken.getLexemType() == SEMICOLON)
                    {
                        advance();
                    }
                    else
                        throw new ParsingException("semicolon expected.", currentToken.getLine(), currentToken.getColumn());
                }
                else
                    throw new ParsingException("Semicolon or '=' expected.", currentToken.getLine(), currentToken.getColumn());
            }
            else if(currentToken.getLexemType() == LEFT_BRACKET)
            {
                parseMethodCall();
                if(currentToken.getLexemType() == SEMICOLON)
                {
                    advance();
                }
                else if(currentToken.getLexemType() == DOT)
                {
                    advance();
                    parseFetchedObject();
                    parseIdentifierMethodOrAssignment();
                }
                else
                    throw new ParsingException("Semicolon or '.' expected.", currentToken.getLine(), currentToken.getColumn());
            }
            else if(currentToken.getLexemType() == DOT)
            {
                advance();
                parseFetchedObjectOptional();
                parseIdentifierMethodOrAssignment();
            }
            else
                throw new ParsingException("Illegal statement.", currentToken.getLine(), currentToken.getColumn());

            parseMethodStatementsOptional();
        }
        else if(currentToken.getLexemType() == INT)
        {
            advance();
            if(currentToken.getLexemType() == IDENTIFIER)
            {
                advance();
                if(currentToken.getLexemType() == SEMICOLON)
                {
                    advance();
                }
                else
                {
                    parsePrimitiveFieldInitialization();
                }
            }
            else
                throw new ParsingException("Identifier expected.", currentToken.getLine(), currentToken.getColumn());
        }
        else
            return;
    }
    private void parseIdentifierMethodOrAssignment() throws ParsingException {
        if(currentToken.getLexemType() == IDENTIFIER)
        {
            advance();
            if(currentToken.getLexemType() == ASSIGNMENT)
            {
                advance();
                parseAssignmentRight();
            }
            else
            {
                parseMethodCall();
            }
            if(currentToken.getLexemType() == SEMICOLON)
            {
                advance();
            }
            else
                throw new ParsingException("Semicolon expected.", currentToken.getLine(), currentToken.getColumn());
        }
        else
            throw new ParsingException("Identifier expected.", currentToken.getLine(), currentToken.getColumn());
    }
    private void parseReturnStatement() throws ParsingException {
        if(currentToken.getLexemType() == RETURN)
        {
            advance();
            parseFetchedOrNumberOptional();
            if(currentToken.getLexemType() == SEMICOLON)
            {
                advance();
            }
            else
                throw new ParsingException("Semicolon expected.", currentToken.getLine(), currentToken.getColumn());
        }
        else
            throw new ParsingException("Return statment expected.", currentToken.getLine(), currentToken.getColumn());
    }
    private void parseFetchedOrNumberOptional() throws ParsingException {
        if(currentToken.getLexemType() == THIS
                || currentToken.getLexemType() == IDENTIFIER)
        {
            parseFetchedCallOrIdentifier();
        }
        else if(currentToken.getLexemType() == NUMBER)
        {
            advance();
        }
        else
            return;
    }
    private void parseMethodCall() throws ParsingException {
        if(currentToken.getLexemType() == LEFT_BRACKET)
        {
            advance();
            parseCallParameters();
            if(currentToken.getLexemType() == RIGHT_BRACKET)
            {
                advance();
            }
            else
                throw new ParsingException("')' expected.", currentToken.getLine(), currentToken.getColumn());
        }
        else
            throw new ParsingException("'(' expected.", currentToken.getLine(), currentToken.getColumn());
    }
    private void parseIdentifierOrMethodCallDotOptional()
    {
        //TODO maybe
    }
    private void parseCallParameters() throws ParsingException {
        if(currentToken.getLexemType() == NUMBER)
        {
            advance();
            parseNextParametersOptional();
        }
        else
        {
            parseFetchedCallOrIdentifier();
        }
    }

    private void parseFetchedCallOrIdentifier() throws ParsingException {
        parseFetchedObject();
        if(currentToken.getLexemType() == IDENTIFIER)
        {
            advance();
            if(currentToken.getLexemType() == LEFT_BRACKET)
            {
                parseMethodCall();
            }
        }
        else
            throw new ParsingException("Identifier expected.", currentToken.getLine(), currentToken.getColumn());
    }

    private void parseNextParametersOptional() throws ParsingException {
        if(currentToken.getLexemType() == COMMA)
        {
            advance();
            parseCallParameters();
        }
        else
            return;
    }
    private void parseFetchedObject()
    {
        parseThisOptional();
        parseFetched();
    }
    private void parseThisOptional()
    {
        if(currentToken.getLexemType() == THIS)
        {
            advance();
        }
        else
            return;
    }
    private void parseFetched() throws ParsingException {
        if(currentToken.getLexemType() == IDENTIFIER)
        {
            advance();
            if(currentToken.getLexemType() == LEFT_BRACKET)
            {
                parseMethodCall();
            }
            parseDotFetchedOptional();
        }
        else
            throw new ParsingException("Identifier expected.", currentToken.getLine(), currentToken.getColumn());
    }
    private void parseDotFetchedOptional() throws ParsingException {
        if(currentToken.getLexemType() == DOT)
        {
            advance();
            parseFetched();
        }
        else
            return;
    }
    private void parseAssignment() throws ParsingException {
        parseFetchedObjectOptional();
        if(currentToken.getLexemType() == IDENTIFIER)
        {
            advance();
            if(currentToken.getLexemType() == ASSIGNMENT)
            {
                advance();
                parseAssignmentRight();
            }
            else
                throw new ParsingException("'=' expected.", currentToken.getLine(), currentToken.getColumn());
        }
        else
            throw new ParsingException("Identifier expected.", currentToken.getLine(), currentToken.getColumn());
    }
    private void parseFetchedObjectOptional()
    {
        if(currentToken.getLexemType() == THIS
                || currentToken.getLexemType() == IDENTIFIER)
        {
            parseFetchedObject();
        }
        else
            return;
    }
    private void parseAssignmentRight() throws ParsingException {
        if(currentToken.getLexemType() == NUMBER)
        {
            advance();
        }
        else if(currentToken.getLexemType() == THIS
                || currentToken.getLexemType() == IDENTIFIER)
        {
            parseFetchedObjectOptional();
            if(currentToken.getLexemType() == IDENTIFIER)
            {
                advance();
                if(currentToken.getLexemType() == LEFT_BRACKET)
                    parseMethodCall();
            }
            else
                throw new ParsingException("Identifier expected.", currentToken.getLine(), currentToken.getColumn());
        }
        else if(currentToken.getLexemType() == NEW)
        {
            advance();
            if(currentToken.getLexemType() == IDENTIFIER)
            {
                advance();
                parseMethodCall();
            }
            else
                throw new ParsingException("Identifier expected.", currentToken.getLine(), currentToken.getColumn());
        }
        else
            throw new ParsingException("Incorrect assignment.", currentToken.getLine(), currentToken.getColumn());
    }

    private void parseParameterList() throws ParsingException {
        if(currentToken.getLexemType() == IDENTIFIER
                || currentToken.getLexemType() == INT)
        {
            advance();
            if (currentToken.getLexemType() == IDENTIFIER)
            {
                advance();
                parseNextIdentifierOptional();
            }
            else
                throw new ParsingException("Identifier expected.", currentToken.getLine(), currentToken.getColumn());
        }
        else
            throw new ParsingException("Type expected", currentToken.getLine(), currentToken.getColumn());
    }

    private void parseNextIdentifierOptional() throws ParsingException {
        if(currentToken.getLexemType() == COMMA)
        {
            advance();
            parseParameterList();
        }
        else
            return;
    }
    private void parsePublicOptional()
    {
        if(currentToken.getLexemType() == PUBLIC)
            advance();
        else
            return;
    }

    private void advance()
    {
        currentToken = scanner.getNextLexem();
    }
}
