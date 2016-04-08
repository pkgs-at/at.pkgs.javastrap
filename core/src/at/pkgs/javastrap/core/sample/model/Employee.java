package at.pkgs.javastrap.core.sample.model;

import java.sql.Timestamp;
import at.pkgs.sql.trifle.Query;

public class Employee extends AbstractModel<Employee.Column> {

	public static enum Column implements AbstractModel.Column {

		EMPLOYEE_ID,

		FAMILY_NAME,

		GIVEN_NAME,

		MAIL_ADDRESS,

		TELEPHONE_NUMBER,

		CREATED_AT,

		UPDATED_AT,

	}

	public static class Via extends AbstractModel.Via<Employee> {

		private Via() {
			// do nothing
		}

		@Override
		protected void from(Query query) {
			query.identifier("t_employee");
		}

	}

	private static final long serialVersionUID = 1L;

	public static final Via VIA = new Via();

	public long getEmployeeId() {
		return this.get(Column.EMPLOYEE_ID);
	}

	public String getFamilyName() {
		return this.get(Column.FAMILY_NAME);
	}

	public String getGivenName() {
		return this.get(Column.GIVEN_NAME);
	}

	public String getMailAddress() {
		return this.get(Column.MAIL_ADDRESS);
	}

	public String getTelephoneNumber() {
		return this.get(Column.TELEPHONE_NUMBER);
	}

	public Timestamp getCreatedAt() {
		return this.get(Column.CREATED_AT);
	}

	public Timestamp getUpdatedAt() {
		return this.get(Column.UPDATED_AT);
	}

}
