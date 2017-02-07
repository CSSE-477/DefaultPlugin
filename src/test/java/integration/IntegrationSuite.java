package integration;

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
        PutRequestTests.class
})
public class IntegrationSuite {

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
        createResourceDir();
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
        reclaimResourceDirectory();
    }

    public static boolean createResourceDir(){
        File dir = new File("src/test/resources");
        if(dir.exists()){
            reclaimResourceDirectory();
        }
        return dir.mkdirs();
    }

    public static boolean reclaimResourceDirectory()
    {
        File dir = new File("src/test/resources");
        return reclaimHelper(dir);
    }

    private static boolean reclaimHelper(File dir) {
        if (dir.isDirectory()) {
            File[] children = dir.listFiles();
            for (int i = 0; i < children.length; i++) {
                boolean success = reclaimHelper(children[i]);
                if (!success) {
                    return false;
                }
            }
        }
        return dir.delete();
    }}
