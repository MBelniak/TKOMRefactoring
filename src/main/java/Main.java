import Exceptions.ParsingException;
import FilesManagement.FileSource;
import Parser.Parser;
import Scanner.*;


public class Main {

    public static void main(String[] args)
    {
        Scanner scanner = new Scanner();
        FileSource fileSource = new FileSource("src/main/resources/test.txt");
        scanner.bindFileSource(fileSource);
        Parser parser = new Parser(scanner);
        try {
            parser.parseFile();
            System.out.println("Parsing successful");
        } catch (ParsingException e) {
            System.out.println(e.getErrorMessage() + " Line: " + e.getLine() + " Column: " + e.getColumn());
        }

    }
}
