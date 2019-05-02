package FilesManagement;

import org.apache.commons.io.input.BoundedInputStream;

import java.io.*;

public class FileSource {

    private static final int MAX_LINE_LENGTH = 10000;
    private static final int MAX_LINES = 50000;
    private static final int END_OF_FILE = -2;
    private String filename;
    private BufferedReader fileInput;
    private int lineNumber;
    private int columnNumber;
    private String bufferedLine;
    private String errorMessage;

    public FileSource(String filename) {
        this.filename = filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public int openFile()  {
        InputStream bounded;
        try {
            bounded = new BoundedInputStream(new FileInputStream(new File(filename)), MAX_LINE_LENGTH);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            errorMessage = "File not found.";
            return -1;
        }
        fileInput = new BufferedReader(new InputStreamReader(bounded));
        lineNumber = 0;
        columnNumber = 0;
        return readNextLine();
    }

    private int nextLine() {
        if(lineNumber>=MAX_LINES-1) {
            errorMessage = "Nothing more to read.";
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
                    errorMessage = "End of file.";
                    return -1;
                }
            } catch (IOException e) {
                e.printStackTrace();
                errorMessage = "Input exception";
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

    public String getErrorMessage() {
        return errorMessage;
    }
}
