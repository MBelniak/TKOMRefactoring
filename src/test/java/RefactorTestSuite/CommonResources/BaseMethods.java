package RefactorTestSuite.CommonResources;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BaseMethods {

    public List<String> getFilePathsInDirectory(String directory)
    {
        List<String> filesInProjectDirectory = new ArrayList<>();
        try (Stream<Path> walk = Files.walk(Paths.get(directory))) {

            filesInProjectDirectory.addAll(walk.map(Path::toString)
                    .filter(f -> f.endsWith(".txt")).collect(Collectors.toList()));
        } catch (IOException e) {
            e.printStackTrace();
            return filesInProjectDirectory;
        }
        return filesInProjectDirectory;
    }
}
