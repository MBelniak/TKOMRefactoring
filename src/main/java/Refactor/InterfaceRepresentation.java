package Refactor;

import Parser.AbstractSyntaxTree;

import java.util.ArrayList;
import java.util.List;

public class InterfaceRepresentation {
    private String filePath;
    private List<String> outerClassesOrInterfaces;
    private String interfaceName;
    private List<String> baseInterfaces;
    private AbstractSyntaxTree.ASTNode nodeRepresentation;
    private List<Statement> statements;

    public InterfaceRepresentation(String filePath, String className, AbstractSyntaxTree.ASTNode node, List<String> outerClassesOrInterfaces) {
        this.filePath = filePath;
        this.interfaceName = className;
        this.baseInterfaces = new ArrayList<>();
        this.nodeRepresentation = node;
        this.outerClassesOrInterfaces = new ArrayList<>(outerClassesOrInterfaces);
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

    String getInterfaceName() {
        return interfaceName;
    }

    List<Statement> getStatements() {
        return statements;
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
                .append(interfaceName)
                .append('\'')
                .append(", baseInterfaces=")
                .append(baseInterfaces)
                .append("}");

        return result.toString();
    }

}