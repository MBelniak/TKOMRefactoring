package model.lexems;

import model.filesManagement.FileSource;


public class LexemFactory {
    private FileSource fileSource;

    public LexemFactory(FileSource fileSource) {
        this.fileSource = fileSource;
    }

    public Lexem getLexem(Lexem.LexemType lexemType)
    {
        return getLexem(lexemType, 1, null);
    }
    public Lexem getLexem(Lexem.LexemType lexemType, String value)
    {
        return getLexem(lexemType, 1, value);
    }
    public Lexem getLexem(Lexem.LexemType lexemType, int lexemLength, String value)
    {
        if(lexemLength > fileSource.getColumnNumber())
            return new Lexem(lexemType, fileSource.getLineNumber(), 0, value);
        return new Lexem(lexemType, fileSource.getLineNumber(), fileSource.getColumnNumber()-lexemLength, value);
    }
}
