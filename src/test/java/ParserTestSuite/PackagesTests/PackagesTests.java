package ParserTestSuite.PackagesTests;

import Exceptions.ParsingException;
import ParserTestSuite.CommonResources.SingleTestConductor;
import org.junit.Test;

import java.io.IOException;
import java.util.AbstractMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.fail;

public class PackagesTests {
    private final static String TEST_FILES_DIRECTORY = "src/test/java/ParserTestSuite/PackagesTests/";

    private final static Map<String, String> testFiles = Map.ofEntries(
            new AbstractMap.SimpleEntry<>(TEST_FILES_DIRECTORY + "testFiles/shouldParsePackageAndImportDeclarations.txt", TEST_FILES_DIRECTORY+ "validation/shouldParsePackageAndImportDeclarationsValidation.txt")
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
