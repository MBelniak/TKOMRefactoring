//package Refactor;
//
//import Lexems.Lexem;
//import Parser.AbstractSyntaxTree;
//import Parser.Parser;
//import Scanner.Scanner;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map;
//
//public class Refactor {
//    private final Scanner scanner;
//    private final Parser parser;
//    private final AbstractSyntaxTree AST;
//    private Map<String, List<Lexem>> lexemsInFiles;
//
//    public enum Refactorings {
//        RENAME, PULL_UP, PUSH_DOWN, DELEGATE
//    }
//
//    public Refactor(Scanner scanner, Parser parser) {
//        this.scanner = scanner;
//        this.parser = parser;
//        this.AST = parser.getAST();
//    }
//
//    public List<Refactor.Refactor.Refactorings> getPossibleRefactors(int lineClicked, int columnClicked)
//    {
//        List<Refactorings> possibleRefactorings = new ArrayList<>();
//        if(AST.isRenameable(lineClicked, columnClicked))
//        {
//            possibleRefactorings.add(Refactorings.RENAME);
//        }
//        if(AST.isPullable(lineClicked, columnClicked))
//        {
//            possibleRefactorings.add(Refactorings.PULL_UP);
//        }
//        if(AST.isPushable(lineClicked, columnClicked))
//        {
//            possibleRefactorings.add(Refactorings.PUSH_DOWN);
//        }
//        if(AST.isDelegable(lineClicked, columnClicked))
//        {
//            possibleRefactorings.add(Refactorings.DELEGATE);
//        }
//
//        return possibleRefactorings;
//    }
//}
