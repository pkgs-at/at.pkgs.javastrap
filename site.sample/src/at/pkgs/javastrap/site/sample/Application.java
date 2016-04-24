package at.pkgs.javastrap.site.sample;

import java.net.URL;
import java.net.MalformedURLException;
import javax.servlet.ServletContextEvent;
import javax.servlet.annotation.WebListener;
import org.apache.commons.configuration.Configuration;
import at.pkgs.template.ResourceProvider;
import at.pkgs.javastrap.core.EnvironmentSettingSource;
import at.pkgs.javastrap.core.utility.Lazy;
import at.pkgs.javastrap.core.utility.PropertiesSource;
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

	private final Lazy<Configuration> configuration = new Lazy<Configuration>() {

		@Override
		protected Configuration initialize() {
			return Application.this.getConfiguration("application");
		}

	};

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

	public Configuration getApplicationConfiguration() {
		return this.configuration.get();
	}

	@Override
	protected PropertiesSource configureMessagePropertiesSource() {
		try {
			return new PropertiesSource.Resource(this.getServletContext().getResource("/WEB-INF/message.properties"))
					.scope(Application.class.getPackage())
					.chain(super.configureMessagePropertiesSource());
		}
		catch (MalformedURLException cause) {
			throw new RuntimeException(cause);
		}
	}

	@Override
	protected ResourceProvider configureTemplateResourceProvider() {
		return new ResourceProvider() {

			@Override
			public URL getResource(String path) {
				try {
					return Application.this.getServletContext().getResource(path);
				}
				catch (MalformedURLException cause) {
					throw new RuntimeException(cause);
				}
			}

		}.chain(super.configureTemplateResourceProvider());
	}

	public static Application get() {
		return (Application)Kernel.get();
	}

}
