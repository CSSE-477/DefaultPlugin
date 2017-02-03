package integration;

import static org.junit.Assert.assertEquals;
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

public class HeadRequestTests {
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
	public void testHead404NotFound() throws Exception {
		GenericUrl url = new GenericUrl("http://" + InetAddress.getLocalHost().getHostAddress() + ":" + port + "/notFound.txt");
		HttpRequest request = requestFactory.buildHeadRequest(url);

		try {
			request.execute();
		} catch (HttpResponseException e) {
			int expected = 404;
			int actual = e.getStatusCode();
			assertEquals(expected, actual);
		}
	}

	@Test
	public void testHead200Ok() throws Exception {
		GenericUrl url = new GenericUrl("http://" + InetAddress.getLocalHost().getHostAddress() + ":" + port + "/" + defaultFileName);
		HttpRequest request = requestFactory.buildHeadRequest(url);
		
		HttpResponse response = request.execute();
		int expected = 200;
		int actual = response.getStatusCode();
		assertEquals(expected, actual);
		
		int expectedLength = defaultFileContent.length();
		int actualLength = Math.toIntExact(response.getHeaders().getContentLength());

		assertEquals(expectedLength, actualLength);
	}

	@Test
	public void testHead200OkDefaultUri() throws Exception {
		GenericUrl url = new GenericUrl("http://" + InetAddress.getLocalHost().getHostAddress() + ":" + port + "/");
		HttpRequest request = requestFactory.buildHeadRequest(url);
		
		HttpResponse response = request.execute();
		int expected = 200;
		int actual = response.getStatusCode();
		assertEquals(expected, actual);
		
		int expectedLength = defaultFileContent.length();
		int actualLength = Math.toIntExact(response.getHeaders().getContentLength());

		assertEquals(expectedLength, actualLength);
	}
	
	@Test
	public void testGet200OkNestedFile() throws Exception {
		GenericUrl url = new GenericUrl("http://" + InetAddress.getLocalHost().getHostAddress() + ":" + port + "/" + directoryName + "/" + nestedFileName);
		HttpRequest request = requestFactory.buildHeadRequest(url);
		
		HttpResponse response = request.execute();
		int expected = 200;
		int actual = response.getStatusCode();
		assertEquals(expected, actual);
		
		int expectedLength = nestedFileContent.length();
		int actualLength = Math.toIntExact(response.getHeaders().getContentLength());

		assertEquals(expectedLength, actualLength);
	}

	@Test
	public void testGet404NotFoundIsDirectory() throws Exception {
		GenericUrl url = new GenericUrl("http://" + InetAddress.getLocalHost().getHostAddress() + ":" + port + "/" + directoryName);
		HttpRequest request = requestFactory.buildHeadRequest(url);


		try {
			request.execute();
		} catch (HttpResponseException e) {
			int expected = 404;
			int actual = e.getStatusCode();
			assertEquals(expected, actual);
		}
	}
	
	@AfterClass
	public static void tearDownAfterClass() {
		server.stop();
	}
}