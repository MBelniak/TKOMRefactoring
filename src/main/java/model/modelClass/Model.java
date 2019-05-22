package model.modelClass;

import model.parser.Parser;
import model.refactor.Refactor;
import model.refactor.Representation;
import model.refactor.Statement;
import model.scanner.Scanner;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Model {
    private static final Scanner scanner = new Scanner();
    private static final Parser parser = new Parser(scanner);
    private static final Refactor refactor = new Refactor(scanner, parser);
    private static final String PROJECT_DIRECTORY = "src\\main\\resources\\projectFiles\\";
    private List<String> filesInProjectDirectory;
    public static final String PULL_UP = "Pull up";
    public static final String PUSH_DOWN = "Push down";
    public static final String DELEGATE = "Inheritance to delegation";

    public Model()
    {
        filesInProjectDirectory  = new ArrayList<>();

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

    public static Refactor getRefactor() {
        return refactor;
    }

    public List<String> getFilesInProjectDirectory() {
        return filesInProjectDirectory.stream().map(string -> string.substring(32, string.length())).collect(Collectors.toList());
    }

    public List<Representation> getRepresentationsInFile(String fileName)
    {
        return refactor.getClassesAndInterfacesInFile(fileName);
    }

    public List<Statement> getStatementsInFileInClass(String fileName, String className) {
        if(className == null || className.length()==0)
            return null;
        List<String> classPath = new ArrayList<>(Arrays.asList(className.split("\\.")));
        Representation representation = refactor.findClassOrInterfaceInFile(fileName, classPath);
        if(representation==null)
            return null;
        return representation.getStatements();
    }

    public void doPullUp(String fileName, String className, List<Integer> statements, String destClassName)
    {
        List<String> classPath = new ArrayList<>(Arrays.asList(className.split("\\.")));
        for(int i = 0; i<statements.size(); i++) {
            refactor.pullUpMember(fileName, classPath, statements.get(i), destClassName);
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

    private List<String> getBasesFor(String file, String chosenClass) {
        if(file == null || !file.contains("."))
            return new ArrayList<>();
        file = file.replace(".", "\\").substring(0, file.length()-4).concat("." + Refactor.FILE_EXTENSION);
        List<String> chosenClassPath = new ArrayList<>(Arrays.asList(chosenClass.split("\\.")));
        Representation representation = refactor.findClassOrInterfaceInFile(file, chosenClassPath);
        if(representation==null)
            return new ArrayList<>();
        return representation.getBases();
    }

    public List<String> checkForPullUpWarnings(String fileName, String className, List<Integer> statements, String destClassName) {

        List<String> warningList = new ArrayList<>(getDuplicationWarnings(fileName, className, statements, destClassName));
        warningList.addAll(getOtherWarnigns(fileName, className, statements, destClassName));

        return warningList;
    }

    private List<String> getOtherWarnigns(String fileName, String className, List<Integer> statements, String destClassName) {
        return new ArrayList<>(); // todo
    }

    private List<String> getDuplicationWarnings(String fileName, String className, List<Integer> statements, String destClassName) {
        List<String> warningList = new ArrayList<>();

        List<String> classPath = new ArrayList<>(Arrays.asList(className.split("\\.")));
        Representation sourceRepresentation = refactor.findClassOrInterfaceInFile(fileName, classPath);
        Representation destRepresenation = sourceRepresentation.getBaseByName(destClassName);

        for(int i = 0; i<statements.size(); i++)
        {
            Statement statement = sourceRepresentation.getStatements().get(statements.get(i));
            if(destRepresenation.checkIfSameStatementExists(statement, 1)) //exists
            {
                warningList.add(statement.getCategory() + " declared as "
                        + statement.getStatementSignature() + " already exists in class/interface "+ destRepresenation.getName());
            }
        }
        
        return warningList;
    }

    public List<String> getDestClassesFor(String file, String chosenClass, String chosenRefactor) {
        if(chosenRefactor==null)
            return new ArrayList<>();
        switch (chosenRefactor)
        {
            case (PULL_UP):
            {
                return getBasesFor(file, chosenClass);
            }
            case (PUSH_DOWN):
            {

            }
            case (DELEGATE):
            {

            }
            default:
                return new ArrayList<>();
        }
    }

    public List<String> getRefactors() {
        List<String> refactors = new ArrayList<>();
        refactors.add(PULL_UP);
        refactors.add(PUSH_DOWN);
        refactors.add(DELEGATE);
        return refactors;
    }

    public String getDestFileNameForClasses(String sourceClass, String baseClass, String sourceFile)
    {
        if(sourceFile == null || !sourceFile.contains("."))
            return "";
        sourceFile = sourceFile.replace(".", "\\").substring(0, sourceFile.length()-4).concat("." + Refactor.FILE_EXTENSION);
        List<String> chosenClassPath = new ArrayList<>(Arrays.asList(sourceClass.split("\\.")));
        Representation representation = refactor.findClassOrInterfaceInFile(sourceFile, chosenClassPath);
        Representation dest = representation.getBaseByName(baseClass);
        return dest.getFilePath();
    }
}
