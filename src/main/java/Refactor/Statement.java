package Refactor;

import Parser.AbstractSyntaxTree;

public abstract class Statement {
    AbstractSyntaxTree.ASTNode nodeRep;
    public enum StatementType{AbstractClassDefinition, AbstractMethodDeclaration,
        ClassDefinition, FieldDeclaration, FieldDefinition, MethodDeclaration, MethodDefinition}
    public enum AccessModifier{Public, Protected, Private, None, Default} //Default for interfaces
    StatementType statementType;
    protected String name;
    AccessModifier accessModifier;

    public StatementType getStatementType()
    {
        return statementType;
    }

    public String getName() {
        return name;
    }

    public AccessModifier getAccessModifier() {
        return accessModifier;
    }

    protected abstract void prepareStatementInfo();

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
