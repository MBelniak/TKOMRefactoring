package FilesManagement;

import org.apache.commons.io.input.BoundedInputStream;

import java.io.*;

public class FileSource {

    private static final int MAX_LINE_LENGTH = 10000;
    private static final int MAX_LINES = 50000;
    private static final int END_OF_FILE = -2;
    private BufferedReader fileInput;
    private int lineNumber;
    private int columnNumber;
    private String bufferedLine;

    public FileSource(File fileSource) throws FileNotFoundException {
        openFile(fileSource);
    }

    private int openFile(File fileSource) throws FileNotFoundException {
        InputStream bounded;
        bounded = new BoundedInputStream(new FileInputStream(fileSource), MAX_LINE_LENGTH);
        fileInput = new BufferedReader(new InputStreamReader(bounded));
        lineNumber = 0;
        columnNumber = 0;
        return readNextLine();
    }

    private int nextLine() {
        if(lineNumber>=MAX_LINES-1) {
            return -1;
        }
        return readNextLine();
    }

    public int nextChar()
    {
        if(bufferedLine==null)
           return END_OF_FILE;

        if (columnNumber==bufferedLine.length())       //we went to the end of line
        {
            columnNumber++;
            return '\n';
        }
        else if(columnNumber>bufferedLine.length())
        {
            int errorCode;
            errorCode = nextLine();
            if (errorCode != 0)
                return errorCode;    //end of file or input error
        }
        return bufferedLine.charAt(columnNumber++);
    }

    private int readNextLine()
    {
        do {
            try {
                if ((bufferedLine = fileInput.readLine()) == null) //no more lines
                {
                    return -1;
                }
            } catch (IOException e) {
                e.printStackTrace();
                return -1;
            }
            lineNumber++;
        } while (bufferedLine.length()==0);

        columnNumber = 0;
        return 0;
    }
    public int getLineNumber() {
        return lineNumber;
    }

    public int getColumnNumber() {
        return columnNumber;
    }
}
