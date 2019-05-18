package ParserTestSuite.ClassesAndInterfacesTests;

import model.exceptions.ParsingException;
import ParserTestSuite.CommonResources.SingleTestConductor;
import org.junit.Test;

import java.io.IOException;
import java.util.AbstractMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.fail;

public class ClassesAndInterfacesTests {
    private final static String TEST_FILES_DIRECTORY = "src/test/java/ParserTestSuite/ClassesAndInterfacesTests/";

    private final static Map<String, String> testFiles = Map.ofEntries(
            new AbstractMap.SimpleEntry<>(TEST_FILES_DIRECTORY + "testFiles/shouldParseSimpleClassHeader.txt", TEST_FILES_DIRECTORY +"validation/shouldParseSimpleClassHeaderValidation.txt"),
            new AbstractMap.SimpleEntry<>(TEST_FILES_DIRECTORY + "testFiles/shouldParseAbstractClassHeader.txt", TEST_FILES_DIRECTORY +"validation/shouldParseAbstractClassHeaderValidation.txt"),
            new AbstractMap.SimpleEntry<>(TEST_FILES_DIRECTORY + "testFiles/shouldParseClassHeaderWithExtendsAndImplements.txt", TEST_FILES_DIRECTORY +"validation/shouldParseClassHeaderWithExtendsAndImplementsValidation.txt"),
            new AbstractMap.SimpleEntry<>(TEST_FILES_DIRECTORY + "testFiles/shouldParseInnerClasses.txt", TEST_FILES_DIRECTORY +"validation/shouldParseInnerClassesValidation.txt"),
            new AbstractMap.SimpleEntry<>(TEST_FILES_DIRECTORY + "testFiles/shouldParseSimpleInterfaceHeader.txt", TEST_FILES_DIRECTORY +"validation/shouldParseSimpleInterfaceHeaderValidation.txt"),
            new AbstractMap.SimpleEntry<>(TEST_FILES_DIRECTORY + "testFiles/shouldParseInterfaceWithExtends.txt", TEST_FILES_DIRECTORY +"validation/shouldParseInterfaceWithExtendsValidation.txt")
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
