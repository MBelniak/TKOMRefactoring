package Refactor;

import Parser.AbstractSyntaxTree;
import javafx.util.Pair;

import java.util.List;

public class AbstractMethodDeclaration extends Statement {
    private List<Pair<String, String>> methodParameterList;
    private String returnType;

    public AbstractMethodDeclaration(AbstractSyntaxTree.ASTNode node) {
        this.nodeRep = node;
        prepareStatementInfo();
    }

    @Override
    protected void prepareStatementInfo() {
        this.accessModifier = checkAccessModifier();
        this.statementType = StatementType.AbstractClassDefinition;
        this.name = checkName();
    }

    private AccessModifier checkAccessModifier() {
        if(nodeRep.children.get(0).nodeType==AbstractSyntaxTree.ElementType.Identifier)
        {
            if (nodeRep.children.get(0).identifier.equals("PUBLIC"))
                return AccessModifier.Public;
            if(nodeRep.children.get(0).identifier.equals("PROTECTED"))
                return AccessModifier.Protected;
            if(nodeRep.children.get(0).identifier.equals("PRIVATE"))
                return AccessModifier.Private;
        }
        return AccessModifier.None;
    }

    private String checkName() {
        int hasAccessModifier = nodeRep.children.get(0).nodeType == AbstractSyntaxTree.ElementType.Identifier ? 1 : 0;

        return nodeRep.children.get(hasAccessModifier).children.get(0).identifier;
    }

    public List<Pair<String, String>> getMethodParameterList() {
        return methodParameterList;
    }

    public String getReturnType() {
        return returnType;
    }
}
