package model.refactor;

import model.parser.AbstractSyntaxTree;
import javafx.util.Pair;

import java.util.ArrayList;
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
        this.accessModifier = checkAccessModifier();
        this.statementType = StatementType.MethodDefinition;
        this.name = checkName();
        this.returnType = checkReturnType();
        this.methodParameterList = checkParameterList();
        this.startsAtLine = nodeRep.parent.startsAtLine;
        this.startsAtColumn = nodeRep.parent.startsAtColumn;
        this.endsAtLine = nodeRep.parent.endsAtLine;
        this.endsAtColumn = nodeRep.parent.endsAtColumn;
    }

    @Override
    public String getExtraInfo() {
        StringBuilder result = new StringBuilder();
        result.append("(");
        for(int i = 0; i<methodParameterList.size(); i++)
        {
            result.append(methodParameterList.get(i).getKey()).append(" ").append(methodParameterList.get(i).getValue());
            if(i!=methodParameterList.size()-1)
                result.append(", ");
        }
        result.append(") returns ").append(returnType);
        return result.toString();
    }

    @Override
    public String getStatementSignature() {
        StringBuilder parameters = new StringBuilder();
        for (int i = 0; i < methodParameterList.size(); i++)
        {
            parameters.append(methodParameterList.get(i).getKey());
            if(i != methodParameterList.size()-1)
                parameters.append(", ");
        }
        return this.name + "(" + parameters.toString() + ")";
    }

    @Override
    public String getMethodParameterList() {
        StringBuilder result = new StringBuilder();
        result.append("(");
        for(int i = 0; i<methodParameterList.size(); i++)
        {
            result.append(methodParameterList.get(i).getKey()).append(" ").append(methodParameterList.get(i).getValue());
            if(i!=methodParameterList.size()-1)
                result.append(", ");
        }
        result.append(")");
        return result.toString();
    }

    @Override
    List<String> getParametersNames() {
        List<String> result = new ArrayList<>();
        methodParameterList.forEach(param -> result.add(param.getValue()));
        return result;
    }

    @Override
    public String getCategory() {
        return "Method";
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
        if(nodeRep.children.get(0).identifier.equals("default"))
            return AccessModifier.Default;

        return AccessModifier.None;
    }

    private String checkName() { //must be called after checkAccessModifier
        int hasAccessModifier = this.accessModifier == AccessModifier.None ? 1 : 2;

        return nodeRep.children.get(hasAccessModifier).identifier;
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
                ", startsAtLine=" + startsAtLine +
                ", startsAtColumn=" + startsAtColumn +
                ", endsAtLine=" + endsAtLine +
                ", endsAtColumn=" + endsAtColumn +
                '}';
    }
}
