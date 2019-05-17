package Refactor;

import Parser.AbstractSyntaxTree;

public class FieldDeclaration extends Statement {
    private String type;

    public FieldDeclaration(AbstractSyntaxTree.ASTNode node) {
        this.nodeRep = node;
        prepareStatementInfo();
    }

    @Override
    protected void prepareStatementInfo() {

    }
    public String getType() {
        return type;
    }
}
