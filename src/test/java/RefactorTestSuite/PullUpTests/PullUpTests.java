package RefactorTestSuite.PullUpTests;

import model.modelClass.Model;
import model.refactor.Refactor;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Test;

import java.io.*;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;

public class PullUpTests {

    private final static String TEST_FILES = "src\\test\\java\\RefactorTestSuite\\PullUpTests\\testFiles\\";
    private final static String VALIDATION_FILES = "src\\test\\java\\RefactorTestSuite\\PullUpTests\\validation\\";
    private final static String ORIGINAL_FILES = "src\\test\\java\\RefactorTestSuite\\PullUpTests\\originalFiles\\";
    private final static String TEST_OUTPUTS = "src\\test\\java\\RefactorTestSuite\\PullUpTests\\testOutputs\\";
    private static Model model;

    @Test
    public void shouldPullUpToClassInTheSameFile() throws IOException {
        model = new Model(TEST_FILES);
        List<Integer> statementsToMove = new ArrayList<>();
        statementsToMove.add(0);
        statementsToMove.add(1);
        statementsToMove.add(2);
        model.doPushOrPull("package1\\Class1.txt",
                           "Class1", statementsToMove,
                            "Class1Base", Refactor.PULL_UP);
        File validationFile = new File(VALIDATION_FILES + "shouldPullUpToClassInTheSameFile.txt");
        File validatedFile = new File(TEST_FILES + "package1\\Class1.txt");

        FileChannel src = new FileInputStream(validatedFile).getChannel();
        FileChannel dest = new FileOutputStream(new File(TEST_OUTPUTS + "shouldPullUpToClassInTheSameFile.txt")).getChannel();
        dest.transferFrom(src, 0, src.size());
        src.close();
        dest.close();

        assertThat(validationFile).hasSameContentAs(validatedFile);
    }

    @Test
    public void shouldPullUpToClassInOtherFileSamePackage() throws IOException {
        model= new Model(TEST_FILES);
        List<Integer> statementsToMove = new ArrayList<>();
        statementsToMove.add(0);
        statementsToMove.add(1);
        statementsToMove.add(2);
        statementsToMove.add(3);
        model.doPushOrPull("package1\\Class2.txt",
                "Class2", statementsToMove,
                "Class3", Refactor.PULL_UP);
        File validationFile1 = new File(VALIDATION_FILES + "shouldPullUpToClassInOtherFileSamePackageSource.txt");
        File validatedFile1 = new File(TEST_FILES + "package1\\Class2.txt");

        FileChannel src = new FileInputStream(validatedFile1).getChannel();
        FileChannel dest = new FileOutputStream(new File(TEST_OUTPUTS + "shouldPullUpToClassInOtherFileSamePackageSource.txt")).getChannel();
        dest.transferFrom(src, 0, src.size());
        src.close();
        dest.close();

        assertThat(validationFile1).hasSameContentAs(validatedFile1);

        File validationFile2 = new File(VALIDATION_FILES + "shouldPullUpToClassInOtherFileSamePackageDest.txt");
        File validatedFile2 = new File(TEST_FILES + "package1\\Class3.txt");

        src = new FileInputStream(validatedFile2).getChannel();
        dest = new FileOutputStream(new File(TEST_OUTPUTS + "shouldPullUpToClassInOtherFileSamePackageDest.txt")).getChannel();
        dest.transferFrom(src, 0, src.size());
        src.close();
        dest.close();

        assertThat(validationFile2).hasSameContentAs(validatedFile2);
    }
    @Test
    public void shouldPullUpToClassInOtherPackage() throws IOException {
        model = new Model(TEST_FILES);
        List<Integer> statementsToMove = new ArrayList<>();
        statementsToMove.add(0);
        statementsToMove.add(1);
        statementsToMove.add(2);
        model.doPushOrPull("package1\\Class4.txt",
                "Class4", statementsToMove,
                "Class5", Refactor.PULL_UP);
        File validationFile1 = new File(VALIDATION_FILES + "shouldPullUpToClassInOtherPackageSource.txt");
        File validatedFile1 = new File(TEST_FILES + "package1\\Class4.txt");

        FileChannel src = new FileInputStream(validatedFile1).getChannel();
        FileChannel dest = new FileOutputStream(new File(TEST_OUTPUTS + "shouldPullUpToClassInOtherPackageSource.txt")).getChannel();
        dest.transferFrom(src, 0, src.size());
        src.close();
        dest.close();

        assertThat(validationFile1).hasSameContentAs(validatedFile1);

        File validationFile2 = new File(VALIDATION_FILES + "shouldPullUpToClassInOtherPackageDest.txt");
        File validatedFile2 = new File(TEST_FILES + "package2\\Class5.txt");

        src = new FileInputStream(validatedFile2).getChannel();
        dest = new FileOutputStream(new File(TEST_OUTPUTS + "shouldPullUpToClassInOtherPackageDest.txt")).getChannel();
        dest.transferFrom(src, 0, src.size());
        src.close();
        dest.close();

        assertThat(validationFile2).hasSameContentAs(validatedFile2);
    }
    @Test
    public void shouldPullUpToInterfaceInTheSameFile() throws IOException {
        model = new Model(TEST_FILES);
        List<Integer> statementsToMove = new ArrayList<>();
        statementsToMove.add(0);
        statementsToMove.add(1);
        statementsToMove.add(2);
        model.doPushOrPull("interfacePackage1\\Interface1.txt",
                "Interface1", statementsToMove,
                "BaseInterface", Refactor.PULL_UP);
        File validationFile = new File(VALIDATION_FILES + "shouldPullUpToInterfaceInTheSameFile.txt");
        File validatedFile = new File(TEST_FILES + "interfacePackage1\\Interface1.txt");

        FileChannel src = new FileInputStream(validatedFile).getChannel();
        FileChannel dest = new FileOutputStream(new File(TEST_OUTPUTS + "shouldPullUpToInterfaceInTheSameFile.txt")).getChannel();
        dest.transferFrom(src, 0, src.size());
        src.close();
        dest.close();

        assertThat(validationFile).hasSameContentAs(validatedFile);
    }
    @Test
    public void shouldPullUpToInterfaceInOtherFileSamePackage() throws IOException {
        model= new Model(TEST_FILES);
        List<Integer> statementsToMove = new ArrayList<>();
        statementsToMove.add(0);
        statementsToMove.add(1);
        model.doPushOrPull("interfacePackage1\\Interface2.txt",
                "Interface2", statementsToMove,
                "Interface3", Refactor.PULL_UP);
        File validationFile1 = new File(VALIDATION_FILES + "shouldPullUpToInterfaceInOtherFileSamePackageSource.txt");
        File validatedFile1 = new File(TEST_FILES + "interfacePackage1\\Interface2.txt");

        FileChannel src = new FileInputStream(validatedFile1).getChannel();
        FileChannel dest = new FileOutputStream(new File(TEST_OUTPUTS + "shouldPullUpToInterfaceInOtherFileSamePackageSource.txt")).getChannel();
        dest.transferFrom(src, 0, src.size());
        src.close();
        dest.close();

        assertThat(validationFile1).hasSameContentAs(validatedFile1);

        File validationFile2 = new File(VALIDATION_FILES + "shouldPullUpToInterfaceInOtherFileSamePackageDest.txt");
        File validatedFile2 = new File(TEST_FILES + "interfacePackage1\\Interface3.txt");

        src = new FileInputStream(validatedFile2).getChannel();
        dest = new FileOutputStream(new File(TEST_OUTPUTS + "shouldPullUpToInterfaceInOtherFileSamePackageDest.txt")).getChannel();
        dest.transferFrom(src, 0, src.size());
        src.close();
        dest.close();

        assertThat(validationFile2).hasSameContentAs(validatedFile2);
    }
    @Test
    public void shouldPullUpToInterfaceInOtherPackage() throws IOException {
        model= new Model(TEST_FILES);
        List<Integer> statementsToMove = new ArrayList<>();
        statementsToMove.add(1);
        model.doPushOrPull("interfacePackage1\\Interface4.txt",
                "Interface4", statementsToMove,
                "Interface5", Refactor.PULL_UP);
        File validationFile1 = new File(VALIDATION_FILES + "shouldPullUpToInterfaceInOtherPackageSource.txt");
        File validatedFile1 = new File(TEST_FILES + "interfacePackage1\\Interface4.txt");

        FileChannel src = new FileInputStream(validatedFile1).getChannel();
        FileChannel dest = new FileOutputStream(new File(TEST_OUTPUTS + "shouldPullUpToInterfaceInOtherPackageSource.txt")).getChannel();
        dest.transferFrom(src, 0, src.size());
        src.close();
        dest.close();

        assertThat(validationFile1).hasSameContentAs(validatedFile1);

        File validationFile2 = new File(VALIDATION_FILES + "shouldPullUpToInterfaceInOtherPackageDest.txt");
        File validatedFile2 = new File(TEST_FILES + "interfacePackage2\\Interface5.txt");

        src = new FileInputStream(validatedFile2).getChannel();
        dest = new FileOutputStream(new File(TEST_OUTPUTS + "shouldPullUpToInterfaceInOtherPackageDest.txt")).getChannel();
        dest.transferFrom(src, 0, src.size());
        src.close();
        dest.close();

        assertThat(validationFile2).hasSameContentAs(validatedFile2);
    }

    @Test
    public void shouldPullUpToOuterClass() throws IOException {
        model = new Model(TEST_FILES);
        List<Integer> statementsToMove = new ArrayList<>();
        statementsToMove.add(0);
        model.doPushOrPull("package2\\Class6.txt",
                "Class6.Class7.Class8", statementsToMove,
                "Class6", Refactor.PULL_UP);
        File validationFile = new File(VALIDATION_FILES + "shouldPullUpToOuterClass.txt");
        File validatedFile = new File(TEST_FILES + "package2\\Class6.txt");

        FileChannel src = new FileInputStream(validatedFile).getChannel();
        FileChannel dest = new FileOutputStream(new File(TEST_OUTPUTS + "shouldPullUpToOuterClass.txt")).getChannel();
        dest.transferFrom(src, 0, src.size());
        src.close();
        dest.close();

        assertThat(validationFile).hasSameContentAs(validatedFile);
    }

    @Test
    public void shouldPullUpFromClassToInterface() throws IOException {
        model = new Model(TEST_FILES);
        List<Integer> statementsToMove = new ArrayList<>();
        statementsToMove.add(0);
        model.doPushOrPull("package2\\Class9.txt",
                "Class9", statementsToMove,
                "Interface9", Refactor.PULL_UP);
        File validationFile = new File(VALIDATION_FILES + "shouldPullUpFromClassToInterface.txt");
        File validatedFile = new File(TEST_FILES + "package2\\Class9.txt");

        FileChannel src = new FileInputStream(validatedFile).getChannel();
        FileChannel dest = new FileOutputStream(new File(TEST_OUTPUTS + "shouldPullUpFromClassToInterface.txt")).getChannel();
        dest.transferFrom(src, 0, src.size());
        src.close();
        dest.close();

        assertThat(validationFile).hasSameContentAs(validatedFile);
    }

    @After
    public void cleanup() throws IOException {
        FileUtils.copyDirectory(new File(ORIGINAL_FILES), new File(TEST_FILES));
    }

}
