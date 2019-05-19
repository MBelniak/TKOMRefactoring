package model.refactor;

import model.exceptions.SemanticException;
import model.parser.AbstractSyntaxTree;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class ClassRepresentation implements Representation{
    private String baseClass;
    private ClassRepresentation baseClassRepresentation;
    private List<String> interfacesImplemented;
    private List<InterfaceRepresentation> interfacesImplementedRepresentations;
    private AbstractSyntaxTree.ASTNode nodeRepresentation;
    private List<Statement> statements;
    private String filePath;        //A\B\C\d.txt
    private List<String> outerClassesOrInterfaces;
    String name;

    public ClassRepresentation(String filePath, String className, AbstractSyntaxTree.ASTNode node, List<String> outerClassesOrInterfaces) {
        this.filePath = filePath;
        this.name = className;
        this.interfacesImplemented = new ArrayList<>();
        this.nodeRepresentation = node;
        this.outerClassesOrInterfaces = new ArrayList<>(outerClassesOrInterfaces);
        interfacesImplementedRepresentations = new ArrayList<>();
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

    @Override
    public void checkIfBaseVisible(List<String> visibleClasses, Map<String, List<Representation>> representationsInFiles) throws SemanticException {
        if(baseClass == null)
            return;

        Representation classFound = null;
        for (String visibleClass : visibleClasses) {
            List<String> path = new ArrayList<>(Arrays.asList(visibleClass.split("\\.")));
            if (path.get(path.size() - 1).equals(baseClass)) {
                if ((classFound = findClassByPath(representationsInFiles, path)) != null) {
                    baseClassRepresentation = (ClassRepresentation) classFound;   //save the base representation for further purposes
                    break;
                }
            }
        }
        if(classFound == null)
            throw new SemanticException("Cannot find base class: " + baseClass + " for class " + this.getName() + " in file " + this.filePath);
    }

    @Override
    public void checkIfInterfacesVisible(List<String> visibleClasses, Map<String, List<Representation>> representationsInFiles) throws SemanticException{
        if(interfacesImplemented.isEmpty())
            return;

        for(String inter : interfacesImplemented)
        {
            Representation interfaceFound = null;
            for (String visibleClass : visibleClasses) {
                List<String> path = new ArrayList<>(Arrays.asList(visibleClass.split("\\.")));
                if (path.get(path.size() - 1).equals(inter)) {
                    if ((interfaceFound = findInterfaceByPath(representationsInFiles, path)) != null) {
                        interfacesImplementedRepresentations.add((InterfaceRepresentation) interfaceFound);   //save the base representation for further purposes
                        break;
                    }
                }
            }
            if(interfaceFound == null)
                throw new SemanticException("Cannot find interface: " + inter + " for class " + this.getName() + " in file " + this.filePath);
        }
    }

    private Representation findInterfaceByPath(Map<String,List<Representation>> representationsInFiles, List<String> path) {
        if(path==null)
            return null;
        StringBuilder pathToFind= new StringBuilder(path.get(0)+".txt");
        for (int i = 0; i<path.size(); i++) {
            if (representationsInFiles.get(pathToFind.toString()) != null)  //found potential file in which there can be class sought
            {
                for(Representation rep : representationsInFiles.get(pathToFind.toString()))
                {
                    if(rep.getName().equals(path.get(path.size()-1)) && rep instanceof InterfaceRepresentation) {
                        return rep;
                    }
                }
            }
            if(i<path.size()-1)
                pathToFind.delete(pathToFind.length() - 4, pathToFind.length()).append("\\").append(path.get(i+1)).append(".txt");
        }

        return null;
    }

    private Representation findClassByPath(Map<String,List<Representation>> representationsInFiles, List<String> path) {
        if(path==null)
            return null;
        StringBuilder pathToFind= new StringBuilder(path.get(0)+".txt");
        for (int i = 0; i<path.size(); i++) {
            if (representationsInFiles.get(pathToFind.toString()) != null)  //found potential file in which there can be class sought
            {
                for(Representation rep : representationsInFiles.get(pathToFind.toString()))
                {
                    if(rep.getName().equals(path.get(path.size()-1)) && rep instanceof ClassRepresentation) {
                        return rep;
                    }
                }
            }
            if(i<path.size()-1)
                pathToFind.delete(pathToFind.length() - 4, pathToFind.length()).append("\\").append(path.get(i+1)).append(".txt");
        }

        return null;
    }

    @Override
    public List<String> getBases()
    {
        List<String> result = new ArrayList<>(interfacesImplemented);
        result.add(baseClass);
        return result;
    }

    @Override
    public Representation getBaseByName(String className) {
        if(baseClassRepresentation.getName().equals(className))
            return baseClassRepresentation;
        for(Representation representation : interfacesImplementedRepresentations)
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

}