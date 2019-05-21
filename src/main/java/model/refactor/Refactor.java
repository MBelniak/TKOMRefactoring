package model.refactor;

import javafx.util.Pair;
import model.exceptions.ParsingException;
import model.exceptions.SemanticException;
import model.parser.AbstractSyntaxTree;
import model.parser.Parser;
import model.scanner.Scanner;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class Refactor {
    private final Scanner scanner;
    private final Parser parser;
    private Map<String, AbstractSyntaxTree> ASTs;
    private Map<String, List<Representation>> classesAndInterfacesInFiles;  //dir1/dir2/file.txt
    public final static String PROJECT_DIRECTORY = "src\\main\\resources\\projectFiles\\";
    public final static String FILE_EXTENSION = "txt";
    private final List<String> refactorings = new ArrayList<>(Arrays.asList("Pull up", "Push down", "Inheritance to delegation"));
    private List<String> files;

    public Refactor(Scanner scanner, Parser parser) {
        this.scanner = scanner;
        this.parser = parser;
        this.ASTs = new HashMap<>();
        this.classesAndInterfacesInFiles = new HashMap<>();
        this.files = new ArrayList<>();
    }

    public void parseFiles(List<String> filenames) throws FileNotFoundException{
        for (String file : filenames) {
            scanner.bindFile(new File(file));
            String filePath = file.substring(32, file.length());
            try {
                ASTs.put(filePath, parser.parseFile());
                files.add(filePath);
            } catch (ParsingException e) {
                System.out.println(e.getErrorMessage() + " Line: " + e.getLine() + " Column: " + e.getColumn()
                        + " in file: " + file);
                System.exit(0);
            }
        }
    }

    public void analyze() {
        try {
            analyzeClassesAndInterfaces();
        } catch (SemanticException e) {
            System.out.println(e.getMessage());
        }
        //writeClassesAndInterfaces();
    }

    private void analyzeClassesAndInterfaces() throws SemanticException {
        ASTs.forEach((filePath, AST)  ->{
            List<Representation> foundCI = null;
            try {
                foundCI = AST.findClassesAndInterfacesAndPutInContext(filePath);
            } catch (SemanticException e) {
                System.out.println(e.getMessage());
                System.exit(0);
            }
            classesAndInterfacesInFiles.put(filePath, foundCI);
        });
        checkForDuplicatedClasses();
        checkForDuplicatedStatements();
        analyzePublicClassesNamesMatch();
        setImportsInRepresentations();
        checkBasesVisibilities();
    }

    /*
       Basic check of duplication. Throws SemanticException if class or interface with the same name
       is declared in overlapping scope in the certain file.
    */
    private void checkForDuplicatedClasses() throws SemanticException {
        for (String file : files) {
            checkForInnerDuplicates(file);
        }
        //checkForOuterDuplicates(); not needed if made sure, that all public classes are named same as the file
    }

    private void checkForDuplicatedStatements() {
        classesAndInterfacesInFiles.forEach((filePath, reps) ->
        {
            reps.forEach(rep ->
            {
                rep.getStatements().forEach(statement -> {
                    if(rep.checkIfSameStatementExists(statement, 2)) {
                        System.out.println("Found duplicated " + statement.getCategory() + ": "
                                + statement.getStatementSignature() + " in class " + rep.getName() + " in file " + filePath);
                        System.exit(0);
                    }
                });
            });
        });
    }

    private void setImportsInRepresentations() {
        ASTs.forEach((filePath, AST) ->
        {
            List<String> imports = AST.checkImports(); //A.B.C.*, A.B.C
            classesAndInterfacesInFiles.get(filePath).forEach(rep->
                    rep.setImports(imports));
        });
    }

    private void analyzePublicClassesNamesMatch() {
        classesAndInterfacesInFiles.forEach((file, reps) ->
        {
            //get Outer Classes
            List<Representation> representations = reps.stream().filter(rep -> rep.getOuterClassesOrInterfaces().isEmpty()).collect(Collectors.toList());
            List<Representation> representation = representations.stream().filter(rep -> rep.getAccessModifier().equals("public")).collect(Collectors.toList());
            if(representation.size() == 1)
                if(!representation.get(0).getName().equals(getFileName(file)))
                    try {
                        throw new SemanticException("Public class/interface name does not match the file name. File path: " + file + ", class/interface name: " + representation.get(0).getName());
                    } catch (SemanticException e) {
                        System.out.println(e.getMessage());
                        System.exit(0);
                    }
        });
    }

    private void checkBasesVisibilities()
    {
        classesAndInterfacesInFiles.forEach((file, classReps) ->
        {
            classReps.forEach(rep ->
            {
                try {
                    rep.checkIfBaseVisible(classesAndInterfacesInFiles);
                    rep.checkIfInterfacesVisible(classesAndInterfacesInFiles);
                }catch (SemanticException e)
                {
                    System.out.println(e.getMessage());
                    System.exit(0);
                }
            });
        });
    }

    private String getFileName(String filePath)
    {
        List<String> dirs = Arrays.asList(filePath.split("[\\\\/]"));
        dirs = dirs.subList(dirs.size()-1, dirs.size());
        String fileName = dirs.get(0);
        if(fileName.contains("."))
        {
            fileName = fileName.substring(0, fileName.lastIndexOf('.'));
        }
        return fileName;
    }




    private void checkForInnerDuplicates(String file) throws SemanticException {
        List<Representation> concernedClassesAndInt = classesAndInterfacesInFiles.get(file);
        for (int i = 0; i < concernedClassesAndInt.size(); i++) {
            Representation representation = concernedClassesAndInt.get(i);
            List<String> outerNames = representation.getOuterClassesOrInterfaces()
                    .stream()
                    .filter(elem -> elem.equals(representation.getName())).collect(Collectors.toList());

            if (outerNames.size() > 0) {        //duplicates in the certain class
                throw new SemanticException("Duplicated class or interface name: " + representation.getName());
            }
            for (Representation representation2 : concernedClassesAndInt) { //duplicates over other classes
                if (representation != representation2
                        && representation.getName().equals(representation2.getName())
                        && representation.getOuterClassesOrInterfaces().equals(representation2.getOuterClassesOrInterfaces())) {
                    throw new SemanticException("Duplicated class name: " + representation.getName());
                }
            }
        }
    }

    //List<String>, because I will represent inner classes as list of outer classes name.
    public void pullUpMember(String sourceFileName, List<String> sourceClassOrInterName, int member, String destClassOrInterName)
    {
         Representation sourceRepresentation = findClassOrInterfaceInFile(sourceFileName, sourceClassOrInterName);
         if(sourceRepresentation == null)
             return;
         Representation destRepresentation = sourceRepresentation.getBaseByName(destClassOrInterName);

         if(destRepresentation==null)
             return;

         File source = new File(PROJECT_DIRECTORY + sourceFileName);
         File dest = new File(PROJECT_DIRECTORY + destRepresentation.getFilePath());

         if(!source.exists() || !dest.exists())
             return;

         Statement statementToMove = sourceRepresentation.getStatements().get(member);

         Pair<Integer, Integer> destinationLineAndColumn = findSpaceAtDestination(destRepresentation);
         try {
             copyStatement(source, dest, statementToMove.getStartsAtLine(), statementToMove.getStartsAtColumn(),
                     statementToMove.endsAtLine, statementToMove.endsAtColumn, destinationLineAndColumn.getKey(), destinationLineAndColumn.getValue());
         } catch (IOException e) {
             e.printStackTrace();
         }
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

        List<String> destFile;

        if(source.getName().equals(dest.getName())) {
            destFile = new ArrayList<>(sourceFile);

            if (destinationLine >= startsAtLine) //first copy the statement, then remove it from its original location
            {
                String lineAtDest = destFile.get(destinationLine - 1);
                sourceStatement.add(0, lineAtDest.substring(0, destinationColumn - 1) + sourceStatement.get(0));
                sourceStatement.remove(1);
                sourceStatement.add(sourceStatement.get(sourceStatement.size() - 1) + lineAtDest.substring(destinationColumn - 1, lineAtDest.length()));
                sourceStatement.remove(sourceStatement.size() - 2);
                destFile.remove(destinationLine - 1);
                destFile.addAll(destinationLine - 1, sourceStatement);
                for (int i = startsAtLine; i <= endsAtLine; i++)
                    destFile.remove(startsAtLine - 1);
            }
            else    //first remove the statement, then copy it to the destinated location
            {
                String lineAtDest = destFile.get(destinationLine - 1);
                sourceStatement.add(0, lineAtDest.substring(0, destinationColumn - 1) + sourceStatement.get(0));
                sourceStatement.remove(1);
                sourceStatement.add(sourceStatement.get(sourceStatement.size() - 1) + lineAtDest.substring(destinationColumn - 1, lineAtDest.length()));
                sourceStatement.remove(sourceStatement.size() - 2);
                for (int i = startsAtLine; i <= endsAtLine; i++)
                    destFile.remove(startsAtLine - 1);
                destFile.remove(destinationLine - 1);
                destFile.addAll(destinationLine - 1, sourceStatement);
            }

            PrintWriter destWriter;
            try {
                destWriter = new PrintWriter(dest);
            } catch (FileNotFoundException e) {
                System.out.println("Could not open file: " + dest.getName());
                return;
            }
            for (int i = 0; i < destFile.size(); i++) {
                if (i == destFile.size() - 1)
                    destWriter.print(destFile.get(i));
                else
                    destWriter.println(destFile.get(i));
            }
            destWriter.close();
        }
        else
        {
            try {
                sourceReader = new BufferedReader(new FileReader(dest));
            } catch (FileNotFoundException e) {
                System.out.println("Could not open file: " + dest.getName());
                return;
            }

            destFile = new ArrayList<>();
            String lineDest;
            while((lineDest=sourceReader.readLine())!=null)
                destFile.add(lineDest);

            sourceReader.close();

            String lineAtDest = destFile.get(destinationLine - 1);
            sourceStatement.add(0, lineAtDest.substring(0, destinationColumn - 1) + sourceStatement.get(0));
            sourceStatement.remove(1);
            sourceStatement.add(sourceStatement.get(sourceStatement.size() - 1) + lineAtDest.substring(destinationColumn - 1, lineAtDest.length()));
            sourceStatement.remove(sourceStatement.size() - 2);
            for (int i = startsAtLine; i <= endsAtLine; i++)
                sourceFile.remove(startsAtLine - 1);
            destFile.remove(destinationLine - 1);
            destFile.addAll(destinationLine - 1, sourceStatement);

            PrintWriter destWriter;
            try {
                destWriter = new PrintWriter(source);
            } catch (FileNotFoundException e) {
                System.out.println("Could not open file: " + source.getName());
                return;
            }
            for (int i = 0; i < sourceFile.size(); i++) {
                if (i == sourceFile.size() - 1)
                    destWriter.print(sourceFile.get(i));
                else
                    destWriter.println(sourceFile.get(i));
            }
            destWriter.close();

            try {
                destWriter = new PrintWriter(dest);
            } catch (FileNotFoundException e) {
                System.out.println("Could not open file: " + dest.getName());
                return;
            }
            for (int i = 0; i < destFile.size(); i++) {
                if (i == destFile.size() - 1)
                    destWriter.print(destFile.get(i));
                else
                    destWriter.println(destFile.get(i));
            }
            destWriter.close();
        }
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

    public List<String> getRefactorings() {
        return refactorings;
    }
}