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
import java.util.ServiceLoader;
import java.io.File;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import at.pkgs.logging.Logger;
import at.pkgs.javastrap.core.utility.Lazy;

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
		// TODO
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
