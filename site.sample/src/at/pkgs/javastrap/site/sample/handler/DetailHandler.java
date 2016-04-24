package at.pkgs.javastrap.site.sample.handler;

import javax.servlet.annotation.WebFilter;
import at.pkgs.javastrap.site.sample.ApplicationHandler;
import at.pkgs.javastrap.core.sample.model.Employee;

public class DetailHandler extends ApplicationHandler {

	@WebFilter(urlPatterns = {"/detail.htpl"})
	public static class Filter extends ApplicationHandler.Filter {

		// nothing

	}

	@Override
	protected void handle() throws Exception {
		Collector collector;
		Long employeeId;
		Employee employee;

		collector = new Collector();
		employeeId = collector.new LongField("employee_id") {{
			required();
		}}.value();
		if (collector.getResult().hasError())
			throw new ErrorResponse(ClientError.BadRequest);
		employee = Employee.VIA.retrieveByEmployeeId(employeeId);
		if (employee == null) throw new ErrorPage(ClientError.NotFound);
		this.getResponse().setParameter("employee", employee);
	}

}
