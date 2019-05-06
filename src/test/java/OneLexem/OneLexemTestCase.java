package OneLexem;

import Lexems.Lexem;
import Scanner.Scanner;
import org.junit.Test;


import java.io.File;
import java.io.FileNotFoundException;

import static Lexems.Lexem.LexemType.EOF;
import static org.junit.Assert.assertEquals;


public class OneLexemTestCase {

    @Test
    public void shouldRecognizeEveryLexem() throws FileNotFoundException {
        File testFileSource = new File("src/test/java/OneLexem/Test1.txt");
        Scanner scanner = new Scanner();
        assertEquals(0, scanner.bindFile(testFileSource));

        int lexValue = 0;
        Lexem lexem;
        while ((lexem = scanner.getNextLexem()).getLexemType() != EOF) {
            assertEquals(lexValue, lexem.getLexemType().getValue());
            lexValue++;
        }
    }
}
