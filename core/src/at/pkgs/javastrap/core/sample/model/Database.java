package at.pkgs.javastrap.core.sample.model;

import java.sql.Timestamp;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import at.pkgs.sql.trifle.AbstractDatabase;
import at.pkgs.sql.trifle.dialect.Dialect;
import at.pkgs.javastrap.core.utility.Connecter;
import at.pkgs.javastrap.core.sample.Kernel;

public class Database extends AbstractDatabase {

	public static class Transaction extends AbstractDatabase.Transaction {

		@Override
		public Isolation isolation() {
			return Isolation.REPEATABLE_READ;
		}

		@Override
		public int retry() {
			return 3;
		}

		@Override
		public long interval() {
			return super.interval() / 2L;
		}

	}

	public static abstract class Function<ResultType> extends Transaction implements AbstractDatabase.Function<ResultType> {

		// nothing

	}

	public static abstract class Action extends Transaction implements AbstractDatabase.Action {

		// nothing

	}

	private final Dialect dialect;

	private Database() {
		this.dialect = new at.pkgs.sql.trifle.dialect.H2Database();
	}

	@Override
	protected Dialect getDialect() {
		return this.dialect;
	}

	@Override
	protected Connection getConnection() throws SQLException {
		return Kernel.get().getDataSource().getConnection();
	}

	protected Connecter connecter(Connection connection) {
		return new Connecter(connection) {

			@Override
			protected Connection connect() throws SQLException {
				return Database.this.getConnection();
			}

		};
	}

	public void script(
			Connection connection,
			String location)
					throws SQLException {
		try (Connecter connecter = this.connecter(connection)) {
			this.query()
					.append("RUNSCRIPT FROM ")
					.quoted(location)
					.append(" CHARSET ")
					.quoted("UTF-8")
					.prepare(connecter.get())
					.executeUpdate();
		}
	}

	public void script(
			String location)
					throws SQLException {
		this.script((Connection)null, location);
	}

	public void script(
			Connection connection,
			String namespace,
			String file)
					throws SQLException {
		StringBuilder location;

		location = new StringBuilder("classpath:/");
		location.append(namespace.replace('.',  '/'));
		location.append('/');
		location.append(file);
		this.script(connection, location.toString());
	}

	public void script(
			String namespace,
			String file)
					throws SQLException {
		this.script(null, namespace, file);
	}

	public int countTableBySchema(
			Connection connection,
			String schema)
					throws SQLException {
		try (Connecter connecter = this.connecter(connection)) {
			ResultSet result;

			result = this.query()
					.append("SELECT ALL COUNT(*)")
					.append(" FROM ").identifier("INFORMATION_SCHEMA").append(".").identifier("TABLES")
					.append(" WHERE ").identifier("TABLE_SCHEMA").append(" = ").value(schema)
					.prepare(connecter.get())
					.executeQuery();
			if (!result.next()) return 0;
			return result.getInt(1);
		}
	}

	public int countTableBySchema(
			String schema)
					throws SQLException {
		return this.countTableBySchema(null, schema);
	}

	public Timestamp getCurrentTimestamp(
			Connection connection)
					throws SQLException {
		try (Connecter connecter = this.connecter(connection)) {
			ResultSet result;

			result = this.query()
					.append("SELECT ALL CURRENT_TIMESTAMP")
					.prepare(connecter.get())
					.executeQuery();
			if (!result.next()) throw new RuntimeException("cannot fetch result");;
			return result.getTimestamp(1);
		}
	}

	public Timestamp getCurrentTimestamp() throws SQLException {
		return this.getCurrentTimestamp(null);
	}

	public static final Database VIA = new Database();

}
