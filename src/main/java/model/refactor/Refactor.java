package model.refactor;

import model.exceptions.ParsingException;
import model.exceptions.SemanticException;
import model.parser.AbstractSyntaxTree;
import model.parser.Parser;
import model.scanner.Scanner;
import javafx.util.Pair;

import java.io.*;
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
    private final static String PROJECT_DIRECTORY = "src\\main\\resources\\projectFiles\\";

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
    public void pullUpMembers(String sourceFileName, List<String> sourceClassOrInterName, List<Integer> members, String destFileName, List<String> destClassOrInterName)
    {
         Representation sourceRepresentation = findClassOrInterfaceInFile(sourceFileName, sourceClassOrInterName);
         Representation destRepresentation = findClassOrInterfaceInFile(destFileName, destClassOrInterName);

         if(sourceRepresentation==null || destRepresentation==null)
             return;

         File source = new File(PROJECT_DIRECTORY + sourceFileName);
         File dest = new File(PROJECT_DIRECTORY + destFileName);

         if(!source.exists() || !dest.exists())
             return;

         List<Statement> statementsToMove = new ArrayList<>();
         members.forEach(member -> {
             if(member>=sourceRepresentation.getStatements().size())
                 return;
             statementsToMove.add(sourceRepresentation.getStatements().get(member));
         });

         statementsToMove.forEach(statement ->
         {
             Pair<Integer, Integer> destinationLineAndColumn = findSpaceAtDestination(destRepresentation);
             try {
                 copyStatement(source, dest, statement.getStartsAtLine(), statement.startsAtColumn,
                         statement.endsAtLine, statement.endsAtColumn, destinationLineAndColumn.getKey(), destinationLineAndColumn.getValue());
             } catch (IOException e) {
                 e.printStackTrace();
                 return;
             }
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
                               int endsAtLine, int endsAtColumn, Integer destinationLine, Integer destinationColumn) throws IOException {

        BufferedReader sourceReader;
        try {
            sourceReader = new BufferedReader(new FileReader(source));
        } catch (FileNotFoundException e) {
            System.out.println("Could not open file: " + source.getName());
            return;
        }

        List<String> sourceFile = new ArrayList<>();
        String line;
        while((line=sourceReader.readLine())!=null)
            sourceFile.add(line);

        sourceReader.close();

        List<String> sourceStatement = new ArrayList<>(sourceFile.subList(startsAtLine-1, endsAtLine));
        sourceStatement.add(0, "\n" + sourceStatement.get(0).substring(startsAtColumn-1, sourceStatement.get(0).length()));
        sourceStatement.remove(1);

        if(startsAtLine == endsAtLine)
            endsAtColumn = endsAtColumn - (startsAtColumn - 2);

        sourceStatement.add(sourceStatement.get(sourceStatement.size()-1).substring(0, endsAtColumn) + "\n");
        sourceStatement.remove(sourceStatement.size()-2);

        if(destinationLine >= startsAtLine) //first copy the statement, then remove it from its original location
        {
            String lineAtDest = sourceFile.get(destinationLine-1);
            sourceStatement.add(0, lineAtDest.substring(0, destinationColumn-1) + sourceStatement.get(0));
            sourceStatement.remove(1);
            sourceStatement.add(sourceStatement.get(sourceStatement.size()-1) + lineAtDest.substring(destinationColumn-1, lineAtDest.length()));
            sourceStatement.remove(sourceStatement.size()-2);
            sourceFile.remove(destinationLine-1);
            sourceFile.addAll(destinationLine-1, sourceStatement);
            for(int i = startsAtLine; i<=endsAtLine; i++)
                sourceFile.remove(startsAtLine-1);
        }
        else    //first remove the statement, then copy it to the destinated location
        {
            String lineAtDest = sourceFile.get(destinationLine-1);
            sourceStatement.add(0, lineAtDest.substring(0, destinationColumn-1) + sourceStatement.get(0));
            sourceStatement.remove(1);
            sourceStatement.add(sourceStatement.get(sourceStatement.size()-1) + lineAtDest.substring(destinationColumn-1, lineAtDest.length()));
            sourceStatement.remove(sourceStatement.size()-2);
            for(int i = startsAtLine; i<=endsAtLine; i++)
                sourceFile.remove(startsAtLine-1);
            sourceFile.remove(destinationLine-1);
            sourceFile.addAll(destinationLine-1, sourceStatement);
        }

        PrintWriter destWriter;
        try {
            destWriter = new PrintWriter(dest);
        } catch (FileNotFoundException e) {
            System.out.println("Could not open file: " + dest.getName());
            return;
        }
        for(int i = 0; i<sourceFile.size(); i++)
        {
            if(i==sourceFile.size()-1)
                destWriter.print(sourceFile.get(i));
            else
                destWriter.println(sourceFile.get(i));
        }
        destWriter.close();
    }

    public Representation findClassOrInterfaceInFile(String fileName, List<String> sourceClassOrInterName) {
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

    public List<Representation> getClassesAndInterfacesInFile(String fileName)
    {
        return classesAndInterfacesInFiles.get(fileName);
    }
}

