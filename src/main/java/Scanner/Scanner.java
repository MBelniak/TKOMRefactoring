package Scanner;

import FilesManagement.FileSource;
import Lexems.Lexem;
import Lexems.LexemFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.AbstractMap;
import java.util.Map;

public class Scanner {
    private static final Map<String, Lexem.LexemType> keywords = Map.ofEntries(
      new AbstractMap.SimpleEntry<>("class", Lexem.LexemType.CLASS),
            new AbstractMap.SimpleEntry<>("interface", Lexem.LexemType.INTERFACE),
            new AbstractMap.SimpleEntry<>("public", Lexem.LexemType.PUBLIC),
            new AbstractMap.SimpleEntry<>("private", Lexem.LexemType.PRIVATE),
            new AbstractMap.SimpleEntry<>("protected", Lexem.LexemType.PROTECTED),
            new AbstractMap.SimpleEntry<>("package", Lexem.LexemType.PACKAGE),
            new AbstractMap.SimpleEntry<>("import", Lexem.LexemType.IMPORT),
            new AbstractMap.SimpleEntry<>("extends", Lexem.LexemType.EXTENDS),
            new AbstractMap.SimpleEntry<>("implements", Lexem.LexemType.IMPLEMENTS),
            new AbstractMap.SimpleEntry<>("abstract", Lexem.LexemType.ABSTRACT),
            new AbstractMap.SimpleEntry<>("this", Lexem.LexemType.THIS),
            new AbstractMap.SimpleEntry<>("super", Lexem.LexemType.SUPER),
            new AbstractMap.SimpleEntry<>("new", Lexem.LexemType.NEW),
            new AbstractMap.SimpleEntry<>("void", Lexem.LexemType.VOID),
            new AbstractMap.SimpleEntry<>("String", Lexem.LexemType.STRING),
            new AbstractMap.SimpleEntry<>("main", Lexem.LexemType.MAIN),
            new AbstractMap.SimpleEntry<>("int", Lexem.LexemType.INT),
            new AbstractMap.SimpleEntry<>("static", Lexem.LexemType.STATIC),
            new AbstractMap.SimpleEntry<>("return", Lexem.LexemType.RETURN),
            new AbstractMap.SimpleEntry<>("default", Lexem.LexemType.DEFAULT)
    );

    private static final int MAX_IDENT_LENGTH = 1000;

    private LexemFactory lexemFactory;
    private FileSource source;
    private int currentChar;

    private boolean isWhiteSpace(int Ch)
    {
        return Ch==' ' || Ch=='\t';
    }

    private boolean isAllowedAlpha(int Ch)
    {
        return (Ch >= 'A' && Ch <= 'Z') || (Ch>= 'a' && Ch <= 'z') || Ch == '$' || Ch=='_';
    }
    private boolean isAllowedAlphaOrNumeric(int Ch)
    {
        return isAllowedAlpha(Ch) || isDigit(Ch);
    }

    private boolean isDigit(int Ch)
    {
        return Ch >= '0' && Ch <= '9';
    }

    public int bindFile(File source) throws FileNotFoundException {
        this.source = new FileSource(source);
        lexemFactory = new LexemFactory(this.source);
        currentChar = this.source.nextChar();
        return 0;
    }

    //MAIN LOOP TO FIND A LEXEM
    public Lexem getNextLexem()
    {
        if(source==null)
        {
            System.out.println("Give me the input file");
            return null;
        }
        do {
            while (isWhiteSpace(currentChar)||currentChar=='\n') {
                currentChar = source.nextChar();    //skipping white spaces
            }
            if(currentChar < 0)
                return lexemFactory.getLexem(Lexem.LexemType.EOF);
            if (currentChar == '/') {
                if ((currentChar = source.nextChar()) == '/')              //LINE comment
                {
                    do
                    {
                        currentChar = source.nextChar();
                        if(currentChar < 0)
                            return lexemFactory.getLexem(Lexem.LexemType.EOF);
                    }
                    while (currentChar != '\n');
                }
                else if(currentChar == '*')             //comment block
                {
                    while(true)
                    {
                        if((currentChar = source.nextChar()) < 0)
                            return lexemFactory.getLexem(Lexem.LexemType.EOF);
                        if(currentChar=='*')
                        {
                            if((currentChar = source.nextChar())=='/')
                            {
                                currentChar = source.nextChar();
                                break;
                            }
                        }
                    }
                }
                else
                {
                    return lexemFactory.getLexem(Lexem.LexemType.ILLEGAL_CHAR,"/");     //we don't recognize dividing
                }
            }
        }
        while(isWhiteSpace(currentChar)||currentChar=='\n');

        //start generating the lexem
        if(isAllowedAlpha(currentChar))        //keyword or ident maybe
        {
            int length=0;
            StringBuilder tokenBuilding = new StringBuilder();
            do
            {
                if(length<MAX_IDENT_LENGTH)
                {
                    length++;
                    tokenBuilding.append((char) currentChar);
                }
                else {
                    currentChar = source.nextChar();
                    return lexemFactory.getLexem(Lexem.LexemType.IDENTIFIER, length, tokenBuilding.toString());
                }

                currentChar = source.nextChar();
            } while(isAllowedAlphaOrNumeric(currentChar));

            if(keywords.get(tokenBuilding.toString())!=null)
                return lexemFactory.getLexem(keywords.get(tokenBuilding.toString()), length, null);
            else
                return lexemFactory.getLexem(Lexem.LexemType.IDENTIFIER, length, tokenBuilding.toString());
        }
        if(isDigit(currentChar)||currentChar=='-')            //numbers
        {
            int length=0;
            StringBuilder tokenBuilding = new StringBuilder();
            do
            {
                if(length<MAX_IDENT_LENGTH)
                {
                    tokenBuilding.append((char)currentChar);
                    length++;
                }
                else {
                    currentChar = source.nextChar();
                    return lexemFactory.getLexem(Lexem.LexemType.NUMBER, length, tokenBuilding.toString());
                }

                currentChar = source.nextChar();
            } while(isDigit(currentChar));
            if(isAllowedAlpha(currentChar))
            {
                do
                {
                    if(length<MAX_IDENT_LENGTH)
                    {
                        tokenBuilding.append((char)currentChar);
                        length++;
                    }
                    else {
                        currentChar = source.nextChar();
                        return lexemFactory.getLexem(Lexem.LexemType.ILLEGAL_IDENT, length, tokenBuilding.toString());
                    }

                    currentChar = source.nextChar();
                } while(isAllowedAlphaOrNumeric(currentChar));
                return lexemFactory.getLexem(Lexem.LexemType.ILLEGAL_IDENT, length, tokenBuilding.toString());
            }
            else
                return lexemFactory.getLexem(Lexem.LexemType.NUMBER, length, tokenBuilding.toString());
        }


        switch(currentChar)
        {
            case '.':
                {
                currentChar = source.nextChar();
                return lexemFactory.getLexem(Lexem.LexemType.DOT);
                }
            case '(':
                {
                currentChar = source.nextChar();
                return lexemFactory.getLexem(Lexem.LexemType.LEFT_BRACKET);
                }
            case ')':
                {
                currentChar = source.nextChar();
                return lexemFactory.getLexem(Lexem.LexemType.RIGHT_BRACKET);
                }
            case '{':
                {
                currentChar = source.nextChar();
                return lexemFactory.getLexem(Lexem.LexemType.LEFT_CURLY_BRACKET);
                }
            case '}':
                {
                currentChar = source.nextChar();
                return lexemFactory.getLexem(Lexem.LexemType.RIGHT_CURLY_BRACKET);
                }
            case '[':
            {
                currentChar = source.nextChar();
                return lexemFactory.getLexem(Lexem.LexemType.LEFT_SQUARE_BRACKET);
            }
            case ']':
            {
                currentChar = source.nextChar();
                return lexemFactory.getLexem(Lexem.LexemType.RIGHT_SQUARE_BRACKET);
            }
            case ';':
            {
                currentChar = source.nextChar();
                return lexemFactory.getLexem(Lexem.LexemType.SEMICOLON);
            }
            case ',':
            {
                currentChar = source.nextChar();
                return lexemFactory.getLexem(Lexem.LexemType.COMMA);
            }
            case '=':
            {
                currentChar = source.nextChar();
                return lexemFactory.getLexem(Lexem.LexemType.ASSIGNMENT);
            }
            case '*':
            {
                currentChar = source.nextChar();
                return lexemFactory.getLexem(Lexem.LexemType.ASTERIX);
            }
        }
        String c = String.valueOf((char)currentChar);
        currentChar = source.nextChar();
        return lexemFactory.getLexem(Lexem.LexemType.ILLEGAL_CHAR, c);
    }
}
