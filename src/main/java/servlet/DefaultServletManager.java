package servlet;

import java.io.InputStream;
import java.net.URLClassLoader;

import protocol.HttpRequest;
import protocol.HttpResponse;
import protocol.HttpResponseBuilder;
import protocol.Keywords;
import protocol.Protocol;
import utils.SwsLogger;

public class DefaultServletManager extends AServletManager {
	
	private AHttpServlet defaultServlet;

	public DefaultServletManager(String filePath, ClassLoader cl) {
		super(filePath, cl);
	}

	@Override
	public void init() {
		// Create default servlet
		this.defaultServlet = new DefaultServlet(filePath);
	}

	@Override
	public void destroy() {
		// Tear down default servlet
		this.defaultServlet.destroy();
	}

	@Override
	public HttpResponse handleRequest(HttpRequest request) {
		
		if (request.getUri().contains("bork")) {
        	// plugin-borking easter egg
			enableBorkMode();
        } else if (request.getUri().contains("plsfix")) {
        	// plugin-unborking easter egg
			disableBorkMode();
        }
		
		if (this.borkMode) {
			SwsLogger.errorLogger.error("BORK MODE enabled, borking request!");
			String bork = null;
			bork.indexOf("bork");
		}
		
		HttpResponseBuilder responseBuilder = new HttpResponseBuilder();

		if (request.getMethod().equals(Protocol.getProtocol().getStringRep(Keywords.GET))) {
			this.defaultServlet.doGet(request, responseBuilder);
		} else if (request.getMethod().equals(Protocol.getProtocol().getStringRep(Keywords.HEAD))) {
			this.defaultServlet.doHead(request, responseBuilder);
		} else if (request.getMethod().equals(Protocol.getProtocol().getStringRep(Keywords.POST))) {
			this.defaultServlet.doPost(request, responseBuilder);
		} else if (request.getMethod().equals(Protocol.getProtocol().getStringRep(Keywords.PUT))) {
			this.defaultServlet.doPut(request, responseBuilder);
		} else if (request.getMethod().equals(Protocol.getProtocol().getStringRep(Keywords.DELETE))) {
			this.defaultServlet.doDelete(request, responseBuilder);
		}
		
		// TODO: verify that response is okay?
		
		return responseBuilder.generateResponse();
	}
}
