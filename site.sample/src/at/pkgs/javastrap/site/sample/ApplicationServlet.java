package at.pkgs.javastrap.site.sample;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import at.pkgs.javastrap.core.sample.KernelServlet;
import at.pkgs.web.http.HttpRequest;
import at.pkgs.web.http.HttpResponse;

@WebServlet(name = "htpl", urlPatterns = {"*.htpl"})
public class ApplicationServlet extends KernelServlet implements ApplicationHolder {

	private static final long serialVersionUID = 1L;

	@Override
	protected ApplicationHandler newDefaultHandler(
			HttpRequest request,
			HttpResponse response)
					throws ServletException, IOException {
		return new ApplicationHandler() {

			@Override
			protected void handle()
					throws ServletException, IOException {
				// do nothing
			}

		};
	}

}
