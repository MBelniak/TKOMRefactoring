package Parser;

import Scanner.Lexem;

import java.util.ArrayList;
import java.util.List;

import static Parser.AbstractSyntaxTree.ElementType.*;
class AbstractSyntaxTree {
    public enum ElementType{
        PublicClassDefinition, PackageDeclaration, ImportDeclaration, AbstractClassDefinition, ClassDefinition, InterfaceDefinition,
        ClassBody, InterfaceBody, ClassHeader, InterfaceHeader, ExtendList, ImplementsList, Statement, AbstractMethodDeclaration,
        MainMethod, MethodDefinition, PrimitiveFieldDeclaration, ConstructorDefinition, PrimitiveFieldInitialization,
        MethodBody, MethodParameterList, MethodCall, Assignment, NewCall, MethodDeclaration, ReturnStatement, MethodStatement,
        LocalVariableDeclaration, LocalVariableInitialization, FetchedObject, Code, ObjectFieldDeclaration, SuperCall, CallParameters,
        Identifier, ObjectFieldInitialization, Number
    }

    private ASTNode ASTRoot;
    private ASTNode currentOpenedElement;
    private List<Lexem> lexemBuffer;

     AbstractSyntaxTree() {
        this.lexemBuffer = new ArrayList<>();
    }

    public List<Lexem> getLexemBuffer() {
        return lexemBuffer;
    }

    void clear() {
        ASTRoot = null;
        currentOpenedElement = null;
    }

    void storeInBuffer(Lexem currentToken)
    {
        lexemBuffer.add(currentToken);
    }

    void flushBuffer()
    {
        lexemBuffer.forEach(this::addIdentifierToList);
        lexemBuffer.clear();
    }

    void startFile(Lexem currentToken) {
        ASTRoot = new ASTNode(Code, currentToken.getLine(), currentToken.getColumn());
        currentOpenedElement = ASTRoot;
    }

    void addIdentifierToList(Lexem currentToken) {
        if(currentToken.getValue() == null)
            currentOpenedElement.putAsChild(new ASTNode(Identifier, currentToken.getLine(), currentToken.getColumn(), currentToken.getLexemType().toString()));
        else
            currentOpenedElement.putAsChild(new ASTNode(Identifier, currentToken.getLine(), currentToken.getColumn(), currentToken.getValue()));
    }

    void addNumberToList(Lexem currentToken)
    {
        currentOpenedElement.putAsChild(new ASTNode(Number, currentToken.getLine(), currentToken.getColumn(), currentToken.getValue()));
    }
    private void organizeStatements()
    {
            //AbstractClassDefinition or AbstractMethodDeclaration or MethodDefinition
            // or PrimitiveFieldInitialization or ConstructorDefinition or ObjectFieldDeclaration or MainMethod
            // or ClassDefinition
//        List<ASTNode> concernedNodes = new ArrayList<>();
//        ASTRoot.findConcernedNodes(concernedNodes);
//        concernedNodes.forEach(node ->
//        {
//            for(int i = 0; i<node.children.size(); i++)
//            {
//                if(node.children.get(i).nodeType == AbstractClassDefinition)
//            }
//        });
    }

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
        ArrayList<ASTNode> children;
        final ElementType nodeType;
        int startsAtLine;
        int startsAtColumn;
        String identifier;
        int level;

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
            child.level = this.level+1;
            child.parent = this;
        }

        @Override
        public String toString()
        {
            StringBuilder result = new StringBuilder();

            for(int i = 0; i < level; i++)
                result.append("\t");

            result.append("<");
            result.append(nodeType.toString());

            if(identifier!=null)
                result.append(" name=\"").append(identifier).append("\"");

            result.append(">\n");
            children.forEach(child -> result.append(child.toString()));

            for(int i = 0; i < level; i++)
                result.append("\t");

            result.append("<").append(nodeType.toString()).append("/>\n");
            return result.toString();
        }

    }
}

