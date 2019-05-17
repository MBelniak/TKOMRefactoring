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
        if(nodeRep.children.get(0).children.get(0).nodeType==AbstractSyntaxTree.ElementType.Identifier)
        {
            if (nodeRep.children.get(0).children.get(0).identifier.equals("public"))
                return AccessModifier.Public;
            if(nodeRep.children.get(0).children.get(0).identifier.equals("protected"))
                return AccessModifier.Protected;
            if(nodeRep.children.get(0).children.get(0).identifier.equals("private"))
                return AccessModifier.Private;
        }
        return AccessModifier.None;
    }

    private String checkName() { //must be called after checkAccessModifier
        int hasAccessModifier = this.accessModifier == AccessModifier.None ? 0 : 1;

        return nodeRep.children.get(0).children.get(hasAccessModifier).identifier;
    }

    @Override
    public String toString() {
        return "AbstractClassDefinition{" +
                "name='" + name + '\'' +
                ", accessModifier=" + accessModifier +
                '}';
    }
}
