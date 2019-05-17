package Refactor;

import Parser.AbstractSyntaxTree;
import java.util.ArrayList;
import java.util.List;

public class ClassInheritanceRepresentation {
    private List<String> filePath;
    private String className;
    private String baseClass;
    private List<String> interfacesImplemented;
    private AbstractSyntaxTree.ASTNode nodeRepresentation;
    private List<Statement> statements;

    public ClassInheritanceRepresentation(List<String> filePath, String className, AbstractSyntaxTree.ASTNode node) {
        this.filePath = filePath;
        this.className = className;
        this.interfacesImplemented = new ArrayList<>();
        this.nodeRepresentation = node;
        checkAllMoveableStatements();
    }

    public void setBaseClass(String baseClass) {
        this.baseClass = baseClass;
    }

    public void addInterfaceImplemented(String interfaceName) {
        this.interfacesImplemented.add(interfaceName);
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
        return "ClassInheritanceRepresentation{" +
                "filePath=" + filePath +
                ", className='" + className + '\'' +
                ", baseClass='" + baseClass + '\'' +
                ", interfacesImplemented=" + interfacesImplemented +
                '}';
    }

    String getClassName() {
        return className;
    }

    List<Statement> getStatements() {
        return statements;
    }
}
