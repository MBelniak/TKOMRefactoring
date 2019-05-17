package Refactor;

import Exceptions.ParsingException;
import Parser.AbstractSyntaxTree;
import Parser.Parser;
import Scanner.Scanner;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Refactor {
    private final Scanner scanner;
    private final Parser parser;
    private Map<String, AbstractSyntaxTree> ASTs;
    private Map<String, Map<ClassInheritanceRepresentation, List<Statement>>> statementsInClasses;
    private Map<String, Map<InterfaceInheritanceRepresentation, List<Statement>>> statementsInInterfaces;
    private Map<String, List<ClassInheritanceRepresentation>> classesInFiles;
    private Map<String, List<InterfaceInheritanceRepresentation>> interfacesInFiles;

    public void analyze() {
        analyzeClassesAndInterfaces();
        analyzeStatements();
    }

    public enum Refactorings {
        RENAME, PULL_UP, PUSH_DOWN, DELEGATE
    }

    public Refactor(Scanner scanner, Parser parser) {
        this.scanner = scanner;
        this.parser = parser;
        this.ASTs = new HashMap<>();
        this.statementsInClasses = new HashMap<>();
        this.statementsInInterfaces = new HashMap<>();
        this.classesInFiles = new HashMap<>();
        this.interfacesInFiles = new HashMap<>();
    }

    public void parseFiles(List<String> filenames) throws FileNotFoundException{
        for (String file : filenames) {
            scanner.bindFile(new File(file));
            String filePath = file.substring(32, file.length());
            try {
                ASTs.put(filePath, parser.parseFile());
            } catch (ParsingException e) {
                System.out.println(e.getErrorMessage() + " Line: " + e.getLine() + " Column: " + e.getColumn()
                        + " in file: " + file);
                System.exit(0);
            }
        }
    }

    private void analyzeClassesAndInterfaces()
    {
        ASTs.forEach((filePath, AST)  ->{
                classesInFiles.put(filePath, AST.findClassesAndPutInContext(filePath));
                interfacesInFiles.put(filePath, AST.findInterfacesAndPutInContext(filePath));
        });

        classesInFiles.forEach((filePath, classInFile)->
                System.out.println(filePath + ": " + classInFile.toString()));
        interfacesInFiles.forEach((filePath, interfaceInFiles) ->
                System.out.println(filePath + ": " + interfaceInFiles.toString()));
    }

    private void analyzeStatements()
    {
        classesInFiles.forEach((filePath, classReps) ->
        {
            Map<ClassInheritanceRepresentation, List<Statement>> newMap = new HashMap<>();
            classReps.forEach(classRep ->
                    newMap.put(classRep, classRep.listAllMoveableStatements()));

            statementsInClasses.put(filePath, newMap);
        });

        interfacesInFiles.forEach((filePath, interfaceReps) ->
        {
            Map<InterfaceInheritanceRepresentation, List<Statement>> newMap = new HashMap<>();
            interfaceReps.forEach(interfaceRep ->
                    newMap.put(interfaceRep, interfaceRep.listAllMoveableStatements()));

            statementsInInterfaces.put(filePath, newMap);
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
