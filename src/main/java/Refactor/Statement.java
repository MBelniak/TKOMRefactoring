package Refactor;

import Parser.AbstractSyntaxTree;

public abstract class Statement {
    protected AbstractSyntaxTree.ASTNode nodeRep;
    public enum StatementType{AbstractClassDefinition, AbstractMethodDeclaration, ClassDefinition, FieldDeclaration, MethodDefinition}
    public enum AccessModifier{Public, Protected, Private, None}
    protected StatementType statementType;
    protected String name;
    protected AccessModifier accessModifier;

    public void getStatementType()
    {

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
    public static Statement getStatement(AbstractSyntaxTree.ASTNode node)
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

            default:
                return null;
        }
    }
}
