package at.pkgs.javastrap.core.sample;

import java.util.Properties;
import java.io.File;
import javax.sql.DataSource;
import at.pkgs.javastrap.core.Core;
import at.pkgs.javastrap.core.utility.Lazy;

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

	public static Kernel get() {
		return (Kernel)Core.get();
	}

}
