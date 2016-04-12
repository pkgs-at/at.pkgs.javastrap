package at.pkgs.javastrap.core.sample.model;

import java.sql.Timestamp;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import at.pkgs.sql.trifle.Query;
import at.pkgs.javastrap.core.utility.Connecter;

public class Employee extends AbstractModel<Employee.Column> implements EmployeeContent {

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

		public static final Query.Identifier TABLE = Query.identifierOf("t_employee");

		private Via() {
			// do nothing
		}

		@Override
		protected void from(Query query) {
			query.append(Via.TABLE);
		}

		public long create(
				Connection connection,
				EmployeeContent content)
						throws SQLException {
			try (Connecter connecter = this.connecter(connection)) {
				Timestamp now;
				ResultSet result;

				now = this.getDatabase().getCurrentTimestamp(connecter.get());
				result = this.query()
						.append("INSERT INTO ", Via.TABLE)
						.clause(
								'(', ", ", ')', null,
								Column.FAMILY_NAME,
								Column.GIVEN_NAME,
								Column.MAIL_ADDRESS,
								Column.TELEPHONE_NUMBER,
								Column.CREATED_AT,
								Column.UPDATED_AT)
						.clause(
								" VALUES(", ", ", ')', null,
								Query.valueOf(content.getFamilyName()),
								Query.valueOf(content.getGivenName()),
								Query.valueOf(content.getMailAddress()),
								Query.valueOf(content.getTelephoneNumber()),
								Query.valueOf(now),
								Query.valueOf(now))
						.append(" RETURNING ", Column.EMPLOYEE_ID)
						.prepare(connecter.get())
						.executeQuery();
				if (!result.next()) throw new RuntimeException("cannot fetch result");
				return result.getLong(1);
			}
		}

		public int update(
				Connection connection,
				long employeeId,
				EmployeeContent content)
						throws SQLException {
			try (Connecter connecter = this.connecter(connection)) {
				Timestamp now;

				now = this.getDatabase().getCurrentTimestamp(connecter.get());
				return this.query()
						.append("UPDATE ", Via.TABLE)
						.clause(
								" SET ", ", ", null, null,
								new Query.Parts(
										Column.FAMILY_NAME,
										" = ",
										Query.valueOf(content.getFamilyName())),
								new Query.Parts(
										Column.GIVEN_NAME,
										" = ",
										Query.valueOf(content.getGivenName())),
								new Query.Parts(
										Column.MAIL_ADDRESS,
										" = ",
										Query.valueOf(content.getMailAddress())),
								new Query.Parts(
										Column.TELEPHONE_NUMBER,
										" = ",
										Query.valueOf(content.getTelephoneNumber())),
								new Query.Parts(
										Column.UPDATED_AT,
										" = ",
										Query.valueOf(now)))
						.append(
								" WHERE ",
								new Query.Equal(
										Column.EMPLOYEE_ID,
										employeeId))
						.prepare(connecter.get())
						.executeUpdate();
			}
		}

	}

	private static final long serialVersionUID = 1L;

	public static final Via VIA = new Via();

	public long getEmployeeId() {
		return this.get(Column.EMPLOYEE_ID);
	}

	@Override
	public String getFamilyName() {
		return this.get(Column.FAMILY_NAME);
	}

	@Override
	public String getGivenName() {
		return this.get(Column.GIVEN_NAME);
	}

	@Override
	public String getMailAddress() {
		return this.get(Column.MAIL_ADDRESS);
	}

	@Override
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
