import FilesManagement.FileSource;
import Parser.Parser;
import Scanner.*;


public class Main {

    public static void main(String[] args)
    {
        Scanner scanner = new Scanner();
        FileSource fileSource = new FileSource("src/main/resources/test3.txt");
        scanner.bindFileSource(fileSource);
        Parser parser = new Parser(scanner);
        parser.parseFile();
    }
}
