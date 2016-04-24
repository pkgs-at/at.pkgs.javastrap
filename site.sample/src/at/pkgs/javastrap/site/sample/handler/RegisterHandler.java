package at.pkgs.javastrap.site.sample.handler;

import javax.servlet.annotation.WebFilter;
import at.pkgs.javastrap.core.sample.model.Employee;
import at.pkgs.javastrap.site.sample.ApplicationHandler;
import at.pkgs.javastrap.site.sample.model.EmployeeForm;

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

	protected void input() throws Exception {
		// do nothing
	}

	protected void submit() throws Exception {
		EmployeeForm form;

		if (this.noneOf(HttpMethod.POST))
			throw new ErrorResponse(
					ClientError.MethodNotAllowed);
		if (!this.validateToken("token"))
			throw new ErrorResponse(
					ClientError.BadRequest,
					RegisterHandler.MESSAGE_INVALID_TOKEN);
		form = this.decode(
				this.getRequest().getParameter("data"),
				EmployeeForm.class);
		Employee.VIA.create(form);
		this.redirect(this.location().path("/default.htpl"));
	}

	@Override
	protected void handle() throws Exception {
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
