package ParserTestSuite.ErrorsTests;

import Exceptions.ParsingException;
import ParserTestSuite.CommonResources.SingleTestConductor;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.AbstractMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.fail;

public class ErrorsTests {
    private final static String TEST_FILES_DIRECTORY = "src/test/java/ParserTestSuite/ErrorsTests/";

    private final static Map<String, String> testFiles = Map.ofEntries(
            new AbstractMap.SimpleEntry<>(TEST_FILES_DIRECTORY + "testFiles/shouldFailOnMoreThanOnePublicClass.txt", TEST_FILES_DIRECTORY+ "validation/shouldFailOnMoreThanOnePublicClassValidation.txt")
    );

    @Test
    public void testStatementsParsingWithErrors()
    {
        testFiles.forEach((validated, expected) ->
        {
            try {
                SingleTestConductor.compareParserExceptionMessage(validated, expected);
            } catch (IOException e) {
                e.printStackTrace();
                fail("Problem with file."+e.getMessage());
            }
        });
    }
}
