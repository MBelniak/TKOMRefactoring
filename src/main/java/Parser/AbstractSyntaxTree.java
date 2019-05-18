package Parser;

import Lexems.Lexem;
import Refactor.ClassRepresentation;
import Refactor.InterfaceRepresentation;
import Refactor.Representation;
import Refactor.Statement;
import javafx.util.Pair;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import static Parser.AbstractSyntaxTree.ElementType.*;
public class AbstractSyntaxTree {



    public enum ElementType{
        PackageDeclaration, ImportDeclaration, AbstractClassDefinition, ClassDefinition, InterfaceDefinition,
        ClassBody, InterfaceBody, ClassHeader, InterfaceHeader, ExtendList, ImplementsList, Statement, AbstractMethodDeclaration,
        MainMethod, MethodDefinition, PrimitiveFieldDeclaration, ConstructorDefinition, PrimitiveFieldInitialization,
        MethodBody, MethodParameterList, MethodCall, Assignment, NewCall, MethodDeclaration, ReturnStatement, MethodStatement,
        LocalVariableDeclaration, LocalVariableInitialization, FetchedObject, Code, ObjectFieldDeclaration, SuperCall, CallParameters,
        Identifier, ObjectFieldInitialization, Asterix, Number, PrimitiveFieldDefinition
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

    public List<Representation> findClassesAndInterfacesAndPutInContext(String filePath) {
        if(null != ASTRoot) {

            List<Representation> classesAndInterfacesFound = new ArrayList<>();
            List<String> contextClassesOrIntefraces = new ArrayList<>();
            for (ASTNode node : ASTRoot.children) {
                node.findClassesOrInterfacesAndPutInContext(filePath, contextClassesOrIntefraces, classesAndInterfacesFound);
            }
            return classesAndInterfacesFound;
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


        private void findClassesOrInterfacesAndPutInContext(String filePath, List<String> context, List<Representation> classesAndInterfaces)
        {
            if (this.nodeType == AbstractClassDefinition
                    || this.nodeType == ClassDefinition) {
                ASTNode classHeader = this.children.get(0);

                Statement.AccessModifier accessModifier = null;

                if (classHeader.children.get(0).identifier.equals("public"))
                    accessModifier = Refactor.Statement.AccessModifier.Public;
                if (classHeader.children.get(0).identifier.equals("protected"))
                    accessModifier = Refactor.Statement.AccessModifier.Protected;
                if (classHeader.children.get(0).identifier.equals("private"))
                    accessModifier = Refactor.Statement.AccessModifier.Private;

                String className;

                if (null != accessModifier)
                    className = classHeader.children.get(1).identifier;
                else
                    className = classHeader.children.get(0).identifier;

                ClassRepresentation classFound
                        = new ClassRepresentation(filePath, className, this, context);

                if (null != accessModifier && classHeader.children.size() > 2) {
                    if (classHeader.children.get(2).nodeType == ExtendList) {
                        classFound.setBaseClass(classHeader.children.get(2).children.get(0).identifier);
                        if (classHeader.children.size() > 3) {
                            for (int i = 0; i < classHeader.children.get(3).children.size(); i++)
                                classFound.addInterfaceImplemented(classHeader.children.get(3).children.get(i).identifier);
                        }
                    } else {
                        for (int i = 0; i < classHeader.children.get(2).children.size(); i++)
                            classFound.addInterfaceImplemented(classHeader.children.get(2).children.get(i).identifier);
                    }
                } else if (accessModifier == null && classHeader.children.size() > 1) {
                    if (classHeader.children.get(1).nodeType == ExtendList) {
                        classFound.setBaseClass(classHeader.children.get(1).children.get(0).identifier);
                        if (classHeader.children.size() > 2) {
                            for (int i = 0; i < classHeader.children.get(2).children.size(); i++)
                                classFound.addInterfaceImplemented(classHeader.children.get(2).children.get(i).identifier);
                        }
                    } else {
                        for (int i = 0; i < classHeader.children.get(1).children.size(); i++)
                            classFound.addInterfaceImplemented(classHeader.children.get(1).children.get(i).identifier);
                    }
                }

                classesAndInterfaces.add(classFound);
                context.add(className);
                for (ASTNode node : this.children) {
                    node.findClassesOrInterfacesAndPutInContext(filePath, context, classesAndInterfaces);
                }
                context.remove(className);
            } else if (this.nodeType == InterfaceDefinition) {
                ASTNode interfaceHeader = this.children.get(0);
                Statement.AccessModifier accessModifier = null;

                if (interfaceHeader.children.get(0).identifier.equals("public"))
                    accessModifier = Refactor.Statement.AccessModifier.Public;
                if (interfaceHeader.children.get(0).identifier.equals("protected"))
                    accessModifier = Refactor.Statement.AccessModifier.Protected;
                if (interfaceHeader.children.get(0).identifier.equals("private"))
                    accessModifier = Refactor.Statement.AccessModifier.Private;

                String interfaceName;

                if (null != accessModifier)
                    interfaceName = interfaceHeader.children.get(1).identifier;
                else
                    interfaceName = interfaceHeader.children.get(0).identifier;

                InterfaceRepresentation interfaceFound
                        = new InterfaceRepresentation(filePath, interfaceName, this, context);

                if (null != accessModifier && interfaceHeader.children.size() > 2) {
                    for (int i = 0; i < interfaceHeader.children.get(2).children.size(); i++)
                        interfaceFound.addBaseInterface(interfaceHeader.children.get(2).children.get(i).identifier);
                } else if (accessModifier == null && interfaceHeader.children.size() > 1) {
                    for (int i = 0; i < interfaceHeader.children.get(1).children.size(); i++)
                        interfaceFound.addBaseInterface(interfaceHeader.children.get(1).children.get(i).identifier);
                }

                classesAndInterfaces.add(interfaceFound);
                context.add(interfaceName);
                for (ASTNode node : this.children) {
                    node.findClassesOrInterfacesAndPutInContext(filePath, context, classesAndInterfaces);
                }
                context.remove(interfaceName);
            } else
                for (ASTNode node : this.children) {
                    node.findClassesOrInterfacesAndPutInContext(filePath, context, classesAndInterfaces);
                }
        }
    }
}

