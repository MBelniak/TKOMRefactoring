package Refactor;

import Parser.AbstractSyntaxTree;

import java.util.List;

public class ClassDefinition extends Statement {
    private List<String> extendsList;
    private String interfaceImplemented;

    public ClassDefinition(AbstractSyntaxTree.ASTNode node) {
        this.nodeRep = node;
        prepareStatementInfo();
    }

    @Override
    protected void prepareStatementInfo() {

    }

    public List<String> getExtendsList() {
        return extendsList;
    }

    public String getInterfaceImplemented() {
        return interfaceImplemented;
    }
}
