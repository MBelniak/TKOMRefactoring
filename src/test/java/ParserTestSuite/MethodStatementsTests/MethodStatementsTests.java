package ParserTestSuite.MethodStatementsTests;

import model.exceptions.ParsingException;
import ParserTestSuite.CommonResources.SingleTestConductor;
import org.junit.Test;

import java.io.IOException;
import java.util.AbstractMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.fail;

public class MethodStatementsTests {
    private final static String TEST_FILES_DIRECTORY = "src/test/java/ParserTestSuite/MethodStatementsTests/";

    private final static Map<String, String> testFiles = Map.ofEntries(
            new AbstractMap.SimpleEntry<>(TEST_FILES_DIRECTORY + "testFiles/shouldParseMethodHeaders.txt", TEST_FILES_DIRECTORY+ "validation/shouldParseMethodHeadersValidation.txt"),
            new AbstractMap.SimpleEntry<>(TEST_FILES_DIRECTORY + "testFiles/constructorShouldBeAbleToCallSuper.txt", TEST_FILES_DIRECTORY+ "validation/constructorShouldBeAbleToCallSuperValidation.txt"),
            new AbstractMap.SimpleEntry<>(TEST_FILES_DIRECTORY + "testFiles/shouldParseLocalVariableDeclaration.txt", TEST_FILES_DIRECTORY+ "validation/shouldParseLocalVariableDeclarationValidation.txt"),
            new AbstractMap.SimpleEntry<>(TEST_FILES_DIRECTORY + "testFiles/shouldParseLocalVariableInitialization.txt", TEST_FILES_DIRECTORY+ "validation/shouldParseLocalVariableInitializationValidation.txt"),
            new AbstractMap.SimpleEntry<>(TEST_FILES_DIRECTORY + "testFiles/shouldAcceptThisKeywordInStatement.txt", TEST_FILES_DIRECTORY+ "validation/shouldAcceptThisKeywordInStatementValidation.txt"),
            new AbstractMap.SimpleEntry<>(TEST_FILES_DIRECTORY + "testFiles/shouldParseFetchingObjectsAndCalls.txt", TEST_FILES_DIRECTORY+ "validation/shouldParseFetchingObjectsAndCallsValidation.txt")
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
