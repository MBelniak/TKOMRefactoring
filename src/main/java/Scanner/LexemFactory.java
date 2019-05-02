package Scanner;

import FilesManagement.FileSource;


class LexemFactory {
    private FileSource fileSource;

    LexemFactory(FileSource fileSource) {
        this.fileSource = fileSource;
    }

    Lexem getLexem(Lexem.LexemType lexemType)
    {
        return getLexem(lexemType, 1, null);
    }
    Lexem getLexem(Lexem.LexemType lexemType, String value)
    {
        return getLexem(lexemType, 1, value);
    }
    Lexem getLexem(Lexem.LexemType lexemType, int lexemLength, String value)
    {
        if(lexemLength > fileSource.getColumnNumber())
            return new Lexem(lexemType, fileSource.getLineNumber(), 0, value);
        return new Lexem(lexemType, fileSource.getLineNumber(), fileSource.getColumnNumber()-lexemLength, value);
    }
}
