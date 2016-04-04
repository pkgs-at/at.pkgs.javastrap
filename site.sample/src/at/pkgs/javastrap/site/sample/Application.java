package at.pkgs.javastrap.site.sample;

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
		return "site.sample";
	}

	public static Application get() {
		return (Application)Kernel.get();
	}

}
