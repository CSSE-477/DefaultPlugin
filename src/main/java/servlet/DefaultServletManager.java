package servlet;

public class DefaultServletManager extends AServletManager {
	public DefaultServletManager(String filePath, ClassLoader cl) {
		super(filePath, cl);
	}

	@Override
	public void init() {
		// Nothing to do
	}

}
