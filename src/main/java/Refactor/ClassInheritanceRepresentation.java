package Refactor;

import java.util.ArrayList;
import java.util.List;

public class ClassInheritanceRepresentation {
    private List<String> filePath;
    private String className;
    private String baseClass;

    public ClassInheritanceRepresentation(List<String> filePath, String className) {
        this.filePath = filePath;
        this.className = className;
        this.baseClass = null;
    }

    public void setBaseClass(String baseClass) {
        this.baseClass = baseClass;
    }

}
