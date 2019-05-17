package Refactor;

import Parser.AbstractSyntaxTree;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.List;

public class MethodDeclaration extends Statement {

    private List<Pair<String, String>> methodParameterList;
    private String returnType;

    public MethodDeclaration(AbstractSyntaxTree.ASTNode node) {
        this.nodeRep = node;
        prepareStatementInfo();
    }

    @Override
    protected void prepareStatementInfo() {
        this.accessModifier = checkAccessModifier();
        this.statementType = StatementType.MethodDeclaration;
        this.name = checkName();
        this.returnType = checkReturnType();
        this.methodParameterList = checkParameterList();
    }

    private String checkReturnType() {
        int hasAccessModifier = this.accessModifier == AccessModifier.None ? 0 : 1;

        return nodeRep.children.get(hasAccessModifier).identifier;
    }


    private List<Pair<String,String>> checkParameterList() {
        List<Pair<String, String>> parameterList = new ArrayList<>();
        int hasAccessModifier = this.accessModifier == AccessModifier.None ? 2 : 3;

        AbstractSyntaxTree.ASTNode parameters = nodeRep.children.get(hasAccessModifier);

        for(int i = 0; i<parameters.children.size(); i+=2)
        {
            parameterList.add(new Pair<>(parameters.children.get(i).identifier, parameters.children.get(i+1).identifier));
        }
        return parameterList;
    }

    private AccessModifier checkAccessModifier() {
        if (nodeRep.children.get(0).identifier.equals("public"))
            return AccessModifier.Public;
        if(nodeRep.children.get(0).identifier.equals("protected"))
            return AccessModifier.Protected;
        if(nodeRep.children.get(0).identifier.equals("private"))
            return AccessModifier.Private;

        return AccessModifier.None;
    }

    private String checkName() { //must be called after checkAccessModifier
        int hasAccessModifier = this.accessModifier == AccessModifier.None ? 1 : 2;

        return nodeRep.children.get(hasAccessModifier).identifier;
    }

    public List<Pair<String, String>> getMethodParameterList() {
        return methodParameterList;
    }

    public String getReturnType() {
        return returnType;
    }

    @Override
    public String toString() {
        return "MethodDefinition{" +
                "methodParameterList=" + methodParameterList +
                ", returnType='" + returnType + '\'' +
                ", name='" + name + '\'' +
                ", accessModifier=" + accessModifier +
                '}';
    }
}
