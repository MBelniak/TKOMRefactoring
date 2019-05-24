package model.refactor;

import model.exceptions.SemanticException;
import model.parser.AbstractSyntaxTree;
import model.util.PackagesUtils;

import java.util.*;
import java.util.stream.Collectors;

public class ClassRepresentation implements Representation{
    private String baseClass;
    private ClassRepresentation baseClassRepresentation;
    private List<String> subclasses;
    private List<Representation> subclassesRepresentations;
    private List<String> interfacesImplemented;
    private List<InterfaceRepresentation> interfacesImplementedRepresentations;
    private AbstractSyntaxTree.ASTNode nodeRepresentation;
    private List<Statement> statements;
    private String filePath;        //A\B\C\d.txt
    private List<String> outerClassesOrInterfaces;
    private List<String> imports;
    String name;

    public ClassRepresentation(String filePath, String className, AbstractSyntaxTree.ASTNode node, List<String> outerClassesOrInterfaces) {
        this.filePath = filePath;
        this.name = className;
        this.interfacesImplemented = new ArrayList<>();
        this.nodeRepresentation = node;
        this.outerClassesOrInterfaces = new ArrayList<>(outerClassesOrInterfaces);
        this.subclasses = new ArrayList<>();
        this.interfacesImplementedRepresentations = new ArrayList<>();
        this.statements = new ArrayList<>();
        this.subclassesRepresentations = new ArrayList<>();
        checkAllMovableStatements();
    }

    public void setBaseClass(String baseClass) {
        this.baseClass = baseClass;
    }

    public void addInterfaceImplemented(String interfaceName) {
        this.interfacesImplemented.add(interfaceName);
    }

    private void checkAllMovableStatements()
    {
        List<Statement> statements = new ArrayList<>();
        nodeRepresentation.children.get(1).children.forEach(child ->
            {
                Statement stmntToAdd = StatementFactory.getStatement(child.children.get(0));
                if(stmntToAdd != null)
                    statements.add(stmntToAdd);
            });
        this.statements = statements;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder("ClassRepresentation{" +
                "filePath=" + filePath);
        if(!outerClassesOrInterfaces.isEmpty())
        {
            result.append(", outerClassesAndInterfaces=[");
            outerClassesOrInterfaces.forEach(CI -> result.append(CI).append(", "));
            result.delete(result.toString().length()-2, result.toString().length());
            result.append("]");
        }
        result.append(", className='")
                .append(name)
                .append('\'')
                .append(", baseClass='")
                .append(baseClass)
                .append('\'')
                .append(", interfacesImplemented=")
                .append(interfacesImplemented)
                .append("}");

        return result.toString();
    }

    @Override
    public List<String> getOuterClassesOrInterfaces() {
        return outerClassesOrInterfaces;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public List<Statement> getStatements() {
        return statements;
    }

    @Override
    public AbstractSyntaxTree.ASTNode getNodeRep() {
        return nodeRepresentation;
    }

    @Override
    public void checkIfBaseVisible(Map<String, List<Representation>> representationsInFiles) throws SemanticException {
        if(baseClass == null)
            return;

        if((baseClassRepresentation = PackagesUtils.checkVisibilityInTheSameFile(representationsInFiles, this, baseClass, outerClassesOrInterfaces, this.filePath)) == null)
            if((baseClassRepresentation = PackagesUtils.checkVisibilityInTheSamePackage(representationsInFiles, this, baseClass, this.filePath)) == null)
                if((baseClassRepresentation = PackagesUtils.checkVisibilityByImports(representationsInFiles, imports, this, baseClass)) == null)
                    throw new SemanticException("Cannot find base class for " + getName() + " in file " + getFilePath());

        baseClassRepresentation.addSubclassRepresentation(this);
        baseClassRepresentation.addSubclass(this.name);
    }

    @Override
    public void checkIfInterfacesVisible(Map<String, List<Representation>> representationsInFiles) throws SemanticException{
        if(interfacesImplemented.isEmpty())
            return;

        InterfaceRepresentation interfaceRepresentation = new InterfaceRepresentation("a", "a", this.nodeRepresentation, new ArrayList<>());
        for (String oneInterface : interfacesImplemented) {

            InterfaceRepresentation oneInterfaceFound;

            if((oneInterfaceFound = PackagesUtils
                                    .checkVisibilityInTheSameFile(representationsInFiles, interfaceRepresentation, oneInterface, outerClassesOrInterfaces, this.filePath)) == null)
                if((oneInterfaceFound = PackagesUtils.checkVisibilityInTheSamePackage(representationsInFiles, interfaceRepresentation, oneInterface, this.filePath)) == null)
                    if((oneInterfaceFound = PackagesUtils.checkVisibilityByImports(representationsInFiles, imports, interfaceRepresentation, oneInterface)) == null)
                        throw new SemanticException("Cannot find base interface for " + getName() + " in file " + getFilePath());
            interfacesImplementedRepresentations.add(oneInterfaceFound);
            oneInterfaceFound.addSubInterfaceOrClass(this.name);
            oneInterfaceFound.addSubInterfaceOrClassRepresentation(this);
        }
    }

    @Override
    public List<String> getBases()
    {
        List<String> result = new ArrayList<>(interfacesImplemented);
        if(baseClass!=null)
            result.add(baseClass);
        return result;
    }

    @Override
    public List<String> getExtends() {
        List<String> result = new ArrayList<>();
        if(baseClass!=null)
            result.add(baseClass);
        return result;
    }

    @Override
    public Representation getBaseByName(String className) {
        if(baseClassRepresentation == null)
            return null;
        if(baseClassRepresentation.getName().equals(className))
            return baseClassRepresentation;
        for(Representation representation : interfacesImplementedRepresentations)
        {
            if(representation.getName().equals(className))
                return representation;
        }
        return null;
    }

    @Override
    public String getFilePath() {
        return filePath;
    }

    @Override
    public String getAccessModifier() {
        AbstractSyntaxTree.ASTNode header = nodeRepresentation.children.get(0);
        if(header.children.get(0).identifier.equals("public")
                ||header.children.get(0).identifier.equals("protected")
                ||header.children.get(0).identifier.equals("private"))
            return header.children.get(0).identifier;
        return "";
    }

    @Override
    public void setImports(List<String> imports) {
        this.imports = imports;
    }

    @Override
    public boolean checkIfSameStatementExists(Statement statement, int count) {
        if(statement==null)
            return false;
        List<Statement> statementsFound = statements.stream()
                .filter(stmnt -> stmnt.getStatementSignature().equals(statement.getStatementSignature())
                        && stmnt.getCategory().equals(statement.getCategory()))
                .collect(Collectors.toList());
        return statementsFound.size() >= count;
    }

    @Override
    public Representation getSubByName(String destClass) {
        for (Representation subclassRepresentation : subclassesRepresentations) {
            if(subclassRepresentation.getName().equals(destClass))
                return subclassRepresentation;
        }
        return null;
    }

    @Override
    public List<String> getSubclassesOrSubinterfaces() {
        return subclasses;
    }

    private void addSubclassRepresentation(Representation subclassesRepresentations) {
        this.subclassesRepresentations.add(subclassesRepresentations);
    }

    private void addSubclass(String subclass) {
        this.subclasses.add(subclass);
    }
}