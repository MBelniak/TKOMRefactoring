package Parser;

import Scanner.Lexem;



class AbstractSyntaxTree {



    public enum ElementType {
        PublicClassDefinition, PackageDeclaration, ImportDeclaration, AbstractClassDefinition, ClassDefinition,
    }
    public void clear() {
    }

    public void startFile(Lexem currentToken) {
    }

    public void addIdentifierToList(Lexem currentToken) {
    }

    public void startElement(ElementType elementType, Lexem currentToken) {
    }

    public void endElement(ElementType publicClassDefinition) {
    }
}

