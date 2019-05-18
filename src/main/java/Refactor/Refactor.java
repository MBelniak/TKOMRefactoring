package Refactor;

import Exceptions.ParsingException;
import Exceptions.SemanticException;
import Parser.AbstractSyntaxTree;
import Parser.Parser;
import Scanner.Scanner;
import javafx.util.Pair;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Refactor {
    private final Scanner scanner;
    private final Parser parser;
    private Map<String, AbstractSyntaxTree> ASTs;
    private Map<String, List<Representation>> classesAndInterfacesInFiles;

    public enum Refactorings {
        RENAME, PULL_UP, PUSH_DOWN, DELEGATE;
    }
    public Refactor(Scanner scanner, Parser parser) {
        this.scanner = scanner;
        this.parser = parser;
        this.ASTs = new HashMap<>();
        this.classesAndInterfacesInFiles = new HashMap<>();
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
        //writeClassesAndInterfaces();
        List<String> source = new ArrayList<>();
        source.add("Class1");
        List<String> dest = new ArrayList<>();
        dest.add("Base");
        List<Integer> members = new ArrayList<>();
        members.add(0);
        pullUpMembers("Class1.txt", source, members, "Class1.txt", dest);
    }

    private void analyzeClassesAndInterfaces()
    {
        ASTs.forEach((filePath, AST)  ->{
            List<Representation> foundCI = AST.findClassesAndInterfacesAndPutInContext(filePath);
                classesAndInterfacesInFiles.put(filePath, foundCI);
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
        is declared in overlapping scope in the certain file.
     */

    private void checkForDuplicates(String filePath) throws SemanticException {

        List<Representation> concernedClassesAndInt = classesAndInterfacesInFiles.get(filePath);
        for(int i = 0; i<concernedClassesAndInt.size(); i++) {
            Representation representation = concernedClassesAndInt.get(i);

            if (representation.getOuterClassesOrInterfaces()
                    .stream()
                    .filter(elem -> elem.equals(representation.getName())).collect(Collectors.toList()).size()>0) {
                throw new SemanticException("Duplicated class or interface name: " + representation.getName());
            }
            for (int k = 0; k < concernedClassesAndInt.size(); k++) {
                Representation representation2 = concernedClassesAndInt.get(k);
                if (representation != representation2
                        && representation.getName().equals(representation2.getName())
                        && representation.getOuterClassesOrInterfaces().equals(representation2.getOuterClassesOrInterfaces()))
                {
                    throw new SemanticException("Duplicated class name: " + representation.getName());
                }
            }
        }
    }

    //List<String>, because I will represent inner classes as list of outer classes name.
    private void pullUpMembers(String sourceFileName, List<String> sourceClassOrInterName, List<Integer> members, String destFileName, List<String> destClassOrInterName)
    {
         Representation sourceRepresentation = findClassOrInterfaceInFile(sourceFileName, sourceClassOrInterName);
         Representation destRepresentation = findClassOrInterfaceInFile(destFileName, destClassOrInterName);
         if(sourceRepresentation==null || destRepresentation==null)
             return;
         File source = new File(sourceFileName);
         File dest;
         if(!sourceFileName.equals(destFileName))
             dest = new File(destFileName);
         else
             dest = source;

         List<Statement> statementsToMove = new ArrayList<>();
         members.forEach(member -> {
             if(member>=sourceRepresentation.getStatements().size())
                 return;
             statementsToMove.add(sourceRepresentation.getStatements().get(member));
         });

         statementsToMove.forEach(statement ->
         {
             Pair<Integer, Integer> destinationLineAndColumn = findSpaceAtDestination(destRepresentation);
             copyStatement(source, dest, statement.getStartsAtLine(), statement.startsAtColumn,
                     statement.endsAtLine, statement.endsAtColumn, destinationLineAndColumn.getKey(), destinationLineAndColumn.getValue());
         });

    }

    private Pair<Integer, Integer> findSpaceAtDestination(Representation representation)
    {
        Pair<Integer, Integer> destLineAndColumn;
        if(representation.getStatements().isEmpty())
        {
            destLineAndColumn = new Pair<>(representation.getNodeRep().endsAtLine, representation.getNodeRep().endsAtColumn);
        }
        else
        {
            destLineAndColumn = new Pair<>(representation.getStatements().get(0).startsAtLine,
                                            representation.getStatements().get(0).startsAtColumn);
        }
        return destLineAndColumn;
    }


    private void copyStatement(File source, File dest, int startsAtLine, int startsAtColumn,
                               int endsAtLine, int endsAtColumn, Integer destinationLine, Integer destinationColumn) {

    }

    private Representation findClassOrInterfaceInFile(String fileName, List<String> sourceClassOrInterName) {
        if(classesAndInterfacesInFiles.get(fileName) == null)
            return null;
        List<Representation> representations = classesAndInterfacesInFiles.get(fileName)
                .stream()
                .filter(rep ->
                        {
                            List<String> outer = sourceClassOrInterName.subList(0, sourceClassOrInterName.size()-1);
                            String name = sourceClassOrInterName.get(sourceClassOrInterName.size()-1);
                            return outer.equals(rep.getOuterClassesOrInterfaces()) && name.equals(rep.getName());
                        }).collect(Collectors.toList());
        if(representations.size()!=1)
        {
            System.out.println("Cannot find adequate class or interface.");
            return null;
        }
        return representations.get(0);
    }

    private void writeClassesAndInterfaces() {
        classesAndInterfacesInFiles.forEach((file, reps) ->
        {
            StringBuilder output = new StringBuilder();
            output.append(file).append(": \n");
            reps.forEach(rep ->
            {
                output.append("\n\t").append(rep.toString()).append(": \n");
                if(!rep.getStatements().isEmpty())
                    rep.getStatements().forEach(statement -> output.append("\t\t").append(statement.toString()).append("\n"));
            });

            System.out.println(output);
        });
    }
}


