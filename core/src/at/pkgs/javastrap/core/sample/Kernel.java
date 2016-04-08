package at.pkgs.javastrap.core.sample;

import java.util.Properties;
import java.io.File;
import java.sql.SQLException;
import javax.sql.DataSource;
import at.pkgs.javastrap.core.Core;
import at.pkgs.javastrap.core.utility.Lazy;
import at.pkgs.javastrap.core.sample.model.Database;

public abstract class Kernel extends Core {

	private final Lazy<DataSource> dataSource = new Lazy<DataSource>() {

		@Override
		protected DataSource initialize() {
			Properties properties;

			properties = new Properties();
			properties.setProperty(
					"url",
					"jdbc:h2:file:" + Kernel.this.getDataFile("database"));
			return Kernel.this.configureDataSource("dataSource", properties);
		}

	};

	@Override
	public String getSystemName() {
		return "at.pkgs.javastrap";
	}

	@Override
	public File getConfigurationDirectory() {
		return new File(this.getRootDirectory(), "config.sample");
	}

	public File getDataDirectory() {
		return new File(this.getRootDirectory(), "data.sample");
	}

	public File getDataFile(String name) {
		return new File(this.getDataDirectory(), name);
	}

	public DataSource getDataSource() {
		return this.dataSource.get();
	}

	@Override
	public void initialize() {
		super.initialize();
		System.out.println(this.getRootDirectory());
		/*
		try {
			if (Database.VIA.countTableBySchema(null, "PUBLIC") <= 0)
				Database.VIA.script(null, Kernel.class.getPackage().getName(), "data/0000.sql");
		}
		catch (SQLException cause) {
			throw new RuntimeException("failed on prepare database", cause);
		}
		 */
	}

	public static Kernel get() {
		return (Kernel)Core.get();
	}

}
