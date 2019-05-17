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
    private Map<String, List<ClassInheritanceRepresentation>> classesInFiles;
    private Map<String, List<InterfaceInheritanceRepresentation>> interfacesInFiles;

    public enum Refactorings {
        RENAME, PULL_UP, PUSH_DOWN, DELEGATE;
    }
    public Refactor(Scanner scanner, Parser parser) {
        this.scanner = scanner;
        this.parser = parser;
        this.ASTs = new HashMap<>();
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

    public void analyze() {
        analyzeClassesAndInterfaces();
        classesInFiles.forEach((file, classReps) ->
        {
           StringBuilder output = new StringBuilder();
           output.append(file).append(": \n");
           classReps.forEach(classRep ->
           {
               output.append("\t").append(classRep.getClassName()).append(": \n");
               if(!classRep.getStatements().isEmpty())
                   classRep.getStatements().forEach(statement -> output.append("\t\t").append(statement.toString()).append("\n"));
           });

            System.out.println(output);
        });

        interfacesInFiles.forEach((file, interfaceReps) ->
        {
            StringBuilder output = new StringBuilder();
            output.append(file).append(": \n");
            interfaceReps.forEach(interfaceRep ->
            {
                output.append("\t").append(interfaceRep.getInterfaceName()).append(": \n");
                if(!interfaceRep.getStatements().isEmpty())
                    interfaceRep.getStatements().forEach(statement -> output.append("\t\t").append(statement.toString()).append("\n"));
            });
            System.out.println(output);
        });
    }

    private void analyzeClassesAndInterfaces()
    {
        ASTs.forEach((filePath, AST)  ->{
                classesInFiles.put(filePath, AST.findClassesAndPutInContext(filePath));
                interfacesInFiles.put(filePath, AST.findInterfacesAndPutInContext(filePath));
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
