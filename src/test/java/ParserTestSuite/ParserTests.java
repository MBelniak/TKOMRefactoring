package ParserTestSuite;

import Exceptions.ParsingException;
import Parser.Parser;
import Scanner.Scanner;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.AbstractMap;
import java.util.Map;


import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;


public class ParserTests {
    private Scanner scanner;
    private Parser parser;
    private final static String CLASSES_AND_INTERFACE_TEST_FILES_DIRECTORY = "src/test/java/ParserTestSuite/ClassesAndInterfacesTests/";
    private final static String ERRORS_TEST_FILES_DIRECTORY = "src/test/java/ParserTestSuite/ErrorsTests/";
    private final static String PACKAGES_TEST_FILES_DIRECTORY = "src/test/java/ParserTestSuite/PackagesTests/";
    private File validatedFile;
    private final static Map<String, String> classesAndInterfaceTestFiles = Map.ofEntries(
            new AbstractMap.SimpleEntry<>(CLASSES_AND_INTERFACE_TEST_FILES_DIRECTORY + "testFiles/shouldParseSimpleClassHeader.txt", CLASSES_AND_INTERFACE_TEST_FILES_DIRECTORY +"validation/shouldParseSimpleClassHeaderValidation.txt"),
            new AbstractMap.SimpleEntry<>(CLASSES_AND_INTERFACE_TEST_FILES_DIRECTORY + "testFiles/shouldParseAbstractClassHeader.txt", CLASSES_AND_INTERFACE_TEST_FILES_DIRECTORY +"validation/shouldParseAbstractClassHeaderValidation.txt"),
            new AbstractMap.SimpleEntry<>(CLASSES_AND_INTERFACE_TEST_FILES_DIRECTORY + "testFiles/shouldParseClassHeaderWithExtendsAndImplements.txt", CLASSES_AND_INTERFACE_TEST_FILES_DIRECTORY +"validation/shouldParseClassHeaderWithExtendsAndImplementsValidation.txt"),
            new AbstractMap.SimpleEntry<>(CLASSES_AND_INTERFACE_TEST_FILES_DIRECTORY + "testFiles/shouldParseInnerClasses.txt", CLASSES_AND_INTERFACE_TEST_FILES_DIRECTORY +"validation/shouldParseInnerClassesValidation.txt"),
            new AbstractMap.SimpleEntry<>(CLASSES_AND_INTERFACE_TEST_FILES_DIRECTORY + "testFiles/shouldParseSimpleInterfaceHeader.txt", CLASSES_AND_INTERFACE_TEST_FILES_DIRECTORY +"validation/shouldParseSimpleInterfaceHeaderValidation.txt"),
            new AbstractMap.SimpleEntry<>(CLASSES_AND_INTERFACE_TEST_FILES_DIRECTORY + "testFiles/shouldParseInterfaceWithExtends.txt", CLASSES_AND_INTERFACE_TEST_FILES_DIRECTORY +"validation/shouldParseInterfaceWithExtendsValidation.txt")
    );

    private final static Map<String, String> errorsTestFiles = Map.ofEntries(
            new AbstractMap.SimpleEntry<>(ERRORS_TEST_FILES_DIRECTORY + "testFiles/shouldParseSimpleClassHeader.txt", ERRORS_TEST_FILES_DIRECTORY +"validation/shouldParseSimpleClassHeaderValidation.txt")
    );

    private final static Map<String, String> packagessTestFiles = Map.ofEntries(
            new AbstractMap.SimpleEntry<>(PACKAGES_TEST_FILES_DIRECTORY + "testFiles/shouldParsePackageAndImportDeclarations.txt", PACKAGES_TEST_FILES_DIRECTORY +"validation/shouldParsePackageAndImportDeclarationsValidation.txt")
    );

    public ParserTests() {
        this.scanner = new Scanner();
        this.parser = new Parser(scanner);
        this.validatedFile = new File("src/main/resources/outputs/ASTOutput.txt");
    }

    @Test
    public void testClassesAndInterfacesParsing()
    {
        classesAndInterfaceTestFiles.forEach((validated, expected) ->
        {
            try {
                checkFileParsing(validated, expected);
            } catch (ParsingException e) {
                fail("Parsing Failed");
            } catch (IOException e) {
                e.printStackTrace();
                fail("Problem with file."+e.getMessage());
            }
        });
    }

    @Test
    @Ignore
    public void testErrors()
    {
        errorsTestFiles.forEach((validated, expected) ->
        {
            try {
                checkFileParsing(validated, expected);
            } catch (ParsingException e) {
                fail("Parsing Failed");
            } catch (IOException e) {
                e.printStackTrace();
                fail("Problem with file."+e.getMessage());
            }
        });
    }

    @Test
    public void testPackagesParsing()
    {
        packagessTestFiles.forEach((validated, expected) ->
        {
            try {
                checkFileParsing(validated, expected);
            } catch (ParsingException e) {
                fail("Parsing Failed");
            } catch (IOException e) {
                e.printStackTrace();
                fail("Problem with file."+e.getMessage());
            }
        });
    }

    private void checkFileParsing(String validated, String expected) throws ParsingException, IOException {
        File fileSource = new File(validated);
        scanner.bindFile(fileSource);
        parser.parseFile();
        File expectedOutput = new File(expected);
        assertThat(validatedFile).hasSameContentAs(expectedOutput);
    }
}
