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
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Model {
    private static final Scanner scanner = new Scanner();
    private static final Parser parser = new Parser(scanner);
    private static final Refactor refactor = new Refactor(scanner, parser);
    private List<String> filesInProjectDirectory;

    public Model()
    {
        filesInProjectDirectory  = new ArrayList<>();
        try (Stream<Path> walk = Files.walk(Paths.get("src/main/resources/projectFiles"))) {

            filesInProjectDirectory = walk.map(Path::toString)
                    .filter(f -> f.endsWith(".txt")).collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            refactor.parseFiles(filesInProjectDirectory);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return;
        }
        refactor.analyze();
    }

    public static Refactor getRefactor() {
        return refactor;
    }

    public List<String> getFilesInProjectDirectory() {
        List<String> files = new ArrayList<>();
        files.addAll(filesInProjectDirectory.stream().map(string -> string.substring(32, string.length())).collect(Collectors.toList()));
        return files;
    }

    public List<Representation> getRepresentationsInFile(String fileName)
    {
        return refactor.getClassesAndInterfacesInFile(fileName);
    }

    public List<Statement> getStatementsInFileInClass(String fileName, String className) {
        //TODO
        return null;
    }
}
