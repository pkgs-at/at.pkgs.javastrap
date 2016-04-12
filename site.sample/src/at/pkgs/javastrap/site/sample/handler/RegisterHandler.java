package at.pkgs.javastrap.site.sample.handler;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebFilter;
import at.pkgs.javastrap.site.sample.ApplicationHandler;

public class RegisterHandler extends ApplicationHandler {

	public static enum Action {

		TOKEN,

		INPUT,

		SUBMIT,

	}

	@WebFilter(urlPatterns = {"/register.htpl"})
	public static class Filter extends ApplicationHandler.Filter {

		// nothing

	}

	protected void input() throws ServletException, IOException {
		// TODO
	}

	protected void submit() throws ServletException, IOException {
		// TODO
		this.getResponse().sendRedirect("/");
		this.finish();
	}

	@Override
	protected void handle() throws ServletException, IOException {
		switch (this.dispatch(Action.INPUT)) {
		case TOKEN :
			this.token();
			break;
		case INPUT :
			this.input();
			break;
		case SUBMIT :
			this.submit();
			break;
		}
	}

}
