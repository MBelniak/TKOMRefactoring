package ParserTestSuite.CommonResources;

import model.exceptions.ParsingException;
import model.parser.Parser;
import model.scanner.Scanner;

import java.io.*;

import static org.assertj.core.api.Assertions.assertThat;

public class SingleTestConductor {

    private static final Scanner scanner = new Scanner();
    private static final Parser parser = new Parser(scanner);
    private static final File validatedFile = new File("src/main/resources/outputs/ASTOutput.txt");
    private static BufferedReader bf;

    public static void checkFileParsing(String validated, String expected) throws ParsingException, IOException {
        File fileSource = new File(validated);
        scanner.bindFile(fileSource);
        parser.parseFile();
        File expectedOutput = new File(expected);
        assertThat(validatedFile).hasSameContentAs(expectedOutput);
    }

    public static void compareParserExceptionMessage(String validated, String expected) throws IOException {
        File fileSource = new File(validated);
        scanner.bindFile(fileSource);
        try {
            parser.parseFile();
        } catch (ParsingException e) {
            bf = new BufferedReader(new FileReader(new File(expected)));
            assertThat(e.getErrorMessage() + " Line: " + e.getLine() + " Column: " + e.getColumn()).isEqualTo(bf.readLine());
        }
    }
}
