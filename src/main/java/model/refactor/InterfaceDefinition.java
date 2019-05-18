package model.refactor;

import model.parser.AbstractSyntaxTree;

public class InterfaceDefinition extends Statement {

    public InterfaceDefinition(AbstractSyntaxTree.ASTNode node) {
        this.nodeRep = node;
        prepareStatementInfo();
    }

    @Override
    protected void prepareStatementInfo() {
        this.accessModifier = checkAccessModifier();
        this.statementType = StatementType.InterfaceDefinition;
        this.name = checkName();
        this.startsAtLine = nodeRep.parent.startsAtLine;
        this.startsAtColumn = nodeRep.parent.startsAtColumn;
        this.endsAtLine = nodeRep.parent.endsAtLine;
        this.endsAtColumn = nodeRep.parent.endsAtColumn;
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
        return "InterfaceDefinition{" +
                "name='" + name + '\'' +
                ", accessModifier=" + accessModifier +
                ", startsAtLine=" + startsAtLine +
                ", startsAtColumn=" + startsAtColumn +
                ", endsAtLine=" + endsAtLine +
                ", endsAtColumn=" + endsAtColumn +
                '}';
    }
}
