package Parser;

import Lexems.Lexem;
import Refactor.ClassInheritanceRepresentation;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static Parser.AbstractSyntaxTree.ElementType.*;
public class AbstractSyntaxTree {



    public enum ElementType{
        PublicClassOrInterfaceDefinition, PackageDeclaration, ImportDeclaration, AbstractClassDefinition, ClassDefinition, InterfaceDefinition,
        ClassBody, InterfaceBody, ClassHeader, InterfaceHeader, ExtendList, ImplementsList, Statement, AbstractMethodDeclaration,
        MainMethod, MethodDefinition, PrimitiveFieldDeclaration, ConstructorDefinition, PrimitiveFieldInitialization,
        MethodBody, MethodParameterList, MethodCall, Assignment, NewCall, MethodDeclaration, ReturnStatement, MethodStatement,
        LocalVariableDeclaration, LocalVariableInitialization, FetchedObject, Code, ObjectFieldDeclaration, SuperCall, CallParameters,
        Identifier, ObjectFieldInitialization, Asterix, Number;
    }
    private ASTNode ASTRoot;
    private ASTNode currentOpenedElement;

    private List<Lexem> lexemBuffer;
    private ASTNode currentlyAnalyzed;
    AbstractSyntaxTree() {
        this.lexemBuffer = new ArrayList<>();
    }

    void clear() {
        ASTRoot = null;
        currentOpenedElement = null;
    }

    void startFile(Lexem currentToken) {
        ASTRoot = new ASTNode(Code, currentToken.getLine(), currentToken.getColumn());
        currentOpenedElement = ASTRoot;
    }

    void storeInBuffer(Lexem currentToken)
    {
        lexemBuffer.add(currentToken);
    }

    void flushBuffer()
    {
        if(!lexemBuffer.isEmpty())
        {
            currentOpenedElement.startsAtLine = lexemBuffer.get(0).getLine();
            currentOpenedElement.startsAtColumn = lexemBuffer.get(0).getColumn();
            lexemBuffer.forEach(this::addIdentifierToList);
            lexemBuffer.clear();
        }
    }

    void clearBuffer()
    {
        lexemBuffer.clear();
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

    void startElement(ElementType elementType, Lexem currentToken) {
        ASTNode child = new ASTNode(elementType, currentToken.getLine(), currentToken.getColumn());
        currentOpenedElement.putAsChild(child);
        currentOpenedElement = child;
    }

    void endElement()
    {
        currentOpenedElement = currentOpenedElement.parent;
    }

    void endElement(int endsAtLine, int endsAtColumn)
    {
        currentOpenedElement.endsAtLine = endsAtLine;
        currentOpenedElement.endsAtColumn = endsAtColumn;
        currentOpenedElement = currentOpenedElement.parent;
    }

    public boolean isRenameable(int lineClicked, int columnClicked) {
         ElementType element = findElementAt(lineClicked, columnClicked, ASTRoot);
         return element == Identifier;
    }

    public List<ClassInheritanceRepresentation> findClassesAndPutInContext(String filePath) {
        if(null != ASTRoot) {

            List<ClassInheritanceRepresentation> classesFound = new ArrayList<>();
            List<String> contextClasses = new ArrayList<>();
            for (ASTNode node : ASTRoot.children) {
                node.findClassesAndPutInContext(filePath, contextClasses, classesFound);
            }

        }
    }



    private ElementType findElementAt(int lineClicked, int columnClicked, ASTNode node) {
        if(node.children.isEmpty())
        {
            if (node.startsAtLine == lineClicked
                    && node.startsAtColumn <= columnClicked)
            {

                if(node.nodeType == Identifier)
                {
                    if(node.identifier.length() + node.startsAtColumn >= columnClicked) //Clicked at this Identifier
                    {
                        currentlyAnalyzed = node;
                        return Identifier;
                    }
                }
//                else if(node.nodeType.toString())
//                {
//
//                }
            }

        }
        else
        {

        }
        return null;
    }

//    public boolean isPullable(int lineClicked, int columnClicked) {
//         //TODO
//    }
//
//    public boolean isPushable(int lineClicked, int columnClicked) {
//         //TODO
//    }
//
//    public boolean isDelegable(int lineClicked, int columnClicked) {
//         //TODO
//    }

    @Override
    public String toString() {
        return ASTRoot.toString();
    }

    void writeOutputToFile()
    {
        File output = new File("src/main/resources/outputs/ASTOutput.txt");
        FileOutputStream fstream;
        try {
            fstream = new FileOutputStream(output);
        } catch (FileNotFoundException e) {
            System.out.println("File not found.");
            return;
        }
        PrintWriter pw = new PrintWriter(fstream);
        pw.print(this.toString());
        pw.close();
    }

    public class ASTNode{
        ASTNode parent;
        ArrayList<ASTNode> children;
        final ElementType nodeType;
        int startsAtLine;
        int startsAtColumn;
        int endsAtLine;
        int endsAtColumn;
        String identifier;
        int level;

        ASTNode(ElementType nodeType, int startLine, int startColumn) {
            this.children = new ArrayList<>();
            this.nodeType = nodeType;
            this.startsAtLine = startLine;
            this.startsAtColumn = startColumn;
            endsAtLine = 0;
            endsAtColumn = 0;
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

//            result.append(" startingLine=\"");
//            result.append(startsAtLine);
//            result.append("\" startingColumn=\"");
//            result.append(startsAtColumn);
//            result.append("\"");
            if(identifier!=null)
                result.append(" name=\"").append(identifier).append("\"");

            result.append(">\n");
            children.forEach(child -> result.append(child.toString()));

            for(int i = 0; i < level; i++)
                result.append("\t");

            result.append("<").append(nodeType.toString());
//            if(endsAtColumn!=0) {
//                result.append(" endingLine=\"");
//                result.append(endsAtLine);
//                result.append("\" endingColumn=\"");
//                result.append(endsAtColumn);
//                result.append("\"");
//            }

            result.append("/>");
            if(level!=0)
                result.append("\n");
            return result.toString();
        }


        private void findClassesAndPutInContext(String filePath, List<String> context, List<ClassInheritanceRepresentation> classes)
        {
            if(this.nodeType==AbstractClassDefinition
                    || this.nodeType==ClassDefinition)
            {
                List<String> path = Arrays.asList(filePath.split("/"));
                path.addAll(context);
                ASTNode classHeader = this.children.get(0);
                String className = classHeader.children.get(0).identifier;
                ClassInheritanceRepresentation classFound
                        = new ClassInheritanceRepresentation(path, className);

                classes.add(classFound);
                context.add(className);

                if(classHeader.children.size()>1
                        &&classHeader.children.get(1).nodeType==ExtendList)
                    classFound.setBaseClass(classHeader.children.get(1).children.get(0).identifier);
            }

            else if(this.nodeType==InterfaceDefinition)
        }
    }
}

