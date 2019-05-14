package ParserTestSuite;

import ParserTestSuite.ClassesAndInterfacesTests.ClassesAndInterfacesTests;
import ParserTestSuite.ErrorsTests.ErrorsTests;
import ParserTestSuite.MethodStatementsTests.MethodStatementsTests;
import ParserTestSuite.StatementsTests.StatementsTests;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;


@RunWith(Suite.class)
@Suite.SuiteClasses({
        ClassesAndInterfacesTests.class,
        StatementsTests.class,
        MethodStatementsTests.class,
        ErrorsTests.class
})
public class ParserTests {
}
