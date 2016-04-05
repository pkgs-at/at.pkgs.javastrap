/*
 * Copyright (c) 2009-2016, Architector Inc., Japan
 * All rights reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package at.pkgs.javastrap.core;

import java.util.List;
import java.util.ArrayList;
import java.util.Properties;
import java.util.ServiceLoader;
import java.io.File;
import java.net.MalformedURLException;
import javax.sql.DataSource;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.CombinedConfiguration;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.tree.OverrideCombiner;
import org.apache.commons.dbcp2.BasicDataSourceFactory;
import net.sf.log4jdbc.sql.jdbcapi.DataSourceSpy;
import at.pkgs.logging.Logger;
import at.pkgs.javastrap.core.utility.Lazy;
import at.pkgs.javastrap.core.utility.Configurations;

public abstract class Core implements at.pkgs.logging.Loggable {

	public static class ServletContextListener implements javax.servlet.ServletContextListener {

		@Override
		public void contextInitialized(ServletContextEvent event) {
			Core.get().setServletContext(event.getServletContext());
		}

		@Override
		public void contextDestroyed(ServletContextEvent event) {
			// do nothing
		}

	}

	private final Lazy<EnvironmentSettingSource> environmentSettingSource = new Lazy<EnvironmentSettingSource>() {

		@Override
		protected EnvironmentSettingSource initialize() {
			return Core.this.configureEnvironmentSettingSource();
		}

	};

	private final Lazy<File> rootDirectory = new Lazy<File>() {

		@Override
		protected File initialize() {
			return new File(Core.this.getSystemSetting("root_directory", "")).getAbsoluteFile();
		}

	};

	private final Lazy<Logger> logger = new Lazy<Logger>() {

		@Override
		protected Logger initialize() {
			return at.pkgs.logging.LoggerFactory.get(Core.this);
		}

	};

	private final Lazy<CombinedConfiguration> configuration = new Lazy<CombinedConfiguration>() {

		@Override
		protected CombinedConfiguration initialize() {
			OverrideCombiner combiner;
			CombinedConfiguration configuration;

			combiner = new OverrideCombiner();
			configuration = new CombinedConfiguration(combiner);
			for (File file : Core.this.getConfigulationFiles("system", "xml")) {
				Core.this.debug("loading configuration from %s", file);
				try {
					configuration.addConfiguration(new XMLConfiguration(file.toURI().toURL()));
				}
				catch (ConfigurationException cause) {
					throw new RuntimeException(String.format("configuration error: %s", file), cause);
				}
				catch (MalformedURLException cause) {
					throw new RuntimeException(cause);
				}
			}
			return configuration;
		}

	};

	private ServletContext servletContext;

	protected abstract EnvironmentSettingSource configureEnvironmentSettingSource();

	public String getEnvironmentSetting(String name, String alternative) {
		String value;

		value = this.environmentSettingSource.get().get(name);
		if (value != null) return value;
		return alternative;
	}

	public String getSystemSetting(String name, String alternative) {
		return this.getEnvironmentSetting(this.getSystemName() + '.' + name, alternative);
	}

	public abstract String getSystemName();

	public abstract String getApplicationName();

	public File getRootDirectory() {
		return this.rootDirectory.get();
	}

	public File getConfigurationDirectory() {
		return new File(this.getRootDirectory(), "config");
	}

	public File getConfigulationFile(String name) {
		File file;

		file = new File(this.getConfigurationDirectory(), name);
		if (!file.exists() || !file.isFile()) return null;
		if (!file.canRead()) return null;
		return file;
	}

	public List<File> getConfigulationFiles(String prefix, String suffix) {
		List<File> files;

		files = new ArrayList<File>();
		files.add(this.getConfigulationFile(prefix + '.' + this.getApplicationName() + ".local." + suffix));
		files.add(this.getConfigulationFile(prefix + '.' + this.getApplicationName() + '.' + suffix));
		files.add(this.getConfigulationFile(prefix + ".local." + suffix));
		files.add(this.getConfigulationFile(prefix + '.' + suffix));
		while (files.remove(null));
		return files;
	}

	public Logger getLogger() {
		return this.logger.get();
	}

	public ServletContext getServletContext() {
		return this.servletContext;
	}

	private void setServletContext(ServletContext context) {
		this.servletContext = context;
	}

	protected CombinedConfiguration getConfiguration() {
		return this.configuration.get();
	}

	public Configuration getConfiguration(String key) {
		return this.getConfiguration().subset(key);
	}

	protected DataSource configureDataSource(String key, Properties defaults) {
		Configuration configuration;
		Properties properties;
		StringBuilder connectionProperties;
		DataSource source;

		configuration = this.getConfiguration(key);
		properties = (defaults == null) ? new Properties() : new Properties(defaults);
		for (Configurations.Entry entry : Configurations.iterable(configuration)) {
			if (entry.getKey().startsWith("[@")) continue;
			if (entry.getKey().startsWith("connectionProperties.")) continue;
			properties.setProperty(entry.getKey(), entry.getString());
		}
		connectionProperties = new StringBuilder();
		for (Configurations.Entry entry : Configurations.iterable(configuration.subset("connectionProperties"))) {
			connectionProperties.append(entry.getKey()).append('=');
			connectionProperties.append(entry.getString()).append(';');
		}
		properties.setProperty("connectionProperties", connectionProperties.toString());
		if (configuration.getBoolean("[@verbose]", false)) {
			System.out.println(" * DataSource properties: " + key);
			Configurations.dump(properties, System.out);
		}
		try {
			source = BasicDataSourceFactory.createDataSource(properties);
		}
		catch (Exception cause) {
			throw new RuntimeException(cause);
		}
		if (configuration.getBoolean("[@log4jdbc]", false)) {
			return new DataSourceSpy(source);
		}
		else {
			return source;
		}
	}

	protected DataSource configureDataSource(String key) {
		return this.configureDataSource(key, null);
	}

	public void initialize() {
		for (File file : this.getConfigulationFiles("log4j2", "xml")) {
			at.pkgs.logging.log4j.LoadableConfigurationFactory.load(file);
			this.information(
					"logger configuration reloaded from: %s",
					file);
			break;
		}
		this.information(
				"%s.%s initializing with %s",
				this.getSystemName(),
				this.getApplicationName(),
				this.getRootDirectory());
		if (this.getConfiguration().getBoolean("[@verbose]", false)) {
			System.out.println(" * configuration");
			Configurations.dump(this.getConfiguration(), System.out);
		}
	}

	private static final Lazy<Core> instance = new Lazy<Core>() {

		@Override
		protected Core initialize() {
			List<Core> services;

			services = new ArrayList<Core>();
			for (Core service : ServiceLoader.load(Core.class)) {
				services.add(service);
			}
			if (services.size() < 1)
				throw new RuntimeException(Core.class.getName() + " provider not found");
			if (services.size() > 1) {
				StringBuilder builder;

				builder = new StringBuilder(Core.class.getName() + " provider conflicted:");
				for (Core service : services)
					builder.append(' ').append(service.getClass().getName());
				throw new RuntimeException(builder.toString());
			}
			return services.get(0);
		}

	};

	public static Core get() {
		return Core.instance.get();
	}

}
