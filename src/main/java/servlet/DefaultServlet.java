package servlet;

import protocol.HttpRequest;
import protocol.HttpResponseBuilder;

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
		// TODO Auto-generated method stub
		
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
