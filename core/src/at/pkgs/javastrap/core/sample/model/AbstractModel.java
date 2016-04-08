package at.pkgs.javastrap.core.sample.model;

import java.sql.Connection;
import java.sql.SQLException;
import at.pkgs.sql.trifle.Model;
import at.pkgs.sql.trifle.Query;
import at.pkgs.javastrap.core.utility.Connecter;

public class AbstractModel<ColumnType extends AbstractModel.Column> extends Model<ColumnType> {

	public static interface Column extends Model.Column {

		public String name();

		public default void expression(Query query) {
			query.identifier(this.name().toLowerCase());
		}

	}

	public static abstract class Function<ResultType> extends Database.Function<ResultType> {

		// nothing

	}

	public static abstract class Action extends Database.Action {

		// nothing

	}

	public static abstract class Via<ModelType extends AbstractModel<?>> extends Model.Via<ModelType> {

		@Override
		protected Database getDatabase() {
			return Database.VIA;
		}

		protected Connecter connecter(Connection connection) {
			return new Connecter(connection) {

				@Override
				protected Connection connect() throws SQLException {
					return Via.this.getConnection();
				}

			};
		}

	}

	private static final long serialVersionUID = 1L;

}
