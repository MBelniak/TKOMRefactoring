package Refactor;

import Parser.AbstractSyntaxTree;

import java.util.List;

public interface Representation {
    List<String> getOuterClassesOrInterfaces();
    String getName();
    List<Statement> getStatements();
    AbstractSyntaxTree.ASTNode getNodeRep();
}
