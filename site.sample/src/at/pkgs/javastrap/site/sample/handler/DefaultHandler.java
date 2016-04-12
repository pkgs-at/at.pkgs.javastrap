package at.pkgs.javastrap.site.sample.handler;

import java.io.IOException;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebFilter;
import at.pkgs.sql.trifle.Query;
import at.pkgs.javastrap.site.sample.ApplicationHandler;
import at.pkgs.javastrap.core.sample.model.Employee;

public class DefaultHandler extends ApplicationHandler {

	@WebFilter(urlPatterns = {"/default.htpl"})
	public static class Filter extends ApplicationHandler.Filter {

		// nothing

	}

	@Override
	protected void handle() throws ServletException, IOException {
		try {
			this.getResponse().setParameter(
					"employees",
					Employee.VIA.retrieveAll(
							new Query.OrderBy()
									.ascending(Employee.Column.FAMILY_NAME)
									.ascending(Employee.Column.GIVEN_NAME)
									.ascending(Employee.Column.EMPLOYEE_ID)));
		}
		catch (SQLException cause) {
			throw new RuntimeException(cause);
		}
	}

}
