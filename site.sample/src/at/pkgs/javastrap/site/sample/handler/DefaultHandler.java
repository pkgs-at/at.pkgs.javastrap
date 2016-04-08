package at.pkgs.javastrap.site.sample.handler;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebFilter;
import at.pkgs.javastrap.core.sample.KernelHandler;

public class DefaultHandler extends KernelHandler {

	@WebFilter(urlPatterns = {"/default.htpl"})
	public static class Filter {
		
	}

	@Override
	protected void handle() throws ServletException, IOException {
		
	}

}
