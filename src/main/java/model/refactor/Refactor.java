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
    public static String PROJECT_DIRECTORY;
    public final static String FILE_EXTENSION = "txt";
    private List<String> files;
    public static final String PULL_UP = "Pull up";
    public static final String PUSH_DOWN = "Push down";
    public static final String DELEGATE = "Inheritance to delegation";

    public Refactor(Scanner scanner, Parser parser, String projectDirectory) {
        this.scanner = scanner;
        this.parser = parser;
        this.ASTs = new HashMap<>();
        this.classesAndInterfacesInFiles = new HashMap<>();
        this.files = new ArrayList<>();
        if(projectDirectory == null)
            PROJECT_DIRECTORY = "src\\main\\resources\\projectFiles\\";
        else
            PROJECT_DIRECTORY = projectDirectory;
    }

    public void parseFiles(List<String> filenames) throws FileNotFoundException{
        for (String file : filenames) {
            scanner.bindFile(new File(file));
            String filePath = file.substring(PROJECT_DIRECTORY.length(), file.length());
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
            System.exit(0);
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
            List<Representation> representations = reps.stream().filter(rep -> rep. getOuterClassesOrInterfaces().isEmpty()).collect(Collectors.toList());
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
                }));
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
                throw new SemanticException("Duplicated class or interface name: " + representation.getName() + " in file " + file);
            }
            for (Representation representation2 : concernedClassesAndInt) { //duplicates over other classes
                if (representation != representation2
                        && representation.getName().equals(representation2.getName())
                        && representation.getOuterClassesOrInterfaces().equals(representation2.getOuterClassesOrInterfaces())) {
                    throw new SemanticException("Duplicated class name: " + representation.getName() + " in file " + file);
                }
            }
        }
    }

    //List<String>, because I will represent inner classes as list of outer classes name.
    public void pullUpOrPushDownMember(String sourceFileName, List<String> sourceClassOrInterName, int member,
                                       String destClassOrInterName, String ref)
    {
         Representation sourceRepresentation = findClassOrInterfaceInFile(sourceFileName, sourceClassOrInterName);
         if(sourceRepresentation == null)
             return;

        Representation destRepresentation;
         if(ref.equals(PULL_UP))
            destRepresentation = sourceRepresentation.getBaseByName(destClassOrInterName);
         else
             destRepresentation = sourceRepresentation.getSubByName(destClassOrInterName);

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
                     statementToMove.endsAtLine, statementToMove.endsAtColumn, destinationLineAndColumn.getKey(),
                     destinationLineAndColumn.getValue());
         } catch (IOException e) {
             e.printStackTrace();
             System.out.println("Problem with reading or writing to file occured. Exiting");
             System.exit(0);
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

        List<String> sourceFile = readFileToListOfLines(source);

        List<String> sourceStatement = new ArrayList<>(sourceFile.subList(startsAtLine-1, endsAtLine));
        sourceStatement.add(0, sourceStatement.get(0).substring(startsAtColumn-1, sourceStatement.get(0).length()));
        sourceStatement.remove(1);

        if(startsAtLine == endsAtLine)
            endsAtColumn = endsAtColumn - (startsAtColumn - 1);

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

            writeListOfLinesToFile(dest, destFile);
        }
        else
        {
            destFile = readFileToListOfLines(dest);

            String lineAtDest = destFile.get(destinationLine - 1);
            sourceStatement.add(0, lineAtDest.substring(0, destinationColumn - 1) + sourceStatement.get(0));
            sourceStatement.remove(1);
            sourceStatement.add(sourceStatement.get(sourceStatement.size() - 1) + lineAtDest.substring(destinationColumn - 1, lineAtDest.length()));
            sourceStatement.remove(sourceStatement.size() - 2);
            for (int i = startsAtLine; i <= endsAtLine; i++)
                sourceFile.remove(startsAtLine - 1);
            destFile.remove(destinationLine - 1);
            destFile.addAll(destinationLine - 1, sourceStatement);

            writeListOfLinesToFile(source, sourceFile);
            writeListOfLinesToFile(dest, destFile);
        }
    }

    private List<String> readFileToListOfLines(File file) throws IOException {
        BufferedReader sourceReader;
        sourceReader = new BufferedReader(new FileReader(file));

        List<String> sourceFile = new ArrayList<>();
        String line;
        while((line=sourceReader.readLine())!=null)
            sourceFile.add(line);

        sourceReader.close();

        return sourceFile;
    }

    private void writeListOfLinesToFile(File file, List<String> lines)
    {
        PrintWriter destWriter;
        try {
            destWriter = new PrintWriter(file);
        } catch (FileNotFoundException e) {
            System.out.println("Could not open file: " + file.getName());
            return;
        }
        for (int i = 0; i < lines.size(); i++) {
            if (i == lines.size() - 1)
                destWriter.print(lines.get(i));
            else
                destWriter.println(lines.get(i));
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

    public Representation getBaseOrSubClassOrInterfaceFor(String sourceClass, String destClass, String sourceFile, String chosenRefactor)
    {
        if(sourceFile == null || !sourceFile.contains(".") || destClass==null || chosenRefactor==null || sourceClass==null)
            return null;
        List<String> chosenClassPath = new ArrayList<>(Arrays.asList(sourceClass.split("\\.")));
        Representation representation = findClassOrInterfaceInFile(sourceFile, chosenClassPath);
        switch (chosenRefactor)
        {
            case PULL_UP:
            {
                return representation.getBaseByName(destClass);
            }
            case PUSH_DOWN:
            {
                return representation.getSubByName(destClass);
            }
            case DELEGATE:
            {
                return representation.getBaseByName(destClass);
            }
            default:
                return null;
        }
    }

    public List<Representation> getClassesAndInterfacesInFile(String fileName)
    {
        return classesAndInterfacesInFiles.get(fileName);
    }

    public void replaceInheritanceWithDelegationFor(String sourceFile, String sourceClass, List<Integer> chosenStatements,
                                                    String destClass, String newFieldName, String newClassName)
    {
        if(sourceFile == null || !sourceFile.contains(".") || destClass==null || sourceClass==null)
            return;
        List<String> chosenClassPath = new ArrayList<>(Arrays.asList(sourceClass.split("\\.")));
        Representation sourceRepresentation = findClassOrInterfaceInFile(sourceFile,chosenClassPath );
        if(sourceRepresentation.getBases().isEmpty() || sourceRepresentation instanceof InterfaceRepresentation)
            return;

        Representation baseRepresentation = getBaseOrSubClassOrInterfaceFor(sourceClass, destClass, sourceFile, DELEGATE);

        if(baseRepresentation instanceof InterfaceRepresentation)
            return;

        List<Statement> statementsBase = getStatementsForDelegationFromClass(baseRepresentation);   //get methods
        List<Statement> statementsSource = getStatementsForDelegationFromClass(sourceRepresentation);
        List<Statement> statementsToDelegate = new ArrayList<>();

        chosenStatements.forEach(number -> statementsToDelegate.add(statementsBase.get(number)));

        List<Statement> overriddenMethods = statementsSource.stream().filter(stmnt ->
                statementsBase.stream().filter(stmntBase -> (stmntBase.getStatementSignature()+stmntBase.getReturnType())
                                        .equals(stmnt.getStatementSignature() + stmnt.getReturnType()))
                        .collect(Collectors.toList()).size()>0).collect(Collectors.toList());

        List<Statement> overriddenAtBase = statementsBase.stream().filter(stmnt ->
                statementsSource.stream().filter(stmntSource -> (stmntSource.getStatementSignature()+stmntSource.getReturnType())
                        .equals(stmnt.getStatementSignature() + stmnt.getReturnType()))
                        .collect(Collectors.toList()).size()>0).collect(Collectors.toList());

        statementsToDelegate.addAll(overriddenAtBase.stream().filter(stmntBase ->  statementsToDelegate.stream()
                .filter(stmntDeleg -> (stmntDeleg.getStatementSignature()+stmntDeleg.getReturnType())
                .equals(stmntBase.getStatementSignature() + stmntBase.getReturnType()))
                .collect(Collectors.toList()).size()==0).collect(Collectors.toList()));

        File source = new File(PROJECT_DIRECTORY + sourceFile);
        try {
            makeDelegateOperationsOnFiles(source, sourceRepresentation, destClass, statementsToDelegate,
                                            overriddenMethods, newFieldName, newClassName);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Problem with reading or writing to file. Exiting");
            System.exit(0);
        }

    }

    private void makeDelegateOperationsOnFiles(File source, Representation sourceRepresentation, String baseClassName,
                                               List<Statement> statementsToDelegate, List<Statement> overriddenMethods,
                                               String newFieldName, String newClassName) throws IOException
    {
        List<String> sourceFile = readFileToListOfLines(source);

        StringBuilder sourceClassHeader = new StringBuilder();
        int originalHeaderStart = sourceRepresentation.getNodeRep().children.get(0).startsAtLine;
        int originalHeaderStartColumn = sourceRepresentation.getNodeRep().children.get(0).startsAtColumn;
        int linesToRemove = 0;
        while (!sourceFile.isEmpty()){
            sourceClassHeader.append(sourceFile.get(originalHeaderStart-1+linesToRemove)).append("\n");
            linesToRemove++;
            if(sourceClassHeader.toString().contains("{")
                    && sourceClassHeader.toString().lastIndexOf("{") > originalHeaderStartColumn) {
                break;
            }
        }

        String restBeginning = sourceClassHeader.substring(0, originalHeaderStartColumn-1);
        sourceClassHeader = new StringBuilder(sourceClassHeader.substring(originalHeaderStartColumn-1));
        String restEnding = sourceClassHeader.substring(sourceClassHeader.indexOf("{")+1);
        sourceClassHeader = new StringBuilder(sourceClassHeader.substring(0, sourceClassHeader.indexOf("{")+1));

        String toFix = sourceClassHeader.toString();

        if(sourceRepresentation.getExtends().size()==1)
            toFix = toFix.replaceFirst("[\\s]*extends", "");
        if(sourceRepresentation.getExtends().size()==1)
            toFix = toFix.replaceFirst(baseClassName, "");
        else if(sourceRepresentation.getExtends().size()>1) {
            toFix = toFix.replaceFirst(baseClassName + "[\\s]*,", "");
            toFix = toFix.replaceFirst("[\\s]*," + baseClassName, "");
        }


        StringBuilder withDelegates = new StringBuilder();
        withDelegates.append(toFix);
        withDelegates.append(" private ").append(newClassName).append(" ").append(newFieldName).append(";\n");

        statementsToDelegate.forEach(stmnt->
                {
                        withDelegates.append(stmnt.getAccessModifier().toLowerCase()).append(" ").append(stmnt.getReturnType())
                                .append(" ").append(stmnt.getName())
                                .append(stmnt.getMethodParameterList()).append("{\n\t\t");
                        if(!stmnt.getReturnType().equals(""))
                            withDelegates.append("return ");
                        withDelegates.append("this.").append(newFieldName+".").append(stmnt.getName()).append("(");
                        for(int i = 0; i<stmnt.getParametersNames().size(); i++)
                        {
                            withDelegates.append(stmnt.getParametersNames().get(i));
                            if(i<stmnt.getParametersNames().size()-1)
                                withDelegates.append(", ");
                        }
                        withDelegates.append(");\n\t}\n");
                }
        );

        withDelegates.append(" private class ").append(newClassName).append(" extends ").append(baseClassName).append("{\n");
        String finalForm = putOverriddenMethodsAndRemoveFromSource(withDelegates, overriddenMethods, sourceFile);
        ///////////////////////////////////////////////////////////////////////////

        String toPutToSource = restBeginning + finalForm + restEnding;

        for(int i = 0; i<linesToRemove; i++)
        {
            sourceFile.remove(originalHeaderStart-1);
        }
        sourceFile.add(originalHeaderStart-1, toPutToSource);

        writeListOfLinesToFile(source, sourceFile);
    }

    private String putOverriddenMethodsAndRemoveFromSource(StringBuilder withDelegates, List<Statement> overriddenMethods,
                                                           List<String> sourceFile)
    {
        String temporary;
        StringBuilder result = new StringBuilder();
        for(int i = overriddenMethods.size()-1; i>=0; i--)
        {
            Statement current = overriddenMethods.get(i);
            if(current.startsAtLine == current.endsAtLine)
            {
                temporary = sourceFile.get(current.startsAtLine-1);
                result.append(temporary, current.startsAtColumn-1, current.endsAtColumn);
                temporary = temporary.substring(0, current.startsAtColumn-1) + temporary.substring(current.endsAtColumn, temporary.length());
                sourceFile.remove(current.startsAtLine-1);
                sourceFile.add(current.startsAtLine-1, temporary);
            }
            else
            {
                temporary = sourceFile.get(current.startsAtLine-1);
                result.append(temporary, current.startsAtColumn-1, temporary.length()).append("\n");
                temporary = temporary.substring(0, current.startsAtColumn-1);
                for(int k = current.startsAtLine; k<current.endsAtLine-1; k++)
                {
                    result.append(sourceFile.get(k)).append("\n");
                }
                String temporaryEnd = sourceFile.get(current.endsAtLine-1);
                result.append(temporaryEnd, 0, current.endsAtColumn);
                temporary += temporaryEnd.substring(current.endsAtColumn, temporaryEnd.length());
                for(int k = current.startsAtLine-1; k<current.endsAtLine; k++)
                {
                    sourceFile.remove(current.startsAtLine-1);
                }
                sourceFile.add(current.startsAtLine-1, temporary);
            }
        }
        withDelegates.append(result.toString()).append("\n").append("}\n");
        return withDelegates.toString();
    }


    public List<Statement> getStatementsForDelegationFromClass(Representation destRepresentation)
    {
        if(destRepresentation.getStatements().isEmpty())
            return new ArrayList<>();
        return destRepresentation.getStatements().stream().filter(stmnt -> stmnt.getStatementType().equals(Statement.StatementType.AbstractMethodDeclaration)
                || stmnt.getStatementType().equals(Statement.StatementType.MethodDefinition)
                || stmnt.getStatementType().equals(Statement.StatementType.MethodDeclaration))
                .collect(Collectors.toList());
    }
}