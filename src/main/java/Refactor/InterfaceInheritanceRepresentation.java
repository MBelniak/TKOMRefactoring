package Refactor;

import Parser.AbstractSyntaxTree;

import java.util.ArrayList;
import java.util.List;

public class InterfaceInheritanceRepresentation {
    private List<String> filePath;
    private String className;
    private List<String> baseInterfaces;
    private AbstractSyntaxTree.ASTNode nodeRepresentation;

    public InterfaceInheritanceRepresentation(List<String> filePath, String className, AbstractSyntaxTree.ASTNode node) {
        this.filePath = filePath;
        this.className = className;
        this.baseInterfaces = new ArrayList<>();
        this.nodeRepresentation = node;
    }

    public void addBaseInterface(String baseInterface) {
        baseInterfaces.add(baseInterface);
    }

    @Override
    public String toString() {
        return "InterfaceInheritanceRepresentation{" +
                "filePath=" + filePath +
                ", className='" + className + '\'' +
                ", baseInterfaces=" + baseInterfaces +
                '}';
    }
}
