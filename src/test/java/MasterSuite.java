import integration.*;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import servlet.DefaultServletTest;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by TrottaSN on 1/19/2017.
 */

@RunWith(Suite.class)
@Suite.SuiteClasses({
        DeleteRequestTests.class,
        GetRequestTests.class,
        HeadRequestTests.class,
        PostRequestTests.class,
        PutRequestTests.class,
        DefaultServletTest.class
})
public class MasterSuite {

    @BeforeClass
    public static void setUp() throws IOException {
        String defaultFileContent = "This is the initialization content in index.html!";
        String nestedFileContent = "This is the initialization content in test.txt!";
        String defaultFileName = "index.html";
        String directoryName = "directory";
        String nestedFileName = "test.txt";
        String newFileName = "new.txt";
        String rootDirectory = "src/test/resources";
        // Initialize test file
        File testDirectory = new File(rootDirectory);
        if(!testDirectory.exists()) {
            testDirectory.mkdirs();
        }
        File innerDirectory = new File(rootDirectory, directoryName);
        if(!innerDirectory.exists()) {
            innerDirectory.mkdirs();
        }
        File testFile = new File(rootDirectory, defaultFileName);
        if(testFile.exists()){
            testFile.delete();
        }
        testFile.createNewFile();

        FileWriter writer = new FileWriter(testFile, false);
        writer.write(defaultFileContent);

        writer.close();

        // Initialize nested test file
        File nestedFile = new File(rootDirectory, directoryName + "/" + nestedFileName);
        if(testFile.exists()){
            nestedFile.delete();
        }
        nestedFile.createNewFile();

        FileWriter nestedWriter = new FileWriter(nestedFile, false);
        nestedWriter.write(nestedFileContent);

        nestedWriter.close();
    }

    @AfterClass
    public static void tearDown() throws IOException {
    }

}
