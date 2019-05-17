package Refactor;

import Parser.AbstractSyntaxTree;

import java.util.ArrayList;
import java.util.List;

public class InterfaceInheritanceRepresentation {
    private List<String> filePath;
    private String interfaceName;
    private List<String> baseInterfaces;
    private AbstractSyntaxTree.ASTNode nodeRepresentation;

    public InterfaceInheritanceRepresentation(List<String> filePath, String className, AbstractSyntaxTree.ASTNode node) {
        this.filePath = filePath;
        this.interfaceName = className;
        this.baseInterfaces = new ArrayList<>();
        this.nodeRepresentation = node;
    }

    public void addBaseInterface(String baseInterface) {
        baseInterfaces.add(baseInterface);
    }

    List<Statement> listAllMoveableStatements()
    {
        List<Statement> statements = new ArrayList<>();
        nodeRepresentation.children.forEach(child ->
                statements.add(StatementFactory.getStatement(child)));

        return statements;
    }

    @Override
    public String toString() {
        return "InterfaceInheritanceRepresentation{" +
                "filePath=" + filePath +
                ", interfaceName='" + interfaceName + '\'' +
                ", baseInterfaces=" + baseInterfaces +
                '}';
    }
}


