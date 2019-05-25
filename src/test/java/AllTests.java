import OneLexem.OneLexemTestCase;
import ParserTestSuite.ParserTests;
import RefactorTestSuite.RefactorTestSuite;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        RefactorTestSuite.class,
        ParserTests.class,
        OneLexemTestCase.class
})
public class AllTests {
}
