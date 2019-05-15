package Refactor;

import Exceptions.ParsingException;
import Lexems.Lexem;
import Parser.AbstractSyntaxTree;
import Parser.Parser;
import Scanner.Scanner;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Map;

public class Refactor {
    private final Scanner scanner;
    private final Parser parser;
    private Map<String, AbstractSyntaxTree> ASTs;
    private Map<String, List<Lexem>> lexemsInFiles;
    private Map<String, Map<String,AbstractSyntaxTree.ASTNode>> members;
    private Map<String, List<ClassInheritanceRepresentation>> classesInFiles;

    public enum Refactorings {
        RENAME, PULL_UP, PUSH_DOWN, DELEGATE
    }

    public Refactor(Scanner scanner, Parser parser) {
        this.scanner = scanner;
        this.parser = parser;
    }

    public void parseFiles(List<String> filenames) throws FileNotFoundException, ParsingException {
        for (String file : filenames) {
            scanner.bindFile(new File(file));
            String filePath = file.substring(0, 31);
            ASTs.put(filePath, parser.parseFile());
        }
    }

    public void analyzeInheritances()
    {
        ASTs.forEach((filePath, AST)  ->
        {
            classesInFiles.put(filePath, AST.findClassesAndPutInContext(filePath));
        });
    }
    //        return possibleRefactorings;
    //
    ////        }
    ////            possibleRefactorings.add(Refactorings.DELEGATE);
    ////        {
    ////        if(AST.isDelegable(lineClicked, columnClicked))
    ////        }
    ////            possibleRefactorings.add(Refactorings.PUSH_DOWN);
    ////        {
    ////        if(AST.isPushable(lineClicked, columnClicked))
    //        }
    //            possibleRefactorings.add(Refactorings.PULL_UP);
    //        {
    //        if(AST.isPullable(lineClicked, columnClicked))
    //        }
    //            possibleRefactorings.add(Refactorings.RENAME);
    //        {
    //        if(AST.isRenameable(lineClicked, columnClicked))
    //        List<Refactorings> possibleRefactorings = new ArrayList<>();
    //    {
//    public List<Refactor.Refactor.Refactorings> getPossibleRefactors(int lineClicked, int columnClicked)

//    }

    private void pullUpMembers(String fileName, String sourceClassName, int[] members, String destFileName, String destClassName)
    {

    }
}
