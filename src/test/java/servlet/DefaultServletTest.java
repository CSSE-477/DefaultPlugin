package servlet;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

import protocol.HttpRequest;
import protocol.HttpResponse;
import protocol.HttpResponseBuilder;

public class DefaultServletTest {
	private static AHttpServlet defaultServlet;
	private static String rootDirectory;
	private static String defaultFileName;
	private static String directoryName;
	private static String nestedFileName;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		defaultFileName = "index.html";
		directoryName = "directory";
		nestedFileName = "test.txt";
		rootDirectory = "src/test/resources";

		// Initialize test file
		File testFile = new File(rootDirectory, defaultFileName);
		if (testFile.exists()) {
			testFile.delete();
		}
		testFile.createNewFile();

		FileWriter writer = new FileWriter(testFile);
		writer.write("This is the initialization content in index.html!");

		writer.close();

		// Initialize test directory
		File testDirectory = new File(rootDirectory, directoryName);
		if (testDirectory.exists()) {
			testDirectory.delete();
		}
		testDirectory.mkdir();

		// Initialize nested test file
		File nestedFile = new File(rootDirectory, directoryName + "/" + nestedFileName);
		if (nestedFile.exists()) {
			nestedFile.delete();
		}
		nestedFile.createNewFile();

		FileWriter nestedWriter = new FileWriter(nestedFile);
		nestedWriter.write("This is the initialization content in test.txt!");

		nestedWriter.close();

		// Initialize default servlet
		defaultServlet = new DefaultServlet(rootDirectory);
	}

	@Test
	public void testGet404NotFound() {
		HttpRequest request = Mockito.mock(HttpRequest.class);
		when(request.getMethod()).thenReturn("GET");
		when(request.getVersion()).thenReturn("HTTP/1.1");
		when(request.getUri()).thenReturn("/notFound.txt");

		HttpResponseBuilder rb = new HttpResponseBuilder();

		defaultServlet.doGet(request, rb);

		HttpResponse response = rb.generateResponse();

		int expected = 404;
		int actual = response.getStatus();
		assertEquals(actual, expected);
	}

	@Test
	public void testGet404NotFoundIsDirectory() {
		HttpRequest request = Mockito.mock(HttpRequest.class);
		when(request.getMethod()).thenReturn("GET");
		when(request.getVersion()).thenReturn("HTTP/1.1");
		when(request.getUri()).thenReturn("/" + directoryName);

		HttpResponseBuilder rb = new HttpResponseBuilder();

		defaultServlet.doGet(request, rb);

		HttpResponse response = rb.generateResponse();

		int expected = 404;
		int actual = response.getStatus();
		assertEquals(actual, expected);
	}

	@Test
	public void testGet200OK() throws Exception {
		HttpRequest request = Mockito.mock(HttpRequest.class);
		when(request.getMethod()).thenReturn("GET");
		when(request.getVersion()).thenReturn("HTTP/1.1");
		when(request.getUri()).thenReturn("/" + defaultFileName);

		HttpResponseBuilder rb = new HttpResponseBuilder();

		defaultServlet.doGet(request, rb);

		HttpResponse response = rb.generateResponse();

		int expected = 200;
		int actual = response.getStatus();
		assertEquals(actual, expected);

		String expectedBody = "This is the initialization content in index.html!";

		FileInputStream fis = new FileInputStream(response.getFile());
		byte[] data = new byte[(int) response.getFile().length()];
		fis.read(data);
		fis.close();
		String actualBody = new String(data, "UTF-8");

		assertEquals(expectedBody, actualBody);
	}
	
	@Test
	public void testGet200OKNestedFile() throws Exception {
		HttpRequest request = Mockito.mock(HttpRequest.class);
		when(request.getMethod()).thenReturn("GET");
		when(request.getVersion()).thenReturn("HTTP/1.1");
		when(request.getUri()).thenReturn("/" + directoryName + "/" + nestedFileName);
		

		HttpResponseBuilder rb = new HttpResponseBuilder();

		defaultServlet.doGet(request, rb);

		HttpResponse response = rb.generateResponse();

		int expected = 200;
		int actual = response.getStatus();
		assertEquals(expected, actual);

		String expectedBody = "This is the initialization content in test.txt!";

		FileInputStream fis = new FileInputStream(response.getFile());
		byte[] data = new byte[(int) response.getFile().length()];
		fis.read(data);
		fis.close();
		String actualBody = new String(data, "UTF-8");

		assertEquals(expectedBody, actualBody);
	}
	
	@Test
	public void testGet200OKDefaultUri() throws Exception {
		HttpRequest request = Mockito.mock(HttpRequest.class);
		when(request.getMethod()).thenReturn("GET");
		when(request.getVersion()).thenReturn("HTTP/1.1");
		when(request.getUri()).thenReturn("/");

		HttpResponseBuilder rb = new HttpResponseBuilder();

		defaultServlet.doGet(request, rb);

		HttpResponse response = rb.generateResponse();

		int expected = 200;
		int actual = response.getStatus();
		assertEquals(actual, expected);

		String expectedBody = "This is the initialization content in index.html!";

		FileInputStream fis = new FileInputStream(response.getFile());
		byte[] data = new byte[(int) response.getFile().length()];
		fis.read(data);
		fis.close();
		String actualBody = new String(data, "UTF-8");

		assertEquals(expectedBody, actualBody);
	}

	@AfterClass
	public static void tearDownAfterClass() {
		File testFile = new File(rootDirectory, defaultFileName);
		if (testFile.exists()) {
			testFile.delete();
		}

		File nestedFile = new File(rootDirectory, directoryName + "/" + nestedFileName);
		if (nestedFile.exists()) {
			nestedFile.delete();
		}

		File testDirectory = new File(rootDirectory, directoryName);
		if (testDirectory.exists()) {
			testDirectory.delete();
		}
	}
}
