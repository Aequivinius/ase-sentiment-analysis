package ch.uzh.ifi.seal.ase.group3.worker.sentimentworker;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import javax.servlet.ServletContext;

public class ServletFileUtil {

	private static ServletFileUtil instance;
	private ServletContext context;

	private ServletFileUtil() {
	}

	public static ServletFileUtil getInstance() {
		if (instance == null) {
			instance = new ServletFileUtil();
		}

		return instance;
	}

	public boolean isConfigured() {
		return context != null;
	}

	public void configure(ServletContext servletContext) {
		this.context = servletContext;
	}

	public InputStream getFileStream(String name) {
		if (context == null) {
			// case when runnung in dev mode
			try {
				return new FileInputStream(new File("WebContent/resources", name));
			} catch (FileNotFoundException e) {
				return null;
			}
		} else {
			// case when deployed
			return context.getResourceAsStream("/resources/" + name);
		}
	}
}
