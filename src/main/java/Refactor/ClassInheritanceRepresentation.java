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

    public ClassInheritanceRepresentation(List<String> filePath, String className, AbstractSyntaxTree.ASTNode node) {
        this.filePath = filePath;
        this.className = className;
        this.interfacesImplemented = new ArrayList<>();
        this.nodeRepresentation = node;
    }

    public void setBaseClass(String baseClass) {
        this.baseClass = baseClass;
    }

    public void addInterfaceImplemented(String interfaceName) {
        this.interfacesImplemented.add(interfaceName);
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
}
