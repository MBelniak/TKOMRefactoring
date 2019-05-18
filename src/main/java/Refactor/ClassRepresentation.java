package Refactor;

import Parser.AbstractSyntaxTree;
import java.util.ArrayList;
import java.util.List;

public class ClassRepresentation implements Representation{
    private String baseClass;
    private List<String> interfacesImplemented;
    private AbstractSyntaxTree.ASTNode nodeRepresentation;
    private List<Statement> statements;
    private String filePath;
    private List<String> outerClassesOrInterfaces;
    String name;

    public ClassRepresentation(String filePath, String className, AbstractSyntaxTree.ASTNode node, List<String> outerClassesOrInterfaces) {
        this.filePath = filePath;
        this.name = className;
        this.interfacesImplemented = new ArrayList<>();
        this.nodeRepresentation = node;
        this.outerClassesOrInterfaces = new ArrayList<>(outerClassesOrInterfaces);
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
    String getClassName() {
        return name;
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
}
