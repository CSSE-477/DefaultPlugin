package benchmarks;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.URLClassLoader;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.JsonObjectParser;
import com.google.api.client.json.jackson.JacksonFactory;

import server.Server;
import servlet.AServletManager;
import servlet.DefaultServletManager;

public class ChecksumTests {
	private static Server server;
	private static int port;
	private static String rootDirectory;
	private static String defaultFileName;
	private static String directoryName;
	private static String nestedFileName;
	private static String newFileName;
	private static final String defaultFileContent = "This is the initialization content in index.html!";
	private static final String nestedFileContent = "This is the initialization content in test.txt!";
	
	private final static HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
	private final static JsonFactory JSON_FACTORY = new JacksonFactory();
	
	private static HttpRequestFactory requestFactory;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		defaultFileName = "index.html";
		directoryName = "directory";
		nestedFileName = "test.txt";
		newFileName = "new.txt";
		rootDirectory = "src/test/resources";
		
		// Mock URLClassLoader
		URLClassLoader fakeClassLoader = Mockito.mock(URLClassLoader.class);
		
		File configFile = new File("src/main/resources/config.csv");
		InputStream configStream = new FileInputStream(configFile);
		when(fakeClassLoader.getResourceAsStream("./config.csv")).thenReturn(configStream);
		
		// Create DefaultServletManager
		AServletManager dsm = new DefaultServletManager("src/test/resources", fakeClassLoader);
		
		// Set up server and add plugin
		port = 8080;
		server = new Server(port);
		server.addPlugin("", dsm);
		Thread runner = new Thread(server);
		runner.start();

		int sleepAmount = 1000;
		int retries = 10;
		while(!server.isReady()) {
            if (retries > 0) {
                Thread.sleep(sleepAmount);
            }
            else{
                break;
            }
            retries = retries - 1;
        }
		
	    requestFactory = HTTP_TRANSPORT.createRequestFactory(request -> request.setParser(new JsonObjectParser(JSON_FACTORY)));
	}
	
	@Before
	public void setUpBeforeEachTest() throws Exception {
		// Initialize test file
		File testFile = new File(rootDirectory, defaultFileName);

		FileWriter writer = new FileWriter(testFile, false);
		writer.write(defaultFileContent);

		writer.close();

		// Initialize nested test file
		File nestedFile = new File(rootDirectory, directoryName + "/" + nestedFileName);

		FileWriter nestedWriter = new FileWriter(nestedFile, false);
		nestedWriter.write(nestedFileContent);

		nestedWriter.close();
	}
	
	@After
	public void tearDownAfterEachTest() throws Exception {
		// Delete new file
		File newFile = new File(rootDirectory, newFileName);
		if (newFile.exists()) {
			newFile.delete();
		}
	}

	@Test
	public void testGet200OkWithChecksum() throws Exception {
		GenericUrl url = new GenericUrl("http://" + InetAddress.getLocalHost().getHostAddress() + ":" + port + "/" + defaultFileName);
		HttpRequest request = requestFactory.buildGetRequest(url);
		
		HttpResponse response = request.execute();
		int expected = 200;
		int actual = response.getStatusCode();
		assertEquals(expected, actual);
		
		String expectedBody = defaultFileContent;

		InputStream is = response.getContent();
		byte[] data = new byte[Math.toIntExact(response.getHeaders().getContentLength())];
		is.read(data);
		is.close();
		String actualBody = new String(data, "UTF-8");

		String received = response.getHeaders().get("checksum").toString();
		String checksum = received.substring(1, received.length()-1);
		
		assertEquals("F9843BC4DF444B6B77BE2D6F22E6AC021EE1D89ED6E2FBCDDB59FC105B6825DC", checksum);

		assertEquals(expectedBody, actualBody);
	}
	
	@Test
	public void testResponseTime() throws Exception{
		
		long total = 0L;
		
		for (int i=0; i < 1000; i++) {
			GenericUrl url = new GenericUrl("http://" + InetAddress.getLocalHost().getHostAddress() + ":" + port + "/" + defaultFileName);
			HttpRequest request = requestFactory.buildGetRequest(url);
			
			long startTime = System.currentTimeMillis();
			HttpResponse response = request.execute();
			long endTime = System.currentTimeMillis();
			
			total += endTime-startTime;
		}
		Double averageTime = total/1000.0;
		System.err.println("Average response time over 1000 requests: " + averageTime);
		assertTrue(true);
	}
	
	@AfterClass
	public static void tearDownAfterClass() {
		server.stop();
	}
}