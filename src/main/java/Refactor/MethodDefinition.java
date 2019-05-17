package Refactor;

import Parser.AbstractSyntaxTree;
import javafx.util.Pair;

import java.util.List;

public class MethodDefinition extends Statement{
    private List<Pair<String, String>> methodParameterList;
    private String returnType;

    public MethodDefinition(AbstractSyntaxTree.ASTNode node) {
        this.nodeRep = node;
        prepareStatementInfo();
    }

    @Override
    protected void prepareStatementInfo() {

    }

    public List<Pair<String, String>> getMethodParameterList() {
        return methodParameterList;
    }

    public String getReturnType() {
        return returnType;
    }
}
