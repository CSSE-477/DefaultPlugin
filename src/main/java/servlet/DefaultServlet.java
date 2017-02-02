package servlet;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

import org.apache.commons.io.FilenameUtils;

import protocol.HttpRequest;
import protocol.HttpResponseBuilder;
import protocol.Keywords;
import protocol.Protocol;
import utils.SwsLogger;

public class DefaultServlet extends AHttpServlet {

	public DefaultServlet(String resourcePath) {
		super(resourcePath);
	}

	@Override
	public void init() {
		// Nothing to do here
	}

	@Override
	public void destroy() {
		// Nothing to do here
	}

	@Override
	public void doGet(HttpRequest request, HttpResponseBuilder responseBuilder) {
		String uri = request.getUri();
		File file = new File(this.resourcePath.concat(uri));

		if (!file.exists()) {
			responseBuilder.setStatus(404)
					.setPhrase(Protocol.getProtocol().getStringRep(Protocol.getProtocol().getCodeKeyword(404)));
		} else if (file.isDirectory()) {
			// check for default file before sending 404
			String location = this.resourcePath.concat(uri).concat(System.getProperty("file.separator"))
					.concat(Protocol.getProtocol().getStringRep(Keywords.DEFAULT_FILE));
			file = new File(location);
			if (file.exists()) {
				responseBuilder.setStatus(200)
						.setPhrase(Protocol.getProtocol().getStringRep(Protocol.getProtocol().getCodeKeyword(200)))
						.putHeader(Protocol.getProtocol().getStringRep(Keywords.CONTENT_TYPE), "text/html")
						.setFile(file);
			} else {
				responseBuilder.setStatus(404)
						.setPhrase(Protocol.getProtocol().getStringRep(Protocol.getProtocol().getCodeKeyword(404)));
			}
		} else {
			responseBuilder.setStatus(200)
					.setPhrase(Protocol.getProtocol().getStringRep(Protocol.getProtocol().getCodeKeyword(200)))
					.putHeader(Protocol.getProtocol().getStringRep(Keywords.CONTENT_TYPE), "text/html").setFile(file);
		}
	}

	@Override
	public void doHead(HttpRequest request, HttpResponseBuilder responseBuilder) {
		String fileRequested = request.getUri();
		String fullPath = this.resourcePath.concat(fileRequested);

		File file = new File(fullPath);

		if (!file.exists()) {
			responseBuilder.setStatus(404)
					.setPhrase(Protocol.getProtocol().getStringRep(Protocol.getProtocol().getCodeKeyword(404)));
		} else if (file.isDirectory()) {
			// check for default file before sending 400
			String location = fullPath.concat(System.getProperty("file.separator"))
					.concat(Protocol.getProtocol().getStringRep(Keywords.DEFAULT_FILE));
			file = new File(location);

			if (file.exists()) {
				SwsLogger.accessLogger.info("HEAD to file " + file.getAbsolutePath() + ". Sending 200 OK");
				getHeadResponseFromFile(file, responseBuilder);
			} else {
				SwsLogger.errorLogger.error("HEAD to file " + file.getAbsolutePath() + ". Sending 400 Bad Request");
				responseBuilder.setStatus(400)
						.setPhrase(Protocol.getProtocol().getStringRep(Protocol.getProtocol().getCodeKeyword(400)));
			}
		} else {
			// file exists; return last modified, file size, file type
			SwsLogger.accessLogger.info("HEAD to file " + file.getAbsolutePath() + ". Sending 200 OK");
			getHeadResponseFromFile(file, responseBuilder);
		}
	}

	@Override
	public void doPost(HttpRequest request, HttpResponseBuilder responseBuilder) {
		String fileRequested = request.getUri();
		String fullPath = this.resourcePath.concat(fileRequested);
		File testFile = new File(fullPath);
		if (!testFile.exists()) {
			try {
				testFile.createNewFile();
			} catch (IOException e) {
				responseBuilder.setStatus(500)
						.setPhrase(Protocol.getProtocol().getStringRep(Protocol.getProtocol().getCodeKeyword(500)));
			}
		} else if (testFile.isDirectory()) {
			responseBuilder.setStatus(400)
					.setPhrase(Protocol.getProtocol().getStringRep(Protocol.getProtocol().getCodeKeyword(400)));
		}
		FileWriter fw;
		try {
			fw = new FileWriter(testFile, true);
			System.out.println(request.getHeader());
			int amount = Integer
					.parseInt(request.getHeader().get(Protocol.getProtocol().getStringRep(Keywords.CONTENT_LENGTH)));
			fw.write(new String(request.getBody()), 0, amount);
			fw.close();
		} catch (IOException e) {
			responseBuilder.setStatus(500)
					.setPhrase(Protocol.getProtocol().getStringRep(Protocol.getProtocol().getCodeKeyword(500)));
		}
		responseBuilder.setStatus(200)
				.setPhrase(Protocol.getProtocol().getStringRep(Protocol.getProtocol().getCodeKeyword(200)))
				.setFile(testFile);
	}

	@Override
	public void doPut(HttpRequest request, HttpResponseBuilder responseBuilder) {
		String fileRequested = request.getUri();
		String fullPath = this.resourcePath.concat(fileRequested);
		File testFile = new File(fullPath);
		if (testFile.isDirectory()) {
			responseBuilder.setStatus(400)
					.setPhrase(Protocol.getProtocol().getStringRep(Protocol.getProtocol().getCodeKeyword(400)));
		}
		if (!testFile.exists()) {
			try {
				testFile.createNewFile();
			} catch (IOException e) {
				responseBuilder.setStatus(500)
						.setPhrase(Protocol.getProtocol().getStringRep(Protocol.getProtocol().getCodeKeyword(500)));
			}
		}
		FileWriter fw;
		try {
			fw = new FileWriter(testFile, false);
			int amount = Integer
					.parseInt(request.getHeader().get(Protocol.getProtocol().getStringRep(Keywords.CONTENT_LENGTH)));
			fw.write(new String(request.getBody()), 0, amount);
			fw.close();
		} catch (IOException e) {
			responseBuilder.setStatus(500)
					.setPhrase(Protocol.getProtocol().getStringRep(Protocol.getProtocol().getCodeKeyword(500)));
		}
		responseBuilder.setStatus(200)
				.setPhrase(Protocol.getProtocol().getStringRep(Protocol.getProtocol().getCodeKeyword(200)))
				.setFile(testFile);
	}

	@Override
	public void doDelete(HttpRequest request, HttpResponseBuilder responseBuilder) {
		// TODO Auto-generated method stub
		String uri = request.getUri();
		File file = new File(this.resourcePath + uri);
		if (file.exists()) {
			file.delete();
			responseBuilder.setStatus(204)
					.setPhrase(Protocol.getProtocol().getStringRep(Protocol.getProtocol().getCodeKeyword(204)))
					.setFile(file);
		}
		responseBuilder.setStatus(404)
				.setPhrase(Protocol.getProtocol().getStringRep(Protocol.getProtocol().getCodeKeyword(404)));
	}

	// Helper for doHead()
	private void getHeadResponseFromFile(File file, HttpResponseBuilder responseBuilder) {
		String lastModified = new Date(file.lastModified()).toString();
		String fileSize = String.valueOf(file.length());
		String fileType = FilenameUtils.getExtension(file.getAbsolutePath());
		responseBuilder.setStatus(200)
				.setPhrase(Protocol.getProtocol().getStringRep(Protocol.getProtocol().getCodeKeyword(200)))
				.setFile(file).putHeader("lastModified", lastModified).putHeader("fileSize", fileSize)
				.putHeader("fileType", fileType);
	}

}
