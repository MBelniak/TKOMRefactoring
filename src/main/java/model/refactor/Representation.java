package model.refactor;

import model.exceptions.SemanticException;
import model.parser.AbstractSyntaxTree;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface Representation {
    List<String> getOuterClassesOrInterfaces();

    String getName();

    List<Statement> getStatements();

    AbstractSyntaxTree.ASTNode getNodeRep();

    void checkIfBaseVisible(Map<String, List<Representation>> representationsInFiles) throws SemanticException;

    void checkIfInterfacesVisible(Map<String, List<Representation>> classesAndInterfacesInFiles) throws SemanticException;

    List<String> getBases();

    Representation getBaseByName(String destClassOrInterName);

    String getFilePath();

    String getAccessModifier();

    void setImports(List<String> imports);

    boolean checkIfSameStatementExists(Statement statement, int count);

    Representation getSubByName(String destClass);

    List<String> getSubclassesOrSubinterfaces();
}
