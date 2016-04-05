package at.pkgs.javastrap.tool.sample;

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

	public int program(String... arguments) {
		this.getDataSource();
		return 0;
	}

	public static Application get() {
		return (Application)Kernel.get();
	}

	public static void main(String[] arguments) {
		Application.get().initialize();
		System.exit(Application.get().program(arguments));
	}

}
