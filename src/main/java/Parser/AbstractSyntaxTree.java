package Parser;

import Lexems.Lexem;
import Refactor.ClassInheritanceRepresentation;
import Refactor.InterfaceInheritanceRepresentation;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static Parser.AbstractSyntaxTree.ElementType.*;
public class AbstractSyntaxTree {



    public enum ElementType{
        PackageDeclaration, ImportDeclaration, AbstractClassDefinition, ClassDefinition, InterfaceDefinition,
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
        if(currentToken.getValue() == null) {
            String keyword = currentToken.getLexemType().toString().toLowerCase();
            currentOpenedElement.putAsChild(new ASTNode(Identifier, currentToken.getLine(), currentToken.getColumn(), keyword));

        }
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

//    public boolean isRenameable(int lineClicked, int columnClicked) {
//         ElementType element = findElementAt(lineClicked, columnClicked, ASTRoot);
//         return element == Identifier;
//    }

    public List<ClassInheritanceRepresentation> findClassesAndPutInContext(String filePath) {
        if(null != ASTRoot) {

            List<ClassInheritanceRepresentation> classesFound = new ArrayList<>();
            List<String> contextClasses = new ArrayList<>();
            for (ASTNode node : ASTRoot.children) {
                node.findClassesAndPutInContext(filePath, contextClasses, classesFound);
            }
            return classesFound;
        }
        return null;
    }

    public List<InterfaceInheritanceRepresentation> findInterfacesAndPutInContext(String filePath) {
        if(null != ASTRoot) {

            List<InterfaceInheritanceRepresentation> interfacesFound = new ArrayList<>();
            List<String> contextInterfaces = new ArrayList<>();
            for (ASTNode node : ASTRoot.children) {
                node.findInterfacesAndPutInContext(filePath, contextInterfaces, interfacesFound);
            }
            return interfacesFound;
        }
        return null;
    }

//    private ElementType findElementAt(int lineClicked, int columnClicked, ASTNode node) {
//        if(node.children.isEmpty())
//        {
//            if (node.startsAtLine == lineClicked
//                    && node.startsAtColumn <= columnClicked)
//            {
//
//                if(node.nodeType == Identifier)
//                {
//                    if(node.identifier.length() + node.startsAtColumn >= columnClicked) //Clicked at this Identifier
//                    {
//                        currentlyAnalyzed = node;
//                        return Identifier;
//                    }
//                }
////                else if(node.nodeType.toString())
////                {
////
////                }
//            }
//
//        }
//        else
//        {
//
//        }
//        return null;
//    }

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
        public ASTNode parent;
        public ArrayList<ASTNode> children;
        public final ElementType nodeType;
        public int startsAtLine;
        public int startsAtColumn;
        public int endsAtLine;
        public int endsAtColumn;
        public String identifier;
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
                List<String> path = new ArrayList<>(Arrays.asList(filePath.split("/")));
                path.addAll(context);
                ASTNode classHeader = this.children.get(0);
                boolean isPublic = classHeader.children.get(0).identifier.equals("PUBLIC");

                String className;
                if(isPublic)
                    className = classHeader.children.get(1).identifier;
                else
                    className = classHeader.children.get(0).identifier;

                ClassInheritanceRepresentation classFound
                        = new ClassInheritanceRepresentation(path, className, this);

                if(isPublic && classHeader.children.size()>2)
                {
                    if(classHeader.children.get(2).nodeType == ExtendList) {
                        classFound.setBaseClass(classHeader.children.get(2).children.get(0).identifier);
                        if (classHeader.children.size() > 3) {
                            for (int i = 0; i < classHeader.children.get(3).children.size(); i++)
                                classFound.addInterfaceImplemented(classHeader.children.get(3).children.get(i).identifier);
                        }
                    }
                    else
                    {
                        for (int i = 0; i < classHeader.children.get(2).children.size(); i++)
                            classFound.addInterfaceImplemented(classHeader.children.get(2).children.get(i).identifier);
                    }
                }
                else if(!isPublic && classHeader.children.size()>1)
                {
                    if(classHeader.children.get(1).nodeType == ExtendList) {
                        classFound.setBaseClass(classHeader.children.get(1).children.get(0).identifier);
                        if (classHeader.children.size() > 2) {
                            for (int i = 0; i < classHeader.children.get(2).children.size(); i++)
                                classFound.addInterfaceImplemented(classHeader.children.get(2).children.get(i).identifier);
                        }
                    }
                    else
                    {
                        for (int i = 0; i < classHeader.children.get(1).children.size(); i++)
                            classFound.addInterfaceImplemented(classHeader.children.get(1).children.get(i).identifier);
                    }
                }

                classes.add(classFound);
                context.add(className);
            }

            for (ASTNode node : this.children) {
                node.findClassesAndPutInContext(filePath, context, classes);
            }
        }
        private void findInterfacesAndPutInContext(String filePath, List<String> context, List<InterfaceInheritanceRepresentation> interfaces)
        {
            if(this.nodeType==InterfaceDefinition)
            {
                List<String> path = Arrays.asList(filePath.split("/"));
                path.addAll(context);
                ASTNode interfaceHeader = this.children.get(0);
                boolean isPublic = interfaceHeader.children.get(0).identifier.equals("PUBLIC");

                String interfaceName;
                if(isPublic)
                    interfaceName = interfaceHeader.children.get(1).identifier;
                else
                    interfaceName = interfaceHeader.children.get(0).identifier;
                InterfaceInheritanceRepresentation interfaceFound
                        = new InterfaceInheritanceRepresentation(path, interfaceName, this);

                if(isPublic && interfaceHeader.children.size()>2)
                {
                    for(int i = 0; i<interfaceHeader.children.get(2).children.size(); i++)
                        interfaceFound.addBaseInterface(interfaceHeader.children.get(2).children.get(i).identifier);
                }
                else if(!isPublic && interfaceHeader.children.size()>1)
                {
                    for(int i = 0; i<interfaceHeader.children.get(1).children.size(); i++)
                        interfaceFound.addBaseInterface(interfaceHeader.children.get(1).children.get(i).identifier);
                }

                interfaces.add(interfaceFound);
                context.add(interfaceName);
            }

            for (ASTNode node : this.children) {
                node.findInterfacesAndPutInContext(filePath, context, interfaces);
            }
        }
    }
}

