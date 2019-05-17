package Refactor;

import Parser.AbstractSyntaxTree;

import java.util.ArrayList;
import java.util.List;

public class InterfaceInheritanceRepresentation {
    private List<String> filePath;
    private String interfaceName;
    private List<String> baseInterfaces;
    private AbstractSyntaxTree.ASTNode nodeRepresentation;
    private List<Statement> statements;

    public InterfaceInheritanceRepresentation(List<String> filePath, String className, AbstractSyntaxTree.ASTNode node) {
        this.filePath = filePath;
        this.interfaceName = className;
        this.baseInterfaces = new ArrayList<>();
        this.nodeRepresentation = node;
        checkAllMoveableStatements();
    }

    public void addBaseInterface(String baseInterface) {
        baseInterfaces.add(baseInterface);
    }

    private void checkAllMoveableStatements()
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
        return "InterfaceInheritanceRepresentation{" +
                "filePath=" + filePath +
                ", interfaceName='" + interfaceName + '\'' +
                ", baseInterfaces=" + baseInterfaces +
                '}';
    }

    String getInterfaceName() {
        return interfaceName;
    }

    List<Statement> getStatements() {
        return statements;
    }
}


