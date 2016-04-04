package at.pkgs.javastrap.site.sample;

import javax.servlet.ServletContextEvent;
import javax.servlet.annotation.WebListener;
import at.pkgs.javastrap.core.EnvironmentSettingSource;
import at.pkgs.javastrap.core.sample.Kernel;

public class Application extends Kernel {

	@WebListener
	public static class ServletContextListener extends Kernel.ServletContextListener {

		@Override
		public void contextInitialized(ServletContextEvent event) {
			super.contextInitialized(event);
			Application.get().initialize();
		}

		@Override
		public void contextDestroyed(ServletContextEvent event) {
			super.contextDestroyed(event);
		}

	}

	@Override
	protected EnvironmentSettingSource configureEnvironmentSettingSource() {
		return EnvironmentSettingSource.configure()
				.fromServletContext(this.getServletContext())
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
