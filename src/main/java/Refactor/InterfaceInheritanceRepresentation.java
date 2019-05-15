package Refactor;

import java.util.ArrayList;
import java.util.List;

public class InterfaceInheritanceRepresentation {
    private List<String> filePath;
    private String className;
    private List<String> baseInterfaces;

    public InterfaceInheritanceRepresentation(List<String> filePath, String className) {
        this.filePath = filePath;
        this.className = className;
        this.baseInterfaces = new ArrayList<>();
    }

    public void addBaseInterface(String baseInterface) {
        baseInterfaces.add(baseInterface);
    }
}
