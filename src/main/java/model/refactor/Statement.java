package model.refactor;

import model.parser.AbstractSyntaxTree;

public abstract class Statement {
    public enum StatementType{AbstractClassDefinition, AbstractMethodDeclaration,
        ClassDefinition, FieldDeclaration, FieldDefinition, MethodDeclaration, InterfaceDefinition, MethodDefinition}
    AbstractSyntaxTree.ASTNode nodeRep;
    public enum AccessModifier{Public, Protected, Private, None, Default} //Default for interfaces
    StatementType statementType;
    protected String name;
    AccessModifier accessModifier;
    int startsAtLine;
    int startsAtColumn;
    int endsAtLine;
    int endsAtColumn;


    public StatementType getStatementType()
    {
        return statementType;
    }

    public String getName() {
        return name;
    }

    public String getAccessModifier() {
        if(accessModifier==AccessModifier.Private
                || accessModifier==AccessModifier.Protected
                || accessModifier==AccessModifier.Public
                || accessModifier==AccessModifier.Default)
            return accessModifier.toString();
        return "";
    }

    protected abstract void prepareStatementInfo();

    public int getStartsAtLine() {
        return startsAtLine;
    }

    public int getStartsAtColumn() {
        return startsAtColumn;
    }

    public int getEndsAtLine() {
        return endsAtLine;
    }

    public int getEndsAtColumn() {
        return endsAtColumn;
    }

    public abstract String getExtraInfo();

    public void setStartsAtLine(int startsAtLine) {
        this.startsAtLine = startsAtLine;
    }

    public void setStartsAtColumn(int startsAtColumn) {
        this.startsAtColumn = startsAtColumn;
    }

    public void setEndsAtLine(int endsAtLine) {
        this.endsAtLine = endsAtLine;
    }

    public void setEndsAtColumn(int endsAtColumn) {
        this.endsAtColumn = endsAtColumn;
    }

}

class StatementFactory
{
    static Statement getStatement(AbstractSyntaxTree.ASTNode node)
    {
        switch(node.nodeType) {
            case AbstractClassDefinition:
                return new AbstractClassDefinition(node);

            case AbstractMethodDeclaration:
                return new AbstractMethodDeclaration(node);

            case ClassDefinition:
                return new ClassDefinition(node);

            case InterfaceDefinition:
                return new InterfaceDefinition(node);
            case PrimitiveFieldDeclaration:
                return new FieldDeclaration(node);

            case ObjectFieldDeclaration:
                return new FieldDeclaration(node);

            case MethodDefinition:
                return new MethodDefinition(node);

            case PrimitiveFieldDefinition:
                return new FieldDefinition(node);

            case MethodDeclaration:
                return new MethodDeclaration(node);
            default:
                return null;
        }
    }
}
