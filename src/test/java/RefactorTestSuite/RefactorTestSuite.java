package RefactorTestSuite;

import RefactorTestSuite.DelegateTests.DelegateTests;
import RefactorTestSuite.PullUpTests.PullUpTests;
import RefactorTestSuite.PushDownTests.PushDownTests;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        DelegateTests.class,
        PullUpTests.class,
        PushDownTests.class
})
public class RefactorTestSuite {
}
