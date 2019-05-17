package Refactor;

import Exceptions.ParsingException;
import Exceptions.SemanticException;
import Parser.AbstractSyntaxTree;
import Parser.Parser;
import Scanner.Scanner;
import javafx.util.Pair;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toCollection;

public class Refactor {
    private final Scanner scanner;
    private final Parser parser;
    private Map<String, AbstractSyntaxTree> ASTs;
    private Map<String, List<ClassRepresentation>> classesInFiles;
    private Map<String, List<InterfaceRepresentation>> interfacesInFiles;

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
        writeClassesAndInterfaces();


    }

    private void writeClassesAndInterfaces() {
        classesInFiles.forEach((file, classReps) ->
        {
            StringBuilder output = new StringBuilder();
            output.append(file).append(": \n");
            classReps.forEach(classRep ->
            {
                output.append("\n\t").append(classRep.toString()).append(": \n");
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
                output.append("\n\t").append(interfaceRep.toString()).append(": \n");
                if(!interfaceRep.getStatements().isEmpty())
                    interfaceRep.getStatements().forEach(statement -> output.append("\t\t").append(statement.toString()).append("\n"));
            });
            System.out.println(output);
        });
    }

    private void analyzeClassesAndInterfaces()
    {
        ASTs.forEach((filePath, AST)  ->{
            Pair<List<ClassRepresentation>, List<InterfaceRepresentation>> foundCI = AST.findClassesAndInterfacesAndPutInContext(filePath);
                classesInFiles.put(filePath, foundCI.getKey());
                interfacesInFiles.put(filePath, foundCI.getValue());
            try {
                checkForDuplicates(filePath);
            } catch (SemanticException e) {
                System.out.println(e.getMessage());
                System.exit(0);
            }
        });
    }

    /*
        Basic check of duplication. Throws SemanticException if class or interface with the same name
        is declared in overlapping scope.
     */
    private void checkForDuplicates(String filePath) throws SemanticException {
        List<Representation> classesAndInterfaces = new ArrayList<Representation>(classesInFiles.get(filePath));
        classesAndInterfaces.addAll(interfacesInFiles.get(filePath));

        for(int i = 0; i<classesAndInterfaces.size(); i++) {
            Representation representation = classesAndInterfaces.get(i);

            if (representation.getOuterClassesOrInterfaces()
                    .stream()
                    .filter(elem -> elem.equals(representation.getName())).collect(Collectors.toList()).size()>0) {
                throw new SemanticException("Duplicated class or interface name: " + representation.getName());
            }
            for (int k = 0; k < classesAndInterfaces.size(); k++) {
                Representation representation2 = classesAndInterfaces.get(k);
                if (representation != representation2
                        && representation.getName().equals(representation2.getName())
                        && representation.getOuterClassesOrInterfaces().equals(representation2.getOuterClassesOrInterfaces()))
                {
                    throw new SemanticException("Duplicated class name: " + representation.getName());
                }
            }
        }
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

    private void pullUpMembers(String fileName, String sourceClassOrInterName, int[] members, String destFileName, String destClassOrInterName)
    {

    }
}


