package at.pkgs.javastrap.tool.sample;

import java.sql.SQLException;
import org.h2.tools.Shell;
import at.pkgs.javastrap.core.EnvironmentSettingSource;
import at.pkgs.javastrap.core.sample.Kernel;

public class Application extends Kernel {

	@Override
	protected EnvironmentSettingSource configureEnvironmentSettingSource() {
		return EnvironmentSettingSource.configure()
				.fromSystemProperties()
				.fromEnvironmentVariables()
				.get();
	}

	@Override
	public String getApplicationName() {
		return "tool.sample";
	}

	public int database(String... arguments) {
		Shell shell;

		shell = new Shell();
		try {
			shell.runTool(this.getDataSource().getConnection(), arguments);
		}
		catch (SQLException cause) {
			cause.printStackTrace(System.err);
			return 1;
		}
		return 0;
	}

	public int program(String... arguments) {
		return this.database(arguments);
	}

	public static Application get() {
		return (Application)Kernel.get();
	}

	public static void main(String[] arguments) {
		Application.get().initialize();
		System.exit(Application.get().program(arguments));
	}

}
