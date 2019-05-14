package ParserTestSuite.StatementsTests;

import Exceptions.ParsingException;
import ParserTestSuite.CommonResources.SingleTestConductor;
import org.junit.Test;

import java.io.IOException;
import java.util.AbstractMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.fail;

public class StatementsTests {

    private final static String TEST_FILES_DIRECTORY = "src/test/java/ParserTestSuite/StatementsTests/";

    private final static Map<String, String> testFiles = Map.ofEntries(
            new AbstractMap.SimpleEntry<>(TEST_FILES_DIRECTORY + "testFiles/shouldParseObjectFieldDeclaration.txt", TEST_FILES_DIRECTORY+ "validation/shouldParseObjectFieldDeclarationValidation.txt"),
            new AbstractMap.SimpleEntry<>(TEST_FILES_DIRECTORY + "testFiles/shouldParseConstructorDefinition.txt", TEST_FILES_DIRECTORY+ "validation/shouldParseConstructorDefinitionValidation.txt"),
            new AbstractMap.SimpleEntry<>(TEST_FILES_DIRECTORY + "testFiles/shouldParseDifferentAccessModifiers.txt", TEST_FILES_DIRECTORY+ "validation/shouldParseDifferentAccessModifiersValidation.txt"),
            new AbstractMap.SimpleEntry<>(TEST_FILES_DIRECTORY + "testFiles/shouldParseEveryKindOfStatement.txt", TEST_FILES_DIRECTORY+ "validation/shouldParseEveryKindOfStatementValidation.txt")
            );

    @Test
    public void testStatementsParsing()
    {
        testFiles.forEach((validated, expected) ->
        {
            try {
                SingleTestConductor.checkFileParsing(validated, expected);
            } catch (ParsingException e) {
                fail("Parsing Failed");
            } catch (IOException e) {
                e.printStackTrace();
                fail("Problem with file."+e.getMessage());
            }
        });
    }
}
