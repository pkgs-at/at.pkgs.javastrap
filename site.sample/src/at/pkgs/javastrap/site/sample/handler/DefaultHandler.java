package at.pkgs.javastrap.site.sample.handler;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebFilter;
import at.pkgs.javastrap.site.sample.ApplicationHandler;

public class DefaultHandler extends ApplicationHandler {

	@WebFilter(urlPatterns = {"/default.htpl"})
	public static class Filter extends ApplicationHandler.Filter {

		// nothing

	}

	@Override
	protected void handle() throws ServletException, IOException {
		// TODO
	}

}
