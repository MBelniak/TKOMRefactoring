package model.refactor;

import model.exceptions.SemanticException;
import model.parser.AbstractSyntaxTree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class InterfaceRepresentation implements Representation{
    private List<String> baseInterfaces;
    private List<InterfaceRepresentation> baseInterfacesRepresentations;
    private AbstractSyntaxTree.ASTNode nodeRepresentation;
    private List<Statement> statements;
    private String filePath;
    private List<String> outerClassesOrInterfaces;
    String name;


    public InterfaceRepresentation(String filePath, String className, AbstractSyntaxTree.ASTNode node, List<String> outerClassesOrInterfaces) {
        this.filePath = filePath;
        this.name = className;
        this.baseInterfaces = new ArrayList<>();
        this.nodeRepresentation = node;
        this.outerClassesOrInterfaces = new ArrayList<>(outerClassesOrInterfaces);
        this.baseInterfacesRepresentations = new ArrayList<>();
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
            result.delete(result.length()-2, result.length());
            result.append("]");
        }
        result.append(", interfaceName='")
                .append(name)
                .append('\'')
                .append(", baseInterfaces=")
                .append(baseInterfaces)
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

    }

    @Override
    public void checkIfInterfacesVisible(List<String> visibleClasses, Map<String, List<Representation>> representationsInFiles) throws SemanticException {
        if(baseInterfaces.isEmpty())
            return;

        for(String inter : baseInterfaces)
        {
            Representation interfaceFound = null;
            for (int impor = 0; impor < visibleClasses.size(); impor++) {
                List<String> path = new ArrayList<>(Arrays.asList(visibleClasses.get(impor).split("\\.")));
                if (path.get(path.size() - 1).equals(inter)) {
                    if ((interfaceFound = findInterfaceByPath(representationsInFiles, path)) != null) {
                        baseInterfacesRepresentations.add((InterfaceRepresentation) interfaceFound);   //save the base representation for further purposes
                        break;
                    }
                }
            }
            if(interfaceFound == null)
                throw new SemanticException("Cannot find interface: " + inter + " for interface " + this.getName() + " in file " + this.filePath);
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

    @Override
    public List<String> getBases() {
        return new ArrayList<>(baseInterfaces);
    }

    @Override
    public Representation getBaseByName(String className) {
        for(Representation representation : baseInterfacesRepresentations)
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