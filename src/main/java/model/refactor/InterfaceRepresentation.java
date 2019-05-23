package model.refactor;

import model.exceptions.SemanticException;
import model.parser.AbstractSyntaxTree;
import model.util.PackagesUtils;

import java.util.*;
import java.util.stream.Collectors;

public class InterfaceRepresentation implements Representation{
    private List<String> baseInterfaces;
    private List<InterfaceRepresentation> baseInterfacesRepresentations;
    private List<String> subInterfacesOrClasses;
    private List<Representation> subInterfacesOrClassesRepresentations;
    private AbstractSyntaxTree.ASTNode nodeRepresentation;
    private List<Statement> statements;
    private String filePath;
    private List<String> outerClassesOrInterfaces;
    private List<String> imports;
    String name;


    public InterfaceRepresentation(String filePath, String className, AbstractSyntaxTree.ASTNode node, List<String> outerClassesOrInterfaces) {
        this.filePath = filePath;
        this.name = className;
        this.baseInterfaces = new ArrayList<>();
        this.nodeRepresentation = node;
        this.outerClassesOrInterfaces = new ArrayList<>(outerClassesOrInterfaces);
        this.baseInterfacesRepresentations = new ArrayList<>();
        this.subInterfacesOrClasses = new ArrayList<>();
        this.subInterfacesOrClassesRepresentations = new ArrayList<>();
        checkAllMovableStatements();
    }

    public void addBaseInterface(String baseInterface) {
        baseInterfaces.add(baseInterface);
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
            result.delete(result.length()-2, result.length());
            result.append("]");
        }
        result.append(", interfaceName='")
                .append(name)
                .append('\'')
                .append(", baseInterfaces=")
                .append(baseInterfaces)
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

    }

    @Override
    public void checkIfInterfacesVisible(Map<String, List<Representation>> representationsInFiles) throws SemanticException {
        if(baseInterfaces.isEmpty())
            return;
        for (String oneInterface : baseInterfaces) {

            InterfaceRepresentation oneInterfaceFound;

            if((oneInterfaceFound = PackagesUtils.checkVisibilityInTheSameFile(representationsInFiles, this, oneInterface, outerClassesOrInterfaces, this.filePath)) == null)
                if((oneInterfaceFound = PackagesUtils.checkVisibilityInTheSamePackage(representationsInFiles, this, oneInterface, this.filePath)) == null)
                    if((oneInterfaceFound = PackagesUtils.checkVisibilityByImports(representationsInFiles, imports,this, oneInterface)) == null)
                        throw new SemanticException("Cannot find base interface for " + getName() + " in file " + getFilePath());
            baseInterfacesRepresentations.add(oneInterfaceFound);
            oneInterfaceFound.addSubInterfaceOrClass(this.name);
            oneInterfaceFound.addSubInterfaceOrClassRepresentation(this);
        }
    }

    @Override
    public List<String> getBases() {
        return new ArrayList<>(baseInterfaces);
    }

    @Override
    public List<String> getExtends() {
        return baseInterfaces;
    }

    @Override
    public Representation getBaseByName(String className) {
        for(Representation representation : baseInterfacesRepresentations)
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
        for (Representation subclassOrInterfaceRepresentation : subInterfacesOrClassesRepresentations) {
            if(subclassOrInterfaceRepresentation.getName().equals(destClass))
                return subclassOrInterfaceRepresentation;
        }
        return null;
    }

    @Override
    public List<String> getSubclassesOrSubinterfaces() {
        return subInterfacesOrClasses;
    }

    void addSubInterfaceOrClass(String subInterfaceOrClass) {
        this.subInterfacesOrClasses.add(subInterfaceOrClass);
    }

    void addSubInterfaceOrClassRepresentation(Representation subInterfaceOrClassRepresentation) {
        this.subInterfacesOrClassesRepresentations.add(subInterfaceOrClassRepresentation);
    }
}