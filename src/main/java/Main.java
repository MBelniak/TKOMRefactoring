import Exceptions.ParsingException;
import Parser.Parser;
import Scanner.Scanner;

import java.io.File;
import java.io.FileNotFoundException;


public class Main {

    public static void main(String[] args)
    {
        Scanner scanner = new Scanner();
        File fileSource = new File("src/main/resources/test.txt");
        try {
            scanner.bindFile(fileSource);
        } catch (FileNotFoundException e) {
            System.out.println("File not found.");
            return;
        }
        Parser parser = new Parser(scanner);
        try {
            parser.parseFile();
            System.out.println(parser.getAST().toString());
            System.out.println("Parsing successful");
        } catch (ParsingException e) {
            System.out.println(e.getErrorMessage() + " Line: " + e.getLine() + " Column: " + e.getColumn());
        }

    }
}
