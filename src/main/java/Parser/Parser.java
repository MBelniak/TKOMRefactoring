package Parser;

import Exceptions.*;
import FilesManagement.FileSource;
import Scanner.Lexem;
import Scanner.Scanner;

import java.net.IDN;
import java.util.Stack;

import static Scanner.Lexem.LexemType.*;

public class Parser {
    private Scanner scanner;
    private Lexem currentToken;
    private FileSource fileSource;

    public Parser(Scanner scanner) {

        this.scanner = scanner;
        //TODO
    }

    public void parseFile() throws NoPublicClassInFileException, UnexpectedIdentifierException {
        parsePackageDeclarationOptional();
        parseImportDeclarationOptional();
        parseClassOrInterfaceDefinitionOptional();
        if (currentToken.getLexemType() == PUBLIC) {
            advance();
        }
        else
        {
            if(currentToken.getLexemType() == EOF)
                throw new NoPublicClassInFileException();
            else
                throw new UnexpectedIdentifierException();
        }
        parseClassOrInterfaceDefinition();
        parseClassOrInterfaceDefinitionOptional();
    }
    private void parsePackageDeclarationOptional() throws SemicolonExpectedException {
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
            throw new SemicolonExpectedException();
    }
    private void parseImportDeclarationOptional() throws SemicolonExpectedException {
        if(currentToken.getLexemType() == IMPORT)
            advance();
        else return;

        parsePackagePath();

        if(currentToken.getLexemType() == SEMICOLON)
            advance();
        else throw new SemicolonExpectedException();

    }
    private void parsePackagePath() throws IdentifierExpectedException, SemicolonExpectedException, IdentOrAsterixExpectedException {
        if(currentToken.getLexemType()==IDENTIFIER)
            advance();
        else
            throw new IdentifierExpectedException();
        if(currentToken.getLexemType() == DOT)
        {
            advance();
            if(currentToken.getLexemType() == IDENTIFIER)
                parseIdentifierOptional();
            else if(currentToken.getLexemType() == ASTERIX)
                advance();
            else
                throw new IdentOrAsterixExpectedException();
        }

        if(currentToken.getLexemType()==SEMICOLON)
            advance();
        else
            throw new SemicolonExpectedException();
    }
    private void parseIdentifierOptional() throws IdentifierExpectedException {
        if(currentToken.getLexemType() == IDENTIFIER)
            advance();
        else
            throw new IdentifierExpectedException();

        if(currentToken.getLexemType() == DOT)
            advance();
        else
            return;

        parseIdentifierOptional();
    }
    private void parseClassOrInterfaceDefinition() throws ClassOrInterfaceDefinitionExpectedException {
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
                throw new ClassOrInterfaceDefinitionExpectedException();
        }

    }
    private void parseClassOrInterfaceDefinitionOptional() throws ClassOrInterfaceDefinitionExpectedException {
        Scanner.Lexem.LexemType currentLexem = currentToken.getLexemType();
        if(currentLexem == ABSTRACT || currentLexem == CLASS || currentLexem == INTERFACE) {
            parseClassOrInterfaceDefinition();
        }
        else
            return;
        parseClassOrInterfaceDefinitionOptional();
    }
    private void parseClassDefinition() throws LeftCurlyBracketExpectedException, RightCurlyBracketExpectedException {
        parseClassHeader();
        if(currentToken.getLexemType() == LEFT_CURLY_BRACKET)
            advance();
        else
            throw new LeftCurlyBracketExpectedException();
        parseClassBody();
        if(currentToken.getLexemType() == RIGHT_CURLY_BRACKET)
            advance();
        else
            throw new RightCurlyBracketExpectedException();
    }
    private void parseInterfaceDefinition() throws LeftCurlyBracketExpectedException, RightCurlyBracketExpectedException {
        parseInterfaceHeader();
        if(currentToken.getLexemType() == LEFT_CURLY_BRACKET)
            advance();
        else
            throw new LeftCurlyBracketExpectedException();
        parseInterfaceBody();
        if(currentToken.getLexemType() == RIGHT_CURLY_BRACKET)
            advance();
        else
            throw new RightCurlyBracketExpectedException();
    }
    private void parseClassHeader() throws ClassKeywordExpectedException, IdentifierExpectedException {
        if(currentToken.getLexemType() == CLASS)
            advance();
        else
            throw new ClassKeywordExpectedException();
        if(currentToken.getLexemType() == IDENTIFIER)
            advance();
        else
            throw new IdentifierExpectedException();
        parseExtends();
        parseImplements();
    }
    private void parseInterfaceHeader() throws InterfaceKeywordExpectedException, IdentifierExpectedException, ExtendsKeywordExpectedException {
        parsePublicOptional();
        if(currentToken.getLexemType() == INTERFACE)
            advance();
        else
            throw new InterfaceKeywordExpectedException();
        if(currentToken.getLexemType() == IDENTIFIER)
            advance();
        else
            throw new IdentifierExpectedException();

        parseExtendsInterfaceOptional();
    }
    private void parseExtendsInterfaceOptional() throws ExtendsKeywordExpectedException, IdentifierExpectedException {
        if(currentToken.getLexemType() == EXTENDS)
            advance();
        else
            return;

        if(currentToken.getLexemType() == IDENTIFIER)
            advance();
        else
            throw new IdentifierExpectedException();

        parseNextExtensionsOptional();
    }

    private void parseNextExtensionsOptional() throws IdentifierExpectedException {
        if(currentToken.getLexemType() == COMMA)
            advance();
        else
            return;

        if(currentToken.getLexemType() == IDENTIFIER)
            advance();
        else
            throw new IdentifierExpectedException();

        parseNextExtensionsOptional();
    }

    private void parseExtends() throws IdentifierExpectedException {
        if(currentToken.getLexemType() == EXTENDS)
            advance();
        else
            return;

        if(currentToken.getLexemType() == IDENTIFIER)
            advance();
        else
            throw new IdentifierExpectedException();
    }
    private void parseImplements() throws IdentifierExpectedException {
        if(currentToken.getLexemType() == IMPLEMENTS)
            advance();
        else
            return;

        if(currentToken.getLexemType() == IDENTIFIER)
            advance();
        else
            throw new IdentifierExpectedException();
        parseNextIdentifier();
    }
    private void parseNextIdentifier() throws IdentifierExpectedException {
        if(currentToken.getLexemType() == COMMA)
            advance();
        else
            return;

        if(currentToken.getLexemType() == IDENTIFIER)
            advance();
        else
            throw new IdentifierExpectedException();
    }
    private void parseClassBody() throws IncorrectDefinitionException, ClassOrMethodDeclarationExpectedException {
        if(currentToken.getLexemType() == PUBLIC)
        {
            advance();
            if(currentToken.getLexemType() == ABSTRACT)
            {
                advance();
                if(currentToken.getLexemType() == CLASS)
                {

                }
                else if(currentToken.getLexemType() == IDENTIFIER
                        || currentToken.getLexemType() == INT
                        || currentToken.getLexemType() == VOID)
                {

                }
                else
                    throw new ClassOrMethodDeclarationExpectedException();
            }
            else if(currentToken.getLexemType() == INT
                    || currentToken.getLexemType() == IDENTIFIER
                    || currentToken.getLexemType() == CLASS)
            {

            }
            else if(currentToken.getLexemType() == STATIC)
            {

            }
            else
                throw new IncorrectDefinitionException();
        }
        else if(currentToken.getLexemType() == PROTECTED)
        {

        }
        else if(currentToken.getLexemType() == PRIVATE)
        {

        }
        else if(currentToken.getLexemType() == ABSTRACT
                || currentToken.getLexemType() == INT
                || currentToken.getLexemType() == IDENTIFIER
                || currentToken.getLexemType() == CLASS)
        {

        }
        else
            return;
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
