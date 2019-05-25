package RefactorTestSuite.DelegateTests;

import model.modelClass.Model;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class DelegateTests {

    private final static String TEST_FILES = "src\\test\\java\\RefactorTestSuite\\DelegateTests\\testFiles\\";
    private final static String VALIDATION_FILES = "src\\test\\java\\RefactorTestSuite\\DelegateTests\\validation\\";
    private final static String ORIGINAL_FILES = "src\\test\\java\\RefactorTestSuite\\DelegateTests\\originalFiles\\";
    private static Model model;

    @Test
    public void shouldDelegateWithoutAnyStatement() {
        model = new Model(TEST_FILES);
        model.doDelegate("package1\\Class1.txt", "Class1", new ArrayList<>(),
                "BaseClass", "newClass", "NewClass");

        File validationFile = new File(VALIDATION_FILES + "shouldDelegateWithoutAnyStatement.txt");
        File validatedFile = new File(TEST_FILES + "package1\\Class1.txt");
        assertThat(validationFile).hasSameContentAs(validatedFile);
    }

    @Test
    public void shouldDelegateStatements()
    {
        model = new Model(TEST_FILES);
        List<Integer> toDelegate = new ArrayList<>();
        toDelegate.add(0);
        toDelegate.add(1);
        model.doDelegate("package1\\Class2.txt", "Class2", toDelegate,
                "BaseClass", "newClass", "NewClass");

        File validationFile = new File(VALIDATION_FILES + "shouldDelegateStatements.txt");
        File validatedFile = new File(TEST_FILES + "package1\\Class2.txt");
        assertThat(validationFile).hasSameContentAs(validatedFile);
    }

    @Test
    public void shouldDelegateOverriddenMethods()
    {
        model = new Model(TEST_FILES);
        model.doDelegate("package1\\Class3.txt", "Class3", new ArrayList<>(),
                "BaseClass", "newClass", "NewClass");

        File validationFile = new File(VALIDATION_FILES + "shouldDelegateOverriddenMethods.txt");
        File validatedFile = new File(TEST_FILES + "package1\\Class3.txt");
        assertThat(validationFile).hasSameContentAs(validatedFile);
    }

    @Test
    public void shouldRecognizeThatOneOfCheckedStatementIsOverridden()
    {
        model = new Model(TEST_FILES);
        List<Integer> toDelegate = new ArrayList<>();
        toDelegate.add(0);
        model.doDelegate("package1\\Class3.txt", "Class3", new ArrayList<>(),   //the same file, but now we choose
                "BaseClass", "newClass", "NewClass");             //the statement which either way would
                                                                                                    //be delegated
        File validationFile = new File(VALIDATION_FILES + "shouldDelegateOverriddenMethods.txt");
        File validatedFile = new File(TEST_FILES + "package1\\Class3.txt");
        assertThat(validationFile).hasSameContentAs(validatedFile);
    }
    @After
    public void cleanup() throws IOException {
        FileUtils.copyDirectory(new File(ORIGINAL_FILES), new File(TEST_FILES));
    }
}
