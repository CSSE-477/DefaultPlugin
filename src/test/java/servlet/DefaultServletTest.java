package servlet;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.util.HashMap;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

import protocol.HttpRequest;
import protocol.HttpResponse;
import protocol.HttpResponseBuilder;
import protocol.Keywords;
import protocol.Protocol;

public class DefaultServletTest {
	private static AHttpServlet defaultServlet;
	private static String rootDirectory;
	private static String defaultFileName;
	private static String directoryName;
	private static String nestedFileName;
	private static String newFileName;
	private static final String defaultFileContent = "This is the initialization content in index.html!";
	private static final String nestedFileContent = "This is the initialization content in test.txt!";

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		defaultFileName = "index.html";
		directoryName = "directory";
		nestedFileName = "test.txt";
		newFileName = "new.txt";
		rootDirectory = "src/test/resources";

		// Initialize default servlet
		defaultServlet = new DefaultServlet(rootDirectory);
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
	public void tearDownAfterEachTwst() throws Exception {
		// Delete new file
		File newFile = new File(rootDirectory, newFileName);
		if (newFile.exists()) {
			newFile.delete();
		}
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
		assertEquals(expected, actual);
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
		assertEquals(expected, actual);
	}

	@Test
	public void testGet200Ok() throws Exception {
		HttpRequest request = Mockito.mock(HttpRequest.class);
		when(request.getMethod()).thenReturn("GET");
		when(request.getVersion()).thenReturn("HTTP/1.1");
		when(request.getUri()).thenReturn("/" + defaultFileName);

		HttpResponseBuilder rb = new HttpResponseBuilder();

		defaultServlet.doGet(request, rb);

		HttpResponse response = rb.generateResponse();

		int expected = 200;
		int actual = response.getStatus();
		assertEquals(expected, actual);

		String expectedBody = defaultFileContent;

		FileInputStream fis = new FileInputStream(response.getFile());
		byte[] data = new byte[(int) response.getFile().length()];
		fis.read(data);
		fis.close();
		String actualBody = new String(data, "UTF-8");

		assertEquals(expectedBody, actualBody);
	}

	@Test
	public void testGet200OkNestedFile() throws Exception {
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

		String expectedBody = nestedFileContent;

		FileInputStream fis = new FileInputStream(response.getFile());
		byte[] data = new byte[(int) response.getFile().length()];
		fis.read(data);
		fis.close();
		String actualBody = new String(data, "UTF-8");

		assertEquals(expectedBody, actualBody);
	}

	@Test
	public void testGet200OkDefaultUri() throws Exception {
		HttpRequest request = Mockito.mock(HttpRequest.class);
		when(request.getMethod()).thenReturn("GET");
		when(request.getVersion()).thenReturn("HTTP/1.1");
		when(request.getUri()).thenReturn("/");

		HttpResponseBuilder rb = new HttpResponseBuilder();

		defaultServlet.doGet(request, rb);

		HttpResponse response = rb.generateResponse();

		int expected = 200;
		int actual = response.getStatus();
		assertEquals(expected, actual);

		String expectedBody = defaultFileContent;

		FileInputStream fis = new FileInputStream(response.getFile());
		byte[] data = new byte[(int) response.getFile().length()];
		fis.read(data);
		fis.close();
		String actualBody = new String(data, "UTF-8");

		assertEquals(expectedBody, actualBody);
	}

	@Test
	public void testHead404NotFound() {
		HttpRequest request = Mockito.mock(HttpRequest.class);
		when(request.getMethod()).thenReturn("HEAD");
		when(request.getVersion()).thenReturn("HTTP/1.1");
		when(request.getUri()).thenReturn("/notFound.txt");

		HttpResponseBuilder rb = new HttpResponseBuilder();

		defaultServlet.doHead(request, rb);

		HttpResponse response = rb.generateResponse();

		int expected = 404;
		int actual = response.getStatus();
		assertEquals(expected, actual);
	}

	@Test
	public void testHead404NotFoundIsDirectory() {
		HttpRequest request = Mockito.mock(HttpRequest.class);
		when(request.getMethod()).thenReturn("HEAD");
		when(request.getVersion()).thenReturn("HTTP/1.1");
		when(request.getUri()).thenReturn("/" + directoryName);

		HttpResponseBuilder rb = new HttpResponseBuilder();

		defaultServlet.doHead(request, rb);

		HttpResponse response = rb.generateResponse();

		int expected = 404;
		int actual = response.getStatus();
		assertEquals(expected, actual);
	}

	@Test
	public void testHead200Ok() throws Exception {
		HttpRequest request = Mockito.mock(HttpRequest.class);
		when(request.getMethod()).thenReturn("HEAD");
		when(request.getVersion()).thenReturn("HTTP/1.1");
		when(request.getUri()).thenReturn("/" + defaultFileName);

		HttpResponseBuilder rb = new HttpResponseBuilder();

		defaultServlet.doHead(request, rb);

		HttpResponse response = rb.generateResponse();

		int expected = 200;
		int actual = response.getStatus();
		assertEquals(expected, actual);

		int expectedLength = defaultFileContent.length();
		int actualLength = Integer.parseInt(response.getHeader().get("Content-Length"));
		assertEquals(expectedLength, actualLength);
	}

	@Test
	public void testHead200OkNestedFile() throws Exception {
		HttpRequest request = Mockito.mock(HttpRequest.class);
		when(request.getMethod()).thenReturn("HEAD");
		when(request.getVersion()).thenReturn("HTTP/1.1");
		when(request.getUri()).thenReturn("/" + directoryName + "/" + nestedFileName);

		HttpResponseBuilder rb = new HttpResponseBuilder();

		defaultServlet.doHead(request, rb);

		HttpResponse response = rb.generateResponse();

		int expected = 200;
		int actual = response.getStatus();
		assertEquals(expected, actual);

		int expectedLength = nestedFileContent.length();
		int actualLength = Integer.parseInt(response.getHeader().get("Content-Length"));
		assertEquals(expectedLength, actualLength);
	}

	@Test
	public void testHead200OkDefaultUri() throws Exception {
		HttpRequest request = Mockito.mock(HttpRequest.class);
		when(request.getMethod()).thenReturn("HEAD");
		when(request.getVersion()).thenReturn("HTTP/1.1");
		when(request.getUri()).thenReturn("/");

		HttpResponseBuilder rb = new HttpResponseBuilder();

		defaultServlet.doHead(request, rb);

		HttpResponse response = rb.generateResponse();

		int expected = 200;
		int actual = response.getStatus();
		assertEquals(expected, actual);

		int expectedLength = defaultFileContent.length();
		int actualLength = Integer.parseInt(response.getHeader().get("Content-Length"));
		assertEquals(expectedLength, actualLength);
	}

	@Test
	public void testPost400BadRequestIsDirectory() throws Exception {
		HttpRequest request = Mockito.mock(HttpRequest.class);
		when(request.getMethod()).thenReturn("POST");
		when(request.getVersion()).thenReturn("HTTP/1.1");
		when(request.getUri()).thenReturn("/" + directoryName);

		HttpResponseBuilder rb = new HttpResponseBuilder();

		defaultServlet.doPost(request, rb);

		HttpResponse response = rb.generateResponse();

		int expected = 400;
		int actual = response.getStatus();
		assertEquals(expected, actual);
	}

	@Test
	public void testPost200OkAppendToFile() throws Exception {
		HttpRequest request = Mockito.mock(HttpRequest.class);
		when(request.getMethod()).thenReturn("POST");
		when(request.getVersion()).thenReturn("HTTP/1.1");
		when(request.getUri()).thenReturn("/" + defaultFileName);
		String postContent = "POST Content";
		when(request.getBody()).thenReturn(postContent.toCharArray());
		HashMap<String, String> fakeRequestHeaders = new HashMap<String, String>();
		fakeRequestHeaders.put(Protocol.getProtocol().getStringRep(Keywords.CONTENT_LENGTH),
				String.valueOf(postContent.length()));
		when(request.getHeader()).thenReturn(fakeRequestHeaders);

		HttpResponseBuilder rb = new HttpResponseBuilder();

		defaultServlet.doPost(request, rb);

		HttpResponse response = rb.generateResponse();

		int expected = 200;
		int actual = response.getStatus();
		assertEquals(expected, actual);

		String expectedBody = defaultFileContent + postContent;

		FileInputStream fis = new FileInputStream(response.getFile());
		byte[] data = new byte[(int) response.getFile().length()];
		fis.read(data);
		fis.close();
		String actualBody = new String(data, "UTF-8");

		assertEquals(expectedBody, actualBody);
	}
	
	@Test
	public void testPost200OkCreateNewFile() throws Exception {
		HttpRequest request = Mockito.mock(HttpRequest.class);
		when(request.getMethod()).thenReturn("POST");
		when(request.getVersion()).thenReturn("HTTP/1.1");
		when(request.getUri()).thenReturn("/" + newFileName);
		String postContent = "POST Content";
		when(request.getBody()).thenReturn(postContent.toCharArray());
		HashMap<String, String> fakeRequestHeaders = new HashMap<String, String>();
		fakeRequestHeaders.put(Protocol.getProtocol().getStringRep(Keywords.CONTENT_LENGTH),
				String.valueOf(postContent.length()));
		when(request.getHeader()).thenReturn(fakeRequestHeaders);

		HttpResponseBuilder rb = new HttpResponseBuilder();

		defaultServlet.doPost(request, rb);

		HttpResponse response = rb.generateResponse();

		int expected = 200;
		int actual = response.getStatus();
		assertEquals(expected, actual);

		String expectedBody = postContent;

		FileInputStream fis = new FileInputStream(response.getFile());
		byte[] data = new byte[(int) response.getFile().length()];
		fis.read(data);
		fis.close();
		String actualBody = new String(data, "UTF-8");

		assertEquals(expectedBody, actualBody);
	}
	
	@Test
	public void testPost500InternalServerErrorFileLocked() throws Exception {
		HttpRequest request = Mockito.mock(HttpRequest.class);
		when(request.getMethod()).thenReturn("POST");
		when(request.getVersion()).thenReturn("HTTP/1.1");
		when(request.getUri()).thenReturn("/" + defaultFileName);
		
		File file = new File(rootDirectory, defaultFileName);
		file.setWritable(false);

		HttpResponseBuilder rb = new HttpResponseBuilder();

		defaultServlet.doPost(request, rb);
		
		file.setWritable(true);

		HttpResponse response = rb.generateResponse();

		int expected = 500;
		int actual = response.getStatus();
		assertEquals(expected, actual);
	}
	
	@Test
	public void testPut400BadRequestIsDirectory() throws Exception {
		HttpRequest request = Mockito.mock(HttpRequest.class);
		when(request.getMethod()).thenReturn("PUT");
		when(request.getVersion()).thenReturn("HTTP/1.1");
		when(request.getUri()).thenReturn("/" + directoryName);

		HttpResponseBuilder rb = new HttpResponseBuilder();

		defaultServlet.doPut(request, rb);

		HttpResponse response = rb.generateResponse();

		int expected = 400;
		int actual = response.getStatus();
		assertEquals(expected, actual);
	}

	@Test
	public void testPost200OkOverwriteFile() throws Exception {
		HttpRequest request = Mockito.mock(HttpRequest.class);
		when(request.getMethod()).thenReturn("PUT");
		when(request.getVersion()).thenReturn("HTTP/1.1");
		when(request.getUri()).thenReturn("/" + defaultFileName);
		String putContent = "POST Content";
		when(request.getBody()).thenReturn(putContent.toCharArray());
		HashMap<String, String> fakeRequestHeaders = new HashMap<String, String>();
		fakeRequestHeaders.put(Protocol.getProtocol().getStringRep(Keywords.CONTENT_LENGTH),
				String.valueOf(putContent.length()));
		when(request.getHeader()).thenReturn(fakeRequestHeaders);

		HttpResponseBuilder rb = new HttpResponseBuilder();

		defaultServlet.doPut(request, rb);

		HttpResponse response = rb.generateResponse();

		int expected = 200;
		int actual = response.getStatus();
		assertEquals(expected, actual);

		String expectedBody = putContent;

		FileInputStream fis = new FileInputStream(response.getFile());
		byte[] data = new byte[(int) response.getFile().length()];
		fis.read(data);
		fis.close();
		String actualBody = new String(data, "UTF-8");

		assertEquals(expectedBody, actualBody);
	}
	
	@Test
	public void testPut200OkCreateNewFile() throws Exception {
		HttpRequest request = Mockito.mock(HttpRequest.class);
		when(request.getMethod()).thenReturn("PUT");
		when(request.getVersion()).thenReturn("HTTP/1.1");
		when(request.getUri()).thenReturn("/" + newFileName);
		String putContent = "POST Content";
		when(request.getBody()).thenReturn(putContent.toCharArray());
		HashMap<String, String> fakeRequestHeaders = new HashMap<String, String>();
		fakeRequestHeaders.put(Protocol.getProtocol().getStringRep(Keywords.CONTENT_LENGTH),
				String.valueOf(putContent.length()));
		when(request.getHeader()).thenReturn(fakeRequestHeaders);

		HttpResponseBuilder rb = new HttpResponseBuilder();

		defaultServlet.doPut(request, rb);

		HttpResponse response = rb.generateResponse();

		int expected = 200;
		int actual = response.getStatus();
		assertEquals(expected, actual);

		String expectedBody = putContent;

		FileInputStream fis = new FileInputStream(response.getFile());
		byte[] data = new byte[(int) response.getFile().length()];
		fis.read(data);
		fis.close();
		String actualBody = new String(data, "UTF-8");

		assertEquals(expectedBody, actualBody);
	}
	
	@Test
	public void testPut500InternalServerErrorFileLocked() throws Exception {
		HttpRequest request = Mockito.mock(HttpRequest.class);
		when(request.getMethod()).thenReturn("PUT");
		when(request.getVersion()).thenReturn("HTTP/1.1");
		when(request.getUri()).thenReturn("/" + defaultFileName);
		
		File file = new File(rootDirectory, defaultFileName);
		file.setWritable(false);

		HttpResponseBuilder rb = new HttpResponseBuilder();

		defaultServlet.doPut(request, rb);
		
		file.setWritable(true);

		HttpResponse response = rb.generateResponse();

		int expected = 500;
		int actual = response.getStatus();
		assertEquals(expected, actual);
	}
	
	@Test
	public void testDelete204NoContentFileDeleted() throws Exception {
		File newFile = new File(rootDirectory, newFileName);
		if (!newFile.exists()) {
			newFile.createNewFile();
		}
		
		HttpRequest request = Mockito.mock(HttpRequest.class);
		when(request.getMethod()).thenReturn("DELETE");
		when(request.getVersion()).thenReturn("HTTP/1.1");
		when(request.getUri()).thenReturn("/" + newFileName);
		
		HttpResponseBuilder rb = new HttpResponseBuilder();

		defaultServlet.doDelete(request, rb);

		HttpResponse response = rb.generateResponse();

		int expected = 204;
		int actual = response.getStatus();
		assertEquals(expected, actual);
		
		HttpRequest getRequest = Mockito.mock(HttpRequest.class);
		when(getRequest.getMethod()).thenReturn("GET");
		when(getRequest.getVersion()).thenReturn("HTTP/1.1");
		when(getRequest.getUri()).thenReturn("/" + newFileName);

		HttpResponseBuilder getrb = new HttpResponseBuilder();

		defaultServlet.doGet(getRequest, getrb);

		HttpResponse getResponse = getrb.generateResponse();

		int getExpected = 404;
		int getActual = getResponse.getStatus();
		assertEquals(getExpected, getActual);
	}
	
	@Test
	public void testDelete204NoContentDirectoryDeleted() throws Exception {
		File newFile = new File(rootDirectory, newFileName);
		if (!newFile.exists()) {
			newFile.mkdir();
		}
		
		HttpRequest request = Mockito.mock(HttpRequest.class);
		when(request.getMethod()).thenReturn("DELETE");
		when(request.getVersion()).thenReturn("HTTP/1.1");
		when(request.getUri()).thenReturn("/" + newFileName);
		
		HttpResponseBuilder rb = new HttpResponseBuilder();

		defaultServlet.doDelete(request, rb);

		HttpResponse response = rb.generateResponse();

		int expected = 204;
		int actual = response.getStatus();
		assertEquals(expected, actual);
		
		HttpRequest getRequest = Mockito.mock(HttpRequest.class);
		when(getRequest.getMethod()).thenReturn("GET");
		when(getRequest.getVersion()).thenReturn("HTTP/1.1");
		when(getRequest.getUri()).thenReturn("/" + newFileName);

		HttpResponseBuilder getrb = new HttpResponseBuilder();

		defaultServlet.doGet(getRequest, getrb);

		HttpResponse getResponse = getrb.generateResponse();

		int getExpected = 404;
		int getActual = getResponse.getStatus();
		assertEquals(getExpected, getActual);
	}
	
	@Test
	public void testDelete404NotFound() {
		HttpRequest request = Mockito.mock(HttpRequest.class);
		when(request.getMethod()).thenReturn("DELETE");
		when(request.getVersion()).thenReturn("HTTP/1.1");
		when(request.getUri()).thenReturn("/notFound.txt");

		HttpResponseBuilder rb = new HttpResponseBuilder();

		defaultServlet.doDelete(request, rb);

		HttpResponse response = rb.generateResponse();

		int expected = 404;
		int actual = response.getStatus();
		assertEquals(expected, actual);
	}

	@AfterClass
	public static void tearDownAfterClass() {
		// nothing to do here
	}
}
