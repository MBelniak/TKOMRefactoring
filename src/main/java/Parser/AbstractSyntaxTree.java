package Parser;

import Scanner.Lexem;

import java.util.ArrayList;
import java.util.List;

import static Parser.AbstractSyntaxTree.ElementType.*;
class AbstractSyntaxTree {
    public enum ElementType{
        PublicClassDefinition, PackageDeclaration, ImportDeclaration, AbstractClassDefinition, ClassDefinition, InterfaceDefinition,
        ClassBody, InterfaceBody, ClassHeader, InterfaceHeader, ExtendList, ImplementsList, Statement, AbstractMethodDeclaration,
        MainMethod, MethodDefinition, PrimitiveFieldDeclaration, ConstructorDefinition, ObjectInitialization, PrimitiveFieldInitialization,
        MethodBody, MethodParameterList, MethodCall, Assignment, NewCall, MethodDeclaration, ReturnStatement, MethodStatement,
        LocalVariableDeclaration, LocalVariableInitialization, FetchedObject, StartCode, ObjectFieldDeclaration, SuperCall, CallParameters, Identifier
    }

    private ASTNode ASTRoot;
    private ASTNode currentOpenedElement;

    void clear() {
        ASTRoot = null;
        currentOpenedElement = null;
    }

    void startFile(Lexem currentToken) {
        ASTRoot = new ASTNode(StartCode, currentToken.getLine(), currentToken.getColumn());
        currentOpenedElement = ASTRoot;
    }

    void addIdentifierToList(Lexem currentToken) {
        if(currentToken.getValue() == null)
            currentOpenedElement.putAsChild(new ASTNode(Identifier, currentToken.getLine(), currentToken.getColumn(), currentToken.getLexemType().toString()));
        else
            currentOpenedElement.putAsChild(new ASTNode(Identifier, currentToken.getLine(), currentToken.getColumn(), currentToken.getValue()));
    }

    void organizeStatements()
    {

    }
//    void addTypeToList(Lexem currentLexem)
//    {
//        currentOpenedElement.putAsChild(new ASTNode());
//    }
//
//    void addKeywordToList(Lexem currentLexem)
//    {
//
//    }

    void startElement(ElementType elementType, Lexem currentToken) {
        ASTNode child = new ASTNode(elementType, currentToken.getLine(), currentToken.getColumn());
        currentOpenedElement.putAsChild(child);
        currentOpenedElement = child;
    }

    void endElement() {
        currentOpenedElement = currentOpenedElement.parent;
    }

    @Override
    public String toString() {
        return ASTRoot.toString();
    }

    private class ASTNode{
        ASTNode parent;
        List<ASTNode> children;
        ElementType nodeType;
        int startsAtLine;
        int startsAtColumn;
        String identifier;

        ASTNode(ElementType nodeType, int startLine, int startColumn) {
            this.children = new ArrayList<>();
            this.nodeType = nodeType;
            this.startsAtLine = startLine;
            this.startsAtColumn = startColumn;
        }

        ASTNode(ElementType nodeType, int startsAtLine, int startsAtColumn, String identifier) {
            this.children = new ArrayList<>();
            this.nodeType = nodeType;
            this.startsAtLine = startsAtLine;
            this.startsAtColumn = startsAtColumn;
            this.identifier = identifier;
        }

        void putAsChild(ASTNode child)
        {
            children.add(child);
            child.parent = this;
        }

        @Override
        public String toString()
        {
            StringBuilder result = new StringBuilder("<");
            result.append(nodeType.toString());
            if(identifier!=null)
                result.append(" name=\"").append(identifier).append("\"");
            result.append(">\n");
            children.forEach(child -> result.append(child.toString()));
            result.append("<").append(nodeType.toString()).append("/>\n");
            return result.toString();
        }
    }
}

