package servlet;

import java.io.File;

import protocol.HttpRequest;
import protocol.HttpResponse;
import protocol.HttpResponseBuilder;
import protocol.Keywords;
import protocol.Protocol;

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
			responseBuilder.setStatus(404);
		} else if (file.isDirectory()) {
			// check for default file before sending 404
			String location = this.resourcePath.concat(uri).concat(System.getProperty("file.separator"))
					.concat(Protocol.getProtocol().getStringRep(Keywords.DEFAULT_FILE));
			file = new File(location);
			if (file.exists()) {
				responseBuilder.setStatus(200)
						.putHeader(Protocol.getProtocol().getStringRep(Keywords.CONTENT_TYPE), "text/html")
						.setFile(file).generateResponse();
			} else {
				responseBuilder.setStatus(404);
			}
		} else {
			responseBuilder.setStatus(200)
					.putHeader(Protocol.getProtocol().getStringRep(Keywords.CONTENT_TYPE), "text/html").setFile(file)
					.generateResponse();
		}
	}

	@Override
	public void doHead(HttpRequest request, HttpResponseBuilder responseBuilder) {
		// TODO Auto-generated method stub

	}

	@Override
	public void doPost(HttpRequest request, HttpResponseBuilder responseBuilder) {
		// TODO Auto-generated method stub

	}

	@Override
	public void doPut(HttpRequest request, HttpResponseBuilder responseBuilder) {
		// TODO Auto-generated method stub

	}

	@Override
	public void doDelete(HttpRequest request, HttpResponseBuilder responseBuilder) {
		// TODO Auto-generated method stub

	}

}
