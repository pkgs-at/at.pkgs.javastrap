package at.pkgs.javastrap.core.sample;

import java.io.IOException;
import javax.servlet.ServletException;
import at.pkgs.web.http.HttpRequest;
import at.pkgs.web.http.HttpResponse;
import at.pkgs.javastrap.core.CoreServlet;

public class KernelServlet extends CoreServlet implements KernelHolder {

	private static final long serialVersionUID = 1L;

	@Override
	protected KernelHandler newDefaultHandler(
			HttpRequest request,
			HttpResponse response)
					throws ServletException, IOException {
		return new KernelHandler() {

			@Override
			protected void handle()
					throws ServletException, IOException {
				// do nothing
			}

		};
	}

}
