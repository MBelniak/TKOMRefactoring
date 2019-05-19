package model.refactor;

import model.exceptions.SemanticException;
import model.parser.AbstractSyntaxTree;

import java.util.List;
import java.util.Map;

public interface Representation {
    List<String> getOuterClassesOrInterfaces();

    String getName();

    List<Statement> getStatements();

    AbstractSyntaxTree.ASTNode getNodeRep();

    void checkIfBaseVisible(List<String> visibleClasses, Map<String, List<Representation>> representationsInFiles) throws SemanticException;

    void checkIfInterfacesVisible(List<String> strings, Map<String, List<Representation>> classesAndInterfacesInFiles) throws SemanticException;
}
