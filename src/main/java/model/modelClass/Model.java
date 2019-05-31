package model.modelClass;

import model.parser.Parser;
import model.refactor.*;
import model.scanner.Scanner;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Model {
    private static final Scanner scanner = new Scanner();
    private static final Parser parser = new Parser(scanner);
    private static Refactor refactor;
    private static String PROJECT_DIRECTORY;
    private List<String> filesInProjectDirectory;
    public static final String PULL_UP = Refactor.PULL_UP;
    public static final String PUSH_DOWN = Refactor.PUSH_DOWN;
    public static final String DELEGATE = Refactor.DELEGATE;

    public Model(String projectDirectory)
    {
        filesInProjectDirectory  = new ArrayList<>();
        if(projectDirectory == null)
            PROJECT_DIRECTORY = "src\\main\\resources\\projectFiles\\";
        else
            PROJECT_DIRECTORY = projectDirectory;

        refactor = new Refactor(scanner, parser, PROJECT_DIRECTORY);

        try (Stream<Path> walk = Files.walk(Paths.get(PROJECT_DIRECTORY))) {

            filesInProjectDirectory = walk.map(Path::toString)
                    .filter(f -> f.endsWith(".txt")).collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            refactor.parseFiles(filesInProjectDirectory);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.exit(0);
        }
        refactor.analyze();
    }

    public List<String> getFilesInProjectDirectory() {
        return filesInProjectDirectory.stream().map(string -> string.substring(PROJECT_DIRECTORY.length(), string.length())).collect(Collectors.toList());
    }

    private List<Representation> getRepresentationsInFile(String fileName)
    {
        return refactor.getClassesAndInterfacesInFile(fileName);
    }

    public List<Statement> getStatementsInFileInClass(String fileName, String className) {
        if(className == null || className.length()==0)
            return new ArrayList<>();
        List<String> classPath = new ArrayList<>(Arrays.asList(className.split("\\.")));
        Representation representation = refactor.findClassOrInterfaceInFile(fileName, classPath);
        if(representation==null)
            return new ArrayList<>();
        return representation.getStatements();
    }

    public void doPushOrPull(String fileName, String className, List<Integer> statements, String destClassName, String refType)
    {
        List<String> classPath = new ArrayList<>(Arrays.asList(className.split("\\.")));
        for(int i = 0; i<statements.size(); i++) {
            refactor.pullUpOrPushDownMember(fileName, classPath, statements.get(i), destClassName, refType);
            statements = statements.stream().map(x-> x - 1).collect(Collectors.toList());

            try {
                refactor.parseFiles(filesInProjectDirectory);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                System.exit(0);
            }
            refactor.analyze();
        }
    }

    public List<String> checkForWarnings(String fileName, String className, List<Integer> statements, String destClassName,
                                         String refType, String newFieldName, String newClassName)
    {
        List<String> warningList;
        if(refType.equals(PUSH_DOWN) || refType.equals(PULL_UP)) {
            warningList = new ArrayList<>(getDuplicationWarnings(fileName, className, statements, destClassName, refType));
            warningList.addAll(getOtherWarnings(fileName, className, statements, destClassName, refType));
        }
        else {
            warningList = new ArrayList<>(getOtherWarnings(fileName, className, newFieldName, newClassName));
        }
        if(!warningList.isEmpty())
            warningList.add("These errors may cause the program to shut down.");
        return warningList;
    }

    private List<String> getOtherWarnings(String fileName, String className, List<Integer> statements, String destClassName, String refType)
    {
        Representation representation = refactor.findClassOrInterfaceInFile(fileName, Arrays.asList(className.split("\\.")));
        Representation destRepresentation = refactor.getBaseOrSubClassOrInterfaceFor(className, destClassName, fileName, refType);
        List<Statement> statementListPre = getStatementsInFileInClass(fileName, className);
        List<Statement> statementList = new ArrayList<>();
        statements.forEach(num -> statementList.add(statementListPre.get(num)));
        List<String> result = new ArrayList<>();
        if(refType.equals(PULL_UP) || refType.equals(PUSH_DOWN)) //delegation has other method for other warnings
        {
            if (representation instanceof InterfaceRepresentation)  //it's push down then
            {
                if (destRepresentation instanceof ClassRepresentation) {
                    statementList.forEach(stmnt ->
                    {
                        if (stmnt.getStatementType() == Statement.StatementType.FieldDefinition)
                            result.add("Field " + stmnt.getName() + " initialization will have to be removed manually in the destinated class.");
                        else if (stmnt.getStatementType() == Statement.StatementType.MethodDeclaration)
                            result.add("Method " + stmnt.getName() + " will have to be implemented manually in the destined class.");
                        else if (stmnt.getStatementType() == Statement.StatementType.MethodDefinition
                                && stmnt.getAccessModifier().equals(Statement.AccessModifier.Default.toString()))
                            result.add("Method " + stmnt.getName() + " is declared as default. Classes do not support default keyword.");
                    });
                }
            } else {
                if (destRepresentation instanceof InterfaceRepresentation) { //it's pull up then
                    statementList.forEach(stmnt ->
                    {
                        if (stmnt.getStatementType() == Statement.StatementType.AbstractMethodDeclaration)
                            result.add("Method " + stmnt.getName() + " is declared abstract.");
                        else if (stmnt.getStatementType() == Statement.StatementType.AbstractClassDefinition)
                            result.add("Class " + stmnt.getName() + " is declared abstract.");
                        else if (stmnt.getStatementType() == Statement.StatementType.FieldDeclaration)
                            result.add("Field " + stmnt.getName() + " will have to be initialized in the destinated interface.");
                        else if (stmnt.getStatementType() == Statement.StatementType.InterfaceDefinition)
                            result.add("Interface " + stmnt.getName() + " cannot be defined in other interface.");
                        else if (stmnt.getStatementType() == Statement.StatementType.MethodDefinition
                                && !stmnt.getAccessModifier().equals(Statement.AccessModifier.Private.toString()))
                            result.add("Method " + stmnt.getName() + " has its definition and is not private.");
                        else if (stmnt.getStatementType() == Statement.StatementType.ClassDefinition
                                && !stmnt.getAccessModifier().equals(""))
                            result.add("Class " + stmnt.getName() + " has access modifier defined and will be incorrectly parsed.");
                    });
                }
            }
        }
        return result;
    }

    private List<String> getOtherWarnings(String fileName, String className, String newFieldName, String newClassName)
    {
        Representation representation = refactor.findClassOrInterfaceInFile(fileName, Arrays.asList(className.split("\\.")));
        List<String> warnings = new ArrayList<>();
        if(newClassName.equals(representation.getName()))
            warnings.add("New class cannot have the same name as its parent class.");

        List<Statement> conflictableStatementsInRepresentation = representation.getStatements().stream().filter(stmnt ->
                           stmnt.getStatementType().equals(Statement.StatementType.FieldDeclaration)
                        || stmnt.getStatementType().equals(Statement.StatementType.FieldDefinition)
                        || stmnt.getStatementType().equals(Statement.StatementType.AbstractClassDefinition)
                        || stmnt.getStatementType().equals(Statement.StatementType.ClassDefinition)
                        || stmnt.getStatementType().equals(Statement.StatementType.InterfaceDefinition)).collect(Collectors.toList());

        conflictableStatementsInRepresentation.forEach(stmnt ->
        {
            if(stmnt.getStatementType().equals(Statement.StatementType.AbstractClassDefinition)
                    || stmnt.getStatementType().equals(Statement.StatementType.ClassDefinition)
                    || stmnt.getStatementType().equals(Statement.StatementType.InterfaceDefinition))
            {
                if(stmnt.getName().equals(newClassName))
                    warnings.add("Class or interface with name " + stmnt.getName() + " already exists in this class.");
            }
            else
            {
                if(stmnt.getName().equals(newFieldName))
                    warnings.add("Field with name " + stmnt.getName() + " already exists in this class.");
            }
        });
        return warnings;
    }
    private List<String> getDuplicationWarnings(String fileName, String className, List<Integer> statements, String destClassName, String refType) {
        List<String> warningList = new ArrayList<>();

        List<String> classPath = new ArrayList<>(Arrays.asList(className.split("\\.")));
        Representation sourceRepresentation = refactor.findClassOrInterfaceInFile(fileName, classPath);
        Representation destRepresentation;
        if(refType.equals(PULL_UP))
             destRepresentation = sourceRepresentation.getBaseByName(destClassName);
        else
            destRepresentation = sourceRepresentation.getSubByName(destClassName);

        for(int i = 0; i<statements.size(); i++)
        {
            Statement statement = sourceRepresentation.getStatements().get(statements.get(i));
            if(destRepresentation.checkIfSameStatementExists(statement, 1)) //exists
            {
                warningList.add(statement.getCategory() + " declared as "
                        + statement.getStatementSignature() + " already exists in class/interface "+ destRepresentation.getName());
            }
        }

        return warningList;
    }

    public List<String> getBaseOrSubClassesWithTypeFor(String file, String chosenClass, String chosenRefactor) {
        if(chosenRefactor==null)
            return new ArrayList<>();
        return getBasesOrSubsWithTypeFor(file, chosenClass, chosenRefactor);
    }

    private List<String> getBasesOrSubsWithTypeFor(String file, String chosenClass, String refType) {
        if(file == null || !file.contains("."))
            return new ArrayList<>();
        List<String> chosenClassPath = new ArrayList<>(Arrays.asList(chosenClass.split("\\.")));
        Representation representation = refactor.findClassOrInterfaceInFile(file, chosenClassPath);
        if(representation==null)
            return new ArrayList<>();
        if(refType.equals(PULL_UP)) {
            if(representation.getBases().isEmpty())
                return new ArrayList<>();
            return representation.getBases().stream().map(base -> {
                StringBuilder result = new StringBuilder();
                Representation b = representation.getBaseByName(base);
                if (b instanceof ClassRepresentation)
                    result.append("(").append('C').append(") ");
                else
                    result.append("(").append('I').append(") ");
                result.append(base);
                return result.toString();
            }).collect(Collectors.toList());
        }
        else if(refType.equals(DELEGATE)) {
            if(representation.getExtends().isEmpty())
                return new ArrayList<>();
            return representation.getExtends().stream().map(base -> {
                StringBuilder result = new StringBuilder();
                Representation b = representation.getBaseByName(base);
                if (b instanceof ClassRepresentation)
                    result.append("(").append('C').append(") ");
                else
                    result.append("(").append('I').append(") ");
                result.append(base);
                return result.toString();
            }).collect(Collectors.toList());
        }
        return representation.getSubclassesOrSubinterfaces().stream().map(sub -> {
            StringBuilder result = new StringBuilder();
            Representation b = representation.getSubByName(sub);
            if(b instanceof ClassRepresentation)
                result.append("(").append('C').append(") ");
            else
                result.append("(").append('I').append(") ");
            result.append(sub);
            return result.toString();
        }).collect(Collectors.toList());
    }

    public List<String> getRefactors() {
        List<String> refactors = new ArrayList<>();
        refactors.add(PULL_UP);
        refactors.add(PUSH_DOWN);
        refactors.add(DELEGATE);
        return refactors;
    }

    public List<String> getClassesListWithType(String chosenSourceFile) {
        List<Representation> representations = getRepresentationsInFile(chosenSourceFile);
        return representations.stream().map(rep -> {
            StringBuilder classPath = new StringBuilder();
            if(rep instanceof ClassRepresentation)
                classPath.append("(" + 'C' + ") ");
            else
                classPath.append("(" + 'I' + ") ");
            rep.getOuterClassesOrInterfaces().forEach(name -> classPath.append(name).append("."));
            classPath.append(rep.getName());
            return classPath.toString();
        }).collect(Collectors.toList());
    }

    public List<Statement> getStatementsWithFileAndSourceClassForDelegation(String chosenSourceFile, String sourceClass, String destClass) {
        Representation baseRepresentation = refactor.getBaseOrSubClassOrInterfaceFor(sourceClass, destClass, chosenSourceFile, DELEGATE);
        if(baseRepresentation == null)
            return new ArrayList<>();

        return refactor.getStatementsForDelegationFromClass(baseRepresentation);
    }

    public void doDelegate(String sourceFile, String sourceClass, List<Integer> chosenStatements, String destClass,
                           String newFieldName, String newClassName) {
        if(sourceFile == null
                ||sourceClass == null
                || destClass == null)
            return;
        refactor.replaceInheritanceWithDelegationFor(sourceFile, sourceClass, chosenStatements, destClass, newFieldName, newClassName);
        try {
            refactor.parseFiles(filesInProjectDirectory);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.exit(0);
        }
        refactor.analyze();
    }

    public Representation getClassOrInterfaceFor(String chosenSourceCorI, String chosenDestCorI, String chosenSourceFile, String chosenRefactor) {
        return refactor.getBaseOrSubClassOrInterfaceFor(chosenSourceCorI, chosenDestCorI, chosenSourceFile, chosenRefactor);
    }
}
