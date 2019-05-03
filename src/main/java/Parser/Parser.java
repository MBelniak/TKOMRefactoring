package Parser;

import Exceptions.ParsingException;
import Scanner.Lexem;
import Scanner.Scanner;

import static Scanner.Lexem.LexemType.*;
import static Parser.AbstractSyntaxTree.ElementType.*;

public class Parser {
    private Scanner scanner;
    private Lexem currentToken;
    private AbstractSyntaxTree AST;

    public Parser(Scanner scanner) {

        this.scanner = scanner;
        AST = new AbstractSyntaxTree();
    }

    public void parseFile() throws ParsingException{
        AST.clear();
        advance();
        AST.startFile(currentToken);
        parsePackageDeclarationOptional();
        parseImportDeclarationOptional();
        parseClassOrInterfaceDefinitionOptional();
        if (currentToken.getLexemType() == PUBLIC) {
            AST.startElement(PublicClassDefinition, currentToken);
            advance();
        }
        else
        {
            if(currentToken.getLexemType() == EOF)
                return;
            else
                throw new ParsingException("Unexpected identifier.", currentToken.getLine(), currentToken.getColumn());
        }
        parseClassOrInterfaceDefinition();
        AST.endElement();
        parseClassOrInterfaceDefinitionOptional();
        if(currentToken.getLexemType() == PUBLIC)
            throw new ParsingException("There can be only one public class or interface in the file.", currentToken.getLine(), currentToken.getColumn());
        else if(currentToken.getLexemType() == EOF)
        {
            System.out.println(AST.toString());
            return;
        }
        else
            throw new ParsingException("Unexpected identifier.", currentToken.getLine(), currentToken.getColumn());
    }
    private void parsePackageDeclarationOptional() throws ParsingException {
        if(currentToken.getLexemType() == PACKAGE)
        {
            AST.startElement(PackageDeclaration, currentToken);
            advance();
        }
        else
            return;

        parseIdentifierOptional();
        if(currentToken.getLexemType() == SEMICOLON)
        {
            AST.endElement();
            advance();
        }
        else
            throw new ParsingException("Semicolon expected", currentToken.getLine(), currentToken.getColumn());
    }
    private void parseImportDeclarationOptional() throws ParsingException {
        if(currentToken.getLexemType() == IMPORT)
        {
            AST.startElement(ImportDeclaration, currentToken);
            advance();
        }
        else return;

        parsePackagePath();

        if(currentToken.getLexemType() == SEMICOLON)
        {
            AST.endElement();
            advance();
        }
        else throw new ParsingException("Semicolon expected", currentToken.getLine(), currentToken.getColumn());

    }
    private void parsePackagePath() throws ParsingException {
        if(currentToken.getLexemType()==IDENTIFIER)
        {
            AST.addIdentifierToList(currentToken);
            advance();
        }
        else
            throw new ParsingException("Identifier expected", currentToken.getLine(), currentToken.getColumn());
        if(currentToken.getLexemType() == DOT)
        {
            advance();
            if(currentToken.getLexemType() == IDENTIFIER)
                parsePackagePath();
            else if(currentToken.getLexemType() == ASTERIX)
                advance();
            else
                throw new ParsingException("Identifier or '*' expected", currentToken.getLine(), currentToken.getColumn());
        }
    }
    private void parseIdentifierOptional() throws ParsingException {
        if(currentToken.getLexemType() == IDENTIFIER) {
            AST.addIdentifierToList(currentToken);
            advance();
        }
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
            AST.startElement(AbstractClassDefinition, currentToken);
            advance();
            parseClassDefinition();
            AST.endElement();
        }
        else
        {
            if(currentToken.getLexemType() == CLASS) {
                AST.startElement(ClassDefinition, currentToken);
                parseClassDefinition();
                AST.endElement();
            }
            else if(currentToken.getLexemType() == INTERFACE) {
                AST.startElement(InterfaceDefinition, currentToken);
                parseInterfaceDefinition();
                AST.endElement();
            }
            else
                throw new ParsingException("Class or interface definition expected", currentToken.getLine(), currentToken.getColumn());
        }

    }
    private void parseClassOrInterfaceDefinitionOptional() throws ParsingException {
        if(currentToken.getLexemType() == ABSTRACT
                || currentToken.getLexemType() == CLASS
                || currentToken.getLexemType()== INTERFACE) {
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
        AST.startElement(ClassBody, currentToken);
        parseClassBody();
        AST.endElement();
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
        AST.startElement(InterfaceBody, currentToken);
        parseInterfaceBody();
        AST.endElement();
        if(currentToken.getLexemType() == RIGHT_CURLY_BRACKET)
            advance();
        else
            throw new ParsingException("'}' expected.", currentToken.getLine(), currentToken.getColumn());
    }
    private void parseClassHeader() throws ParsingException {
        if(currentToken.getLexemType() == CLASS) {
            AST.startElement(ClassHeader, currentToken);
            AST.flushBuffer();
            advance();
        }
        else
            throw new ParsingException("'class' keyword expected.", currentToken.getLine(), currentToken.getColumn());
        if(currentToken.getLexemType() == IDENTIFIER) {
            AST.addIdentifierToList(currentToken);
            advance();
        }
        else
            throw new ParsingException("Identifier expected.", currentToken.getLine(), currentToken.getColumn());
        parseExtends();
        parseImplements();
        AST.endElement();
    }
    private void parseInterfaceHeader() throws ParsingException {
        if (currentToken.getLexemType() == INTERFACE) {
            AST.startElement(InterfaceHeader, currentToken);
            advance();
        } else
            throw new ParsingException("'interface' keyword expected", currentToken.getLine(), currentToken.getColumn());
        if (currentToken.getLexemType() == IDENTIFIER)
        {
            AST.addIdentifierToList(currentToken);
            advance();
        }
        else
            throw new ParsingException("Identifier expected.", currentToken.getLine(), currentToken.getColumn());

        parseExtendsInterfaceOptional();
        AST.endElement();
    }
    private void parseExtendsInterfaceOptional() throws ParsingException {
        if(currentToken.getLexemType() == EXTENDS) {
            AST.startElement(ExtendList, currentToken);
            advance();
        }
        else
            return;

        if(currentToken.getLexemType() == IDENTIFIER) {
            AST.addIdentifierToList(currentToken);
            advance();
        }
        else
            throw new ParsingException("Identifier expected.", currentToken.getLine(), currentToken.getColumn());

        parseNextExtensionsOptional();
        AST.endElement();
    }

    private void parseNextExtensionsOptional() throws ParsingException {
        if(currentToken.getLexemType() == COMMA)
            advance();
        else
            return;

        if(currentToken.getLexemType() == IDENTIFIER) {
            AST.addIdentifierToList(currentToken);
            advance();
        }
        else
            throw new ParsingException("Identifier expected.", currentToken.getLine(), currentToken.getColumn());

        parseNextExtensionsOptional();
    }

    private void parseExtends() throws ParsingException {
        if(currentToken.getLexemType() == EXTENDS) {
            AST.startElement(ExtendList, currentToken);
            advance();
        }
        else
            return;

        if(currentToken.getLexemType() == IDENTIFIER)
        {
            AST.addIdentifierToList(currentToken);
            advance();
        }
        else
            throw new ParsingException("Identifier expected.", currentToken.getLine(), currentToken.getColumn());
        AST.endElement();
    }
    private void parseImplements() throws ParsingException {
        if(currentToken.getLexemType() == IMPLEMENTS)
        {
            AST.startElement(ImplementsList, currentToken);
            advance();
        }
        else
            return;

        if(currentToken.getLexemType() == IDENTIFIER)
        {
            AST.addIdentifierToList(currentToken);
            advance();
        }
        else
            throw new ParsingException("Identifier expected.", currentToken.getLine(), currentToken.getColumn());
        parseNextIdentifier();
        AST.endElement();
    }
    private void parseNextIdentifier() throws ParsingException {
        if(currentToken.getLexemType() == COMMA)
            advance();
        else
            return;

        if(currentToken.getLexemType() == IDENTIFIER)
        {
            AST.addIdentifierToList(currentToken);
            advance();
        }
        else
            throw new ParsingException("Identifier expected.", currentToken.getLine(), currentToken.getColumn());
    }
    private void parseClassBody() throws ParsingException {
        if(currentToken.getLexemType() == PUBLIC)
        {
            AST.startElement(Statement, currentToken);
            AST.storeInBuffer(currentToken);
            advance();
            if(currentToken.getLexemType() == ABSTRACT)
            {
                advance();
                if(currentToken.getLexemType() == CLASS)
                {
                    AST.startElement(AbstractClassDefinition, currentToken);
                    AST.flushBuffer();
                    parseClassDefinition();
                    AST.endElement();
                }
                else if(currentToken.getLexemType() == IDENTIFIER
                        || currentToken.getLexemType() == INT
                        || currentToken.getLexemType() == VOID)
                {
                    AST.startElement(AbstractMethodDeclaration, currentToken);
                    AST.flushBuffer();
                    parseAbstractMethodDeclaration();
                    AST.endElement();
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
                    || currentToken.getLexemType() == VOID
                    || currentToken.getLexemType() == CLASS)
            {
                parseDefinition();
            }
            else if(currentToken.getLexemType() == STATIC)
            {
                AST.startElement(MainMethod, currentToken);
                parseMainMethod();
                AST.endElement();
            }
            else
                throw new ParsingException("Incorrect definition.", currentToken.getLine(), currentToken.getColumn());
            AST.endElement();
            parseClassBody();
        }
        else if(currentToken.getLexemType() == PROTECTED
                ||currentToken.getLexemType() == ABSTRACT
                || currentToken.getLexemType() == INT
                || currentToken.getLexemType() == IDENTIFIER
                || currentToken.getLexemType() == CLASS)
        {
            AST.startElement(Statement, currentToken);
            if(currentToken.getLexemType()==PROTECTED)
            {
                AST.storeInBuffer(currentToken);
                advance();
            }
            if(currentToken.getLexemType() == ABSTRACT)
            {
                advance();
                if(currentToken.getLexemType() == CLASS)
                {
                    AST.startElement(AbstractClassDefinition, currentToken);
                    AST.flushBuffer();
                    parseClassDefinition();
                    AST.endElement();
                }

                else if(currentToken.getLexemType() == IDENTIFIER
                        || currentToken.getLexemType() == INT
                        || currentToken.getLexemType() == VOID)
                {
                    AST.startElement(AbstractMethodDeclaration, currentToken);
                    AST.flushBuffer();
                    parseAbstractMethodDeclaration();
                    if(currentToken.getLexemType() == SEMICOLON)
                    {
                        AST.endElement();
                        advance();
                    }
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
            AST.endElement();
            parseClassBody();
        }
        else if(currentToken.getLexemType() == PRIVATE)
        {
            AST.startElement(Statement, currentToken);
            AST.storeInBuffer(currentToken);
            advance();
            parseDefinition();
            AST.endElement();
            parseClassBody();
        }
        else
            return;
    }
    private void parseDefinition() throws ParsingException {
        if(currentToken.getLexemType() == INT)
        {
            AST.storeInBuffer(currentToken);
            advance();
            if(currentToken.getLexemType() == IDENTIFIER) {
                AST.storeInBuffer(currentToken);
                advance();
            }
            else
                throw new ParsingException("Identifier expected.", currentToken.getLine(), currentToken.getColumn());
            parseMethodOrPrimitiveFieldInitialization();
        }
        else if(currentToken.getLexemType() == IDENTIFIER)
        {
            AST.storeInBuffer(currentToken);
            advance();
            parseObjectFieldOrConstructorDefinitionOrMethod();
        }
        else if(currentToken.getLexemType() == CLASS)
        {
            AST.startElement(ClassDefinition, currentToken);
            parseClassDefinition();
            AST.endElement();
        }
        else if(currentToken.getLexemType() == VOID)
        {
            AST.storeInBuffer(currentToken);
            advance();
            if(currentToken.getLexemType() == IDENTIFIER)
            {
                AST.startElement(MethodDefinition, currentToken);
                AST.storeInBuffer(currentToken);
                advance();
                parseMethodDeclaration();
                parseMethodBlock();
                AST.endElement();
            }
            else
                throw new ParsingException("Identifier expected.", currentToken.getLine(), currentToken.getColumn());
        }
        else
            throw new ParsingException("Incorrect definition.", currentToken.getLine(), currentToken.getColumn());

    }
    private void parseMethodOrPrimitiveFieldInitialization() throws ParsingException {
        if(currentToken.getLexemType() == LEFT_BRACKET)
        {
            AST.startElement(MethodDefinition, currentToken);
            AST.flushBuffer();
            parseMethodDeclaration();
            parseMethodBlock();
            AST.endElement();
        }
        else if(currentToken.getLexemType() == ASSIGNMENT
                || currentToken.getLexemType() == SEMICOLON)
        {
            AST.startElement(PrimitiveFieldDeclaration, currentToken);
            AST.flushBuffer();
            parsePrimitiveFieldInitialization();
            AST.endElement();
        }
        else
            throw new ParsingException("Incorrect definition.", currentToken.getLine(), currentToken.getColumn());
    }
    private void parseObjectFieldOrConstructorDefinitionOrMethod() throws ParsingException {
        if(currentToken.getLexemType() == LEFT_BRACKET)
        {
            AST.startElement(ConstructorDefinition, currentToken);
            AST.flushBuffer();
            parseConstructorDefinition();
            AST.endElement();
        }
        else if(currentToken.getLexemType() == IDENTIFIER)
        {
            AST.storeInBuffer(currentToken);
            advance();
            if(currentToken.getLexemType() == LEFT_BRACKET)
            {
                AST.startElement(MethodDefinition, currentToken);
                AST.flushBuffer();
                parseMethodDeclaration();
                parseMethodBlock();
                AST.endElement();
            }
            else if(currentToken.getLexemType() == SEMICOLON)
            {
                AST.startElement(ObjectFieldDeclaration, currentToken);
                AST.flushBuffer();
                advance();
                AST.endElement();
            }
            else
                throw new ParsingException("Incorrect statement. Semicolon or method definition expected.", currentToken.getLine(), currentToken.getColumn());
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
        if(currentToken.getLexemType() == INT)                //Interface_body -> „int” ident (Primit_field_init|Method_dec „;”) Interface_body  | e
        {
            AST.startElement(Statement, currentToken);
            AST.storeInBuffer(currentToken);
            advance();
            if(currentToken.getLexemType() == IDENTIFIER)
            {
                AST.storeInBuffer(currentToken);
                advance();
                if(currentToken.getLexemType() == ASSIGNMENT)
                {
                    AST.startElement(PrimitiveFieldDeclaration, currentToken);
                    AST.flushBuffer();
                    parsePrimitiveFieldInitialization();
                    AST.endElement();
                }
                else if(currentToken.getLexemType() == LEFT_BRACKET)
                {
                    AST.startElement(MethodDeclaration, currentToken);
                    AST.flushBuffer();
                    parseMethodDeclaration();
                    AST.endElement();
                }
                else
                    throw new ParsingException("'=' or '(' expected.", currentToken.getLine(), currentToken.getColumn());

                if(currentToken.getLexemType() == SEMICOLON)
                {
                    AST.endElement();
                    advance();
                    parseInterfaceBody();
                }
            }
            else
                throw new ParsingException("Identifier expected.", currentToken.getLine(), currentToken.getColumn());
        }
        else if(currentToken.getLexemType() == IDENTIFIER) //Interface_body ->ident ident (Object_field_init|Method_dec) „;” Interface_body  | e
        {
            AST.startElement(Statement, currentToken);
            AST.storeInBuffer(currentToken);
            advance();
            if(currentToken.getLexemType() == IDENTIFIER)
            {
                AST.storeInBuffer(currentToken);
                advance();
                if(currentToken.getLexemType() == ASSIGNMENT)
                {
                    parseObjectFieldInitialization();
                }
                else if(currentToken.getLexemType() == LEFT_BRACKET)
                {
                    AST.startElement(MethodDeclaration, currentToken);
                    parseMethodDeclaration();
                    AST.endElement();
                }
                else
                    throw new ParsingException("'=' or '(' expected.", currentToken.getLine(), currentToken.getColumn());

                if(currentToken.getLexemType() == SEMICOLON)
                {
                    AST.endElement();
                    advance();
                    parseInterfaceBody();
                }
                else
                    throw new ParsingException("Semicolon expected.", currentToken.getLine(), currentToken.getColumn());
            }
            else
                throw new ParsingException("Identifier expected.", currentToken.getLine(), currentToken.getColumn());
        }
        else if(currentToken.getLexemType() == VOID) //Interface_body -> „void” ident Method_dec „;” Interface_body | e
        {
            AST.startElement(MethodDeclaration, currentToken);
            AST.addIdentifierToList(currentToken);
            advance();
            if(currentToken.getLexemType() == IDENTIFIER)
            {
                AST.addIdentifierToList(currentToken);
                advance();
                parseMethodDeclaration();
                if(currentToken.getLexemType() == SEMICOLON)
                {
                    advance();
                    AST.endElement();
                    parseInterfaceBody();
                }
                else
                    throw new ParsingException("Semicolon expected.", currentToken.getLine(), currentToken.getColumn());
            }
            else
                throw new ParsingException("Identifier expected.", currentToken.getLine(), currentToken.getColumn());

        }
        else if(currentToken.getLexemType() == DEFAULT //Interface_body -> („default”|”private”) Method_def Interface_body | e
                || currentToken.getLexemType() == PRIVATE)
        {
            AST.startElement(MethodDefinition, currentToken);
            AST.addIdentifierToList(currentToken);
            advance();
            parseMethodDefinition();
            AST.endElement();
            parseInterfaceBody();
        }
        else
            return;
    }
    private void parseConstructorDefinition() throws ParsingException {
        parseMethodDeclaration();
        if(currentToken.getLexemType() == LEFT_CURLY_BRACKET)
        {
            AST.startElement(MethodBody, currentToken);
            advance();
            parseSuperOptional();
            parseMethodBody();
            if(currentToken.getLexemType() == RIGHT_CURLY_BRACKET)
            {
                AST.endElement();
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
            AST.flushBuffer();
            advance();
            return;
        }
        else if(currentToken.getLexemType() == ASSIGNMENT)
        {
            AST.startElement(PrimitiveFieldInitialization, currentToken);
            AST.flushBuffer();
            AST.addIdentifierToList(currentToken);
            advance();
            if(currentToken.getLexemType() == NUMBER)
            {
                AST.addNumberToList(currentToken);
                advance();
            }
            else
                parseFetchedObjectOptional();

            if(currentToken.getLexemType() == SEMICOLON)
            {
                AST.endElement();
                advance();
            }
            else
                throw new ParsingException("Semicolon expected.", currentToken.getLine(), currentToken.getColumn());
        }
        else
            throw new ParsingException("Semicolon or '=' expected.", currentToken.getLine(), currentToken.getColumn());

    }
    private void parseObjectFieldInitialization() throws ParsingException {
        if(currentToken.getLexemType() == ASSIGNMENT)
        {
            AST.startElement(ObjectFieldInitialization, currentToken);
            AST.flushBuffer();
            AST.addIdentifierToList(currentToken);
            advance();
            if(currentToken.getLexemType() == NEW)
            {
                AST.startElement(NewCall, currentToken);
                advance();
                if(currentToken.getLexemType() == IDENTIFIER)
                {
                    AST.storeInBuffer(currentToken);
                    AST.startElement(MethodCall, currentToken);
                    AST.flushBuffer();
                    advance();
                    parseMethodCall();
                    AST.endElement();
                }
                else
                    throw new ParsingException("Identifier expected.", currentToken.getLine(), currentToken.getColumn());
            }
            else if(currentToken.getLexemType() == THIS
                    || currentToken.getLexemType() == IDENTIFIER)
            {
                AST.startElement(FetchedObject, currentToken);
                parseFetchedCallOrIdentifier();
                AST.endElement();
            }
            else
                throw new ParsingException("Object initialization expected.", currentToken.getLine(), currentToken.getColumn());
            AST.endElement();
        }
        else
            return;
    }
    private void parseMethodBlock() throws ParsingException {
        if(currentToken.getLexemType() == LEFT_CURLY_BRACKET)
        {
            AST.startElement(MethodBody, currentToken);
            advance();
            parseMethodBody();
            if(currentToken.getLexemType() == RIGHT_CURLY_BRACKET)
            {
                AST.endElement();
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
            AST.flushBuffer();
            AST.startElement(MethodParameterList, currentToken);
            advance();
            parseParameterList();
            if(currentToken.getLexemType() == RIGHT_BRACKET)
            {
                AST.endElement();
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
            AST.addIdentifierToList(currentToken);
            advance();
            if(currentToken.getLexemType() == IDENTIFIER)
            {
                AST.addIdentifierToList(currentToken);
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
            AST.addIdentifierToList(currentToken);
            advance();
            if(currentToken.getLexemType() == IDENTIFIER)
            {
                AST.addIdentifierToList(currentToken);
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
            AST.startElement(SuperCall, currentToken);
            advance();
            if(currentToken.getLexemType() == LEFT_BRACKET)
            {
                advance();
                AST.startElement(CallParameters, currentToken);
                parseCallParameters();
                if(currentToken.getLexemType() == RIGHT_BRACKET)
                {
                    AST.endElement();
                    advance();
                    if(currentToken.getLexemType() == SEMICOLON)
                    {
                        AST.endElement();
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
            AST.startElement(ReturnStatement, currentToken);
            parseReturnStatement();
            AST.endElement();
        }
        else
            return;
    }
    private void parseMethodStatementsOptional() throws ParsingException
    {
        /* Method_stmnts_opt -> ident (ident („;” | „=” Assign_right „;”)
                                        | Method_call („;” |  Fetched_opt („=” Assign_right|e „;”)))
                                        | Fetched_opt („=” Assign_right | e) „;”) Method_stmnts_opt | e

         */
        if(currentToken.getLexemType() == IDENTIFIER)
        {
            AST.startElement(MethodStatement, currentToken);
            AST.storeInBuffer(currentToken);
            advance();
            if(currentToken.getLexemType() == IDENTIFIER)
            {
                AST.storeInBuffer(currentToken);
                advance();
                if(currentToken.getLexemType() == SEMICOLON)
                {
                    AST.startElement(LocalVariableDeclaration, currentToken);
                    AST.flushBuffer();
                    AST.endElement();
                    advance();
                }
                else if(currentToken.getLexemType() == ASSIGNMENT)
                {
                    AST.startElement(LocalVariableInitialization, currentToken);
                    AST.flushBuffer();
                    AST.addIdentifierToList(currentToken);
                    advance();
                    parseAssignmentRight();
                    if(currentToken.getLexemType() == SEMICOLON)
                    {
                        AST.endElement();
                        advance();
                    }
                    else
                        throw new ParsingException("Semicolon expected.", currentToken.getLine(), currentToken.getColumn());
                }
                else
                    throw new ParsingException("Semicolon or '=' expected.", currentToken.getLine(), currentToken.getColumn());
            }
            else if(currentToken.getLexemType() == LEFT_BRACKET) // ident  Method_call („;” |  Fetched_opt („=” Assign_right|e) „;”)
            {
                AST.startElement(FetchedObject, currentToken);
                AST.startElement(MethodCall, currentToken);
                AST.flushBuffer();
                parseMethodCall();
                AST.endElement();
                if(currentToken.getLexemType() == SEMICOLON)
                {
                    AST.endElement();
                    advance();
                }
                else if(currentToken.getLexemType() == DOT)
                {
                    parseFetchedOptional();
                    if(currentToken.getLexemType() == ASSIGNMENT)
                    {
                        AST.startElement(Assignment, currentToken);
                        AST.flushBuffer();
                        advance();
                        parseAssignmentRight();
                    }
                    if(currentToken.getLexemType() == SEMICOLON)
                    {
                        AST.endElement();
                        advance();
                    }
                    else
                        throw new ParsingException("Semicolon expected.", currentToken.getLine(), currentToken.getColumn());
                }
                else
                    throw new ParsingException("Semicolon or '.' expected.", currentToken.getLine(), currentToken.getColumn());

                AST.endElement();
            }
            else //ident Fetched_opt („=” Assign_right | e) „;”
            {
                AST.startElement(FetchedObject, currentToken);
                AST.flushBuffer();
                parseFetchedOptional();
                AST.endElement();
                if(currentToken.getLexemType() == ASSIGNMENT) {
                    AST.startElement(Assignment, currentToken);
                    AST.flushBuffer();
                    advance();
                    parseAssignmentRight();
                    AST.endElement();
                }
                if(currentToken.getLexemType() == SEMICOLON)
                {
                    advance();
                }
                else
                    throw new ParsingException("Semicolon expected.", currentToken.getLine(), currentToken.getColumn());
            }
            AST.endElement();
            parseMethodStatementsOptional();
        }
        //Method_stmnts_opt -> „int” ident („;” | Primit_field_init);
        else if(currentToken.getLexemType() == INT)
        {
            AST.startElement(PrimitiveFieldDeclaration, currentToken);
            advance();
            if(currentToken.getLexemType() == IDENTIFIER)
            {
                AST.storeInBuffer(currentToken);
                advance();
                parsePrimitiveFieldInitialization();
                AST.endElement();
            }
            else
                throw new ParsingException("Identifier expected.", currentToken.getLine(), currentToken.getColumn());
            parseMethodStatementsOptional();
        }
        else
            return;
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
            AST.addNumberToList(currentToken);
            advance();
        }
        else
            return;
    }
    private void parseMethodCall() throws ParsingException {
        if(currentToken.getLexemType() == LEFT_BRACKET)
        {
            advance();
            AST.startElement(CallParameters, currentToken);
            parseCallParameters();
            AST.endElement();
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

    private void parseCallParameters() throws ParsingException {
        if(currentToken.getLexemType() == NUMBER)
        {
            AST.addNumberToList(currentToken);
            advance();
            parseNextParametersOptional();
        }
        else if(currentToken.getLexemType() == THIS
                || currentToken.getLexemType() == IDENTIFIER)
        {
            parseFetchedCallOrIdentifier();
        }
        else
            return;
    }

    private void parseFetchedCallOrIdentifier() throws ParsingException
    {
        parseFetchedObject();
        if(currentToken.getLexemType() == LEFT_BRACKET)
        {
            AST.startElement(MethodCall, currentToken);
            AST.flushBuffer();
            parseMethodCall();
            AST.endElement();
        }
        else
            return;
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
    private void parseFetched() throws ParsingException {
        if(currentToken.getLexemType() == DOT) {
            advance();
            if (currentToken.getLexemType() == IDENTIFIER) {
                AST.storeInBuffer(currentToken);
                advance();
                if (currentToken.getLexemType() == LEFT_BRACKET) {
                    AST.startElement(MethodCall, currentToken);
                    AST.flushBuffer();
                    parseMethodCall();
                    AST.endElement();
                }
                else
                    AST.flushBuffer();
                parseFetchedOptional();
            } else
                throw new ParsingException("Identifier expected.", currentToken.getLine(), currentToken.getColumn());
        }
        else
            throw new ParsingException("'.' Expected.", currentToken.getLine(), currentToken.getColumn());
    }
    private void parseFetchedOptional() throws ParsingException {
        if(currentToken.getLexemType() == DOT)
        {
            parseFetched();
        }
        else
            return;
    }

    private void parseFetchedObject() throws ParsingException {
        if(currentToken.getLexemType() == THIS
                || currentToken.getLexemType() == IDENTIFIER)
        {
            AST.storeInBuffer(currentToken);
            advance();
            if(currentToken.getLexemType() == LEFT_BRACKET)
            {
                AST.startElement(MethodCall, currentToken);
                AST.flushBuffer();
                parseMethodCall();
                AST.endElement();
            }
            else
                AST.flushBuffer();
            parseFetchedOptional();
        }
        else
            throw new ParsingException("'This' or identifier expected.", currentToken.getLine(), currentToken.getColumn());
    }

    private void parseFetchedObjectOptional() throws ParsingException {
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
            AST.addNumberToList(currentToken);
            advance();
        }
        else if(currentToken.getLexemType() == THIS
                || currentToken.getLexemType() == IDENTIFIER)
        {
            parseFetchedCallOrIdentifier();
        }
        else if(currentToken.getLexemType() == NEW)
        {
            AST.startElement(NewCall, currentToken);
            advance();
            if(currentToken.getLexemType() == IDENTIFIER)
            {
                AST.addIdentifierToList(currentToken);
                advance();
                AST.startElement(MethodCall, currentToken);
                parseMethodCall();
                AST.endElement();
                AST.endElement();
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
            AST.addIdentifierToList(currentToken);
            advance();
            if (currentToken.getLexemType() == IDENTIFIER)
            {
                AST.addIdentifierToList(currentToken);
                advance();
                parseNextIdentifierOptional();
            }
            else
                throw new ParsingException("Identifier expected.", currentToken.getLine(), currentToken.getColumn());
        }
        else
            return;
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

    private void advance()
    {
        currentToken = scanner.getNextLexem();
    }
}
