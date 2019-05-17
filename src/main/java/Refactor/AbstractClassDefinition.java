package Refactor;

import Parser.AbstractSyntaxTree;

public class AbstractClassDefinition extends Statement {

    public AbstractClassDefinition(AbstractSyntaxTree.ASTNode nodeType) {
        this.nodeRep = nodeType;
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
}
