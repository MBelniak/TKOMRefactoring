package OneLexem;

import FilesManagement.FileSource;
import Scanner.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;


class OneLexemTestCase {


    @Test
    void shouldRecognizeEveryLexem() {
        FileSource testFileSource = new FileSource("src/test/java/OneLexem/Test1.txt");
        Scanner scanner = new Scanner();
        assertEquals(0, scanner.bindFileSource(testFileSource));

        int lexValue = 0;
        Lexem lexem;
        while ((lexem = scanner.getNextLexem()) != null) {
            assertEquals(lexValue, lexem.getLexemType().getValue());
            lexValue++;
        }
    }
}
