package model.refactor;

import model.parser.AbstractSyntaxTree;

public class FieldDeclaration extends Statement {
    private String type;

    public FieldDeclaration(AbstractSyntaxTree.ASTNode node) {
        this.nodeRep = node;
        prepareStatementInfo();
    }

    @Override
    protected void prepareStatementInfo() {
        this.accessModifier = checkAccessModifier();
        this.statementType = StatementType.FieldDeclaration;
        this.name = checkName();
        this.type = checkType();
        this.startsAtLine = nodeRep.parent.startsAtLine;
        this.startsAtColumn = nodeRep.parent.startsAtColumn;
        this.endsAtLine = nodeRep.parent.endsAtLine;
        this.endsAtColumn = nodeRep.parent.endsAtColumn;
    }

    @Override
    public String getExtraInfo() {
        return " (" + type + ")";
    }

    @Override
    public String getStatementSignature() {
        return this.name;
    }

    @Override
    public String getCategory() {
        return "Field";
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

    private String checkType() {
        int hasAccessModifier = this.accessModifier == AccessModifier.None ? 0 : 1;

        return nodeRep.children.get(hasAccessModifier).identifier;
    }

    public String getType() {
        return type;
    }

    @Override
    public String toString() {
        return "FieldDeclaration{" +
                "type='" + type + '\'' +
                ", name='" + name + '\'' +
                ", accessModifier=" + accessModifier +
                ", startsAtLine=" + startsAtLine +
                ", startsAtColumn=" + startsAtColumn +
                ", endsAtLine=" + endsAtLine +
                ", endsAtColumn=" + endsAtColumn +
                '}';
    }
}

