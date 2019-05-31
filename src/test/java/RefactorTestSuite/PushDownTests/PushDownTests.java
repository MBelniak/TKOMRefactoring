package RefactorTestSuite.PushDownTests;

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

public class PushDownTests {
    private final static String TEST_FILES = "src\\test\\java\\RefactorTestSuite\\PushDownTests\\testFiles\\";
    private final static String VALIDATION_FILES = "src\\test\\java\\RefactorTestSuite\\PushDownTests\\validation\\";
    private final static String ORIGINAL_FILES = "src\\test\\java\\RefactorTestSuite\\PushDownTests\\originalFiles\\";
    private final static String TEST_OUTPUTS = "src\\test\\java\\RefactorTestSuite\\PushDownTests\\testOutputs\\";
    private static Model model;

    @Test
    public void shouldPushDownToClassInTheSameFile() throws IOException {
        model = new Model(TEST_FILES);
        List<Integer> statementsToMove = new ArrayList<>();
        statementsToMove.add(0);
        statementsToMove.add(1);
        statementsToMove.add(2);
        model.doPushOrPull("package1\\Class1.txt",
                "Class1Base", statementsToMove,
                "Class1", Refactor.PUSH_DOWN);
        File validationFile = new File(VALIDATION_FILES + "shouldPushDownToClassInTheSameFile.txt");
        File validatedFile = new File(TEST_FILES + "package1\\Class1.txt");

        FileChannel src = new FileInputStream(validatedFile).getChannel();
        FileChannel dest = new FileOutputStream(new File(TEST_OUTPUTS + "shouldPushDownToClassInTheSameFile.txt")).getChannel();
        dest.transferFrom(src, 0, src.size());
        src.close();
        dest.close();

        assertThat(validationFile).hasSameContentAs(validatedFile);
    }

    @Test
    public void shouldPushDownToClassInOtherFileSamePackage() throws IOException {
        model= new Model(TEST_FILES);
        List<Integer> statementsToMove = new ArrayList<>();
        statementsToMove.add(0);
        statementsToMove.add(1);
        statementsToMove.add(3);
        model.doPushOrPull("package1\\Class3.txt",
                "Class3", statementsToMove,
                "Class2", Refactor.PUSH_DOWN);

        File validationFile1 = new File(VALIDATION_FILES + "shouldPushDownToClassInOtherFileSamePackageSource.txt");
        File validatedFile1 = new File(TEST_FILES + "package1\\Class3.txt");

        FileChannel src = new FileInputStream(validatedFile1).getChannel();
        FileChannel dest = new FileOutputStream(new File(TEST_OUTPUTS + "shouldPushDownToClassInOtherFileSamePackageSource.txt")).getChannel();
        dest.transferFrom(src, 0, src.size());
        src.close();
        dest.close();

        assertThat(validationFile1).hasSameContentAs(validatedFile1);


        File validationFile2 = new File(VALIDATION_FILES + "shouldPushDownToClassInOtherFileSamePackageDest.txt");
        File validatedFile2 = new File(TEST_FILES + "package1\\Class2.txt");

        src = new FileInputStream(validatedFile2).getChannel();
        dest = new FileOutputStream(new File(TEST_OUTPUTS + "shouldPushDownToClassInOtherFileSamePackageDest.txt")).getChannel();
        dest.transferFrom(src, 0, src.size());
        src.close();
        dest.close();

        assertThat(validationFile2).hasSameContentAs(validatedFile2);
    }

    @Test
    public void shouldPushDownToClassInOtherPackage() throws IOException {
        model = new Model(TEST_FILES);
        List<Integer> statementsToMove = new ArrayList<>();
        statementsToMove.add(0);
        statementsToMove.add(1);
        statementsToMove.add(2);
        model.doPushOrPull("package2\\Class5.txt",
                "Class5", statementsToMove,
                "Class4", Refactor.PUSH_DOWN);

        File validationFile1 = new File(VALIDATION_FILES + "shouldPushDownToClassInOtherPackageSource.txt");
        File validatedFile1 = new File(TEST_FILES + "package2\\Class5.txt");

        FileChannel src = new FileInputStream(validatedFile1).getChannel();
        FileChannel dest = new FileOutputStream(new File(TEST_OUTPUTS + "shouldPushDownToClassInOtherPackageSource.txt")).getChannel();
        dest.transferFrom(src, 0, src.size());
        src.close();
        dest.close();

        assertThat(validationFile1).hasSameContentAs(validatedFile1);

        File validationFile2 = new File(VALIDATION_FILES + "shouldPushDownToClassInOtherPackageDest.txt");
        File validatedFile2 = new File(TEST_FILES + "package1\\Class4.txt");

        src = new FileInputStream(validatedFile2).getChannel();
        dest = new FileOutputStream(new File(TEST_OUTPUTS + "shouldPushDownToClassInOtherPackageDest.txt")).getChannel();
        dest.transferFrom(src, 0, src.size());
        src.close();
        dest.close();

        assertThat(validationFile2).hasSameContentAs(validatedFile2);
    }
    @Test
    public void shouldPushDownToInterfaceInTheSameFile() throws IOException {
        model = new Model(TEST_FILES);
        List<Integer> statementsToMove = new ArrayList<>();
        statementsToMove.add(0);
        statementsToMove.add(1);
        statementsToMove.add(2);
        model.doPushOrPull("interfacePackage1\\Interface1.txt",
                "BaseInterface", statementsToMove,
                "Interface1", Refactor.PUSH_DOWN);

        File validationFile = new File(VALIDATION_FILES + "shouldPushDownToInterfaceInTheSameFile.txt");
        File validatedFile = new File(TEST_FILES + "interfacePackage1\\Interface1.txt");

        FileChannel src = new FileInputStream(validatedFile).getChannel();
        FileChannel dest = new FileOutputStream(new File(TEST_OUTPUTS + "shouldPushDownToInterfaceInTheSameFile.txt")).getChannel();
        dest.transferFrom(src, 0, src.size());
        src.close();
        dest.close();

        assertThat(validationFile).hasSameContentAs(validatedFile);
    }

    @Test
    public void shouldPushDownToInterfaceInOtherFileSamePackage() throws IOException {
        model= new Model(TEST_FILES);
        List<Integer> statementsToMove = new ArrayList<>();
        statementsToMove.add(0);
        statementsToMove.add(1);
        model.doPushOrPull("interfacePackage1\\Interface3.txt",
                "Interface3", statementsToMove,
                "Interface2", Refactor.PUSH_DOWN);
        File validationFile1 = new File(VALIDATION_FILES + "shouldPushDownToInterfaceInOtherFileSamePackageSource.txt");
        File validatedFile1 = new File(TEST_FILES + "interfacePackage1\\Interface3.txt");

        FileChannel src = new FileInputStream(validatedFile1).getChannel();
        FileChannel dest = new FileOutputStream(new File(TEST_OUTPUTS + "shouldPushDownToInterfaceInOtherFileSamePackageSource.txt")).getChannel();
        dest.transferFrom(src, 0, src.size());
        src.close();
        dest.close();

        assertThat(validationFile1).hasSameContentAs(validatedFile1);


        File validationFile2 = new File(VALIDATION_FILES + "shouldPushDownToInterfaceInOtherFileSamePackageDest.txt");
        File validatedFile2 = new File(TEST_FILES + "interfacePackage1\\Interface2.txt");

        src = new FileInputStream(validatedFile2).getChannel();
        dest = new FileOutputStream(new File(TEST_OUTPUTS + "shouldPushDownToInterfaceInOtherFileSamePackageDest.txt")).getChannel();
        dest.transferFrom(src, 0, src.size());
        src.close();
        dest.close();

        assertThat(validationFile2).hasSameContentAs(validatedFile2);
    }

    @Test
    public void shouldPushDownToInterfaceInOtherPackage() throws IOException {
        model= new Model(TEST_FILES);
        List<Integer> statementsToMove = new ArrayList<>();
        statementsToMove.add(1);
        model.doPushOrPull("interfacePackage2\\Interface5.txt",
                "Interface5", statementsToMove,
                "Interface4", Refactor.PUSH_DOWN);
        File validationFile1 = new File(VALIDATION_FILES + "shouldPushDownToInterfaceInOtherPackageSource.txt");
        File validatedFile1 = new File(TEST_FILES + "interfacePackage2\\Interface5.txt");

        FileChannel src = new FileInputStream(validatedFile1).getChannel();
        FileChannel dest = new FileOutputStream(new File(TEST_OUTPUTS + "shouldPushDownToInterfaceInOtherPackageSource.txt")).getChannel();
        dest.transferFrom(src, 0, src.size());
        src.close();
        dest.close();

        assertThat(validationFile1).hasSameContentAs(validatedFile1);

        File validationFile2 = new File(VALIDATION_FILES + "shouldPushDownToInterfaceInOtherPackageDest.txt");
        File validatedFile2 = new File(TEST_FILES + "interfacePackage1\\Interface4.txt");

        src = new FileInputStream(validatedFile2).getChannel();
        dest = new FileOutputStream(new File(TEST_OUTPUTS + "shouldPushDownToInterfaceInOtherPackageDest.txt")).getChannel();
        dest.transferFrom(src, 0, src.size());
        src.close();
        dest.close();

        assertThat(validationFile2).hasSameContentAs(validatedFile2);
    }

    @Test
    public void shouldPushDownToInnerClass() throws IOException {
        model = new Model(TEST_FILES);
        List<Integer> statementsToMove = new ArrayList<>();
        statementsToMove.add(0);
        model.doPushOrPull("package2\\Class6.txt",
                "Class6", statementsToMove,
                "Class6.Class7.Class8", Refactor.PUSH_DOWN);
        File validationFile = new File(VALIDATION_FILES + "shouldPushDownToInnerClass.txt");
        File validatedFile = new File(TEST_FILES + "package2\\Class6.txt");

        FileChannel src = new FileInputStream(validatedFile).getChannel();
        FileChannel dest = new FileOutputStream(new File(TEST_OUTPUTS + "shouldPushDownToInnerClass.txt")).getChannel();
        dest.transferFrom(src, 0, src.size());
        src.close();
        dest.close();

        assertThat(validationFile).hasSameContentAs(validatedFile);
    }

    @Test
    public void shouldPushDownFromClassToInterface() throws IOException {
        model = new Model(TEST_FILES);
        List<Integer> statementsToMove = new ArrayList<>();
        statementsToMove.add(0);
        model.doPushOrPull("package2\\Class9.txt",
                "Interface9", statementsToMove,
                "Class9", Refactor.PUSH_DOWN);
        File validationFile = new File(VALIDATION_FILES + "shouldPushDownFromInterfaceToClass.txt");
        File validatedFile = new File(TEST_FILES + "package2\\Class9.txt");

        FileChannel src = new FileInputStream(validatedFile).getChannel();
        FileChannel dest = new FileOutputStream(new File(TEST_OUTPUTS + "shouldPushDownFromInterfaceToClass.txt")).getChannel();
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

