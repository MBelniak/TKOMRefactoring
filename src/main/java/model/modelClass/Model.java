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
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Model {
    private static final Scanner scanner = new Scanner();
    private static final Parser parser = new Parser(scanner);
    private static final Refactor refactor = new Refactor(scanner, parser);
    private static final String PROJECT_DIRECTORY = "src\\main\\resources\\projectFiles\\";
    private List<String> filesInProjectDirectory;

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
            if(i==statements.size()-1)
                return;
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

    public List<String> getRefactors() {
        return refactor.getRefactorings();
    }


    public List<String> getBasesFor(String file, String chosenClass) {
        file = file.replace(".", "\\").substring(0, file.length()-4).concat("." + Refactor.FILE_EXTENSION);
        List<String> chosenClassPath = new ArrayList<>(Arrays.asList(chosenClass.split("\\.")));
        Representation representation = refactor.findClassOrInterfaceInFile(file, chosenClassPath);
        if(representation==null)
            return new ArrayList<>();
        return representation.getBases();
    }
}
