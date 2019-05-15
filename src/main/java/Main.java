import Exceptions.ParsingException;
import Parser.Parser;
import Refactor.Refactor;
import Scanner.Scanner;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class Main {

    public static void main(String[] args) throws FileNotFoundException {
        Scanner scanner = new Scanner();
        Parser parser = new Parser(scanner);
        Refactor refactor = new Refactor(scanner, parser);

        List<String> filesInProjectDirectory = new ArrayList<>();
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
        refactor.analyzeClassesAndInterfaces();

    }
}
