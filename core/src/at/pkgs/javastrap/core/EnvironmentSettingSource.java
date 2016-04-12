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

public interface EnvironmentSettingSource {

	public static class EnvironmentVariables implements EnvironmentSettingSource {

		public static class Builder extends EnvironmentSettingSource.Builder {

			public Builder(EnvironmentSettingSource.Builder previous) {
				super(previous);
			}

			@Override
			protected EnvironmentSettingSource create(EnvironmentSettingSource defaults) {
				return new EnvironmentVariables(defaults);
			}

		}

		private final EnvironmentSettingSource defaults;

		public EnvironmentVariables(EnvironmentSettingSource defaults) {
			this.defaults = defaults;
		}

		public EnvironmentVariables() {
			this(null);
		}

		public String get(String name) {
			String value;

			value = System.getenv(name.toUpperCase().replace('.', '_'));
			if (value != null) return value;
			if (this.defaults != null) return this.defaults.get(name);
			return null;
		}

	}

	public static class SystemProperties implements EnvironmentSettingSource {

		public static class Builder extends EnvironmentSettingSource.Builder {

			public Builder(EnvironmentSettingSource.Builder previous) {
				super(previous);
			}

			@Override
			protected EnvironmentSettingSource create(EnvironmentSettingSource defaults) {
				return new SystemProperties(defaults);
			}

		}

		private final EnvironmentSettingSource defaults;

		public SystemProperties(EnvironmentSettingSource defaults) {
			this.defaults = defaults;
		}

		public SystemProperties() {
			this(null);
		}

		public String get(String name) {
			String value;

			value = System.getProperty(name);
			if (value != null) return value;
			if (this.defaults != null) return this.defaults.get(name);
			return null;
		}

	}

	public static class ServletContext implements EnvironmentSettingSource {

		public static class Builder extends EnvironmentSettingSource.Builder {

			private final javax.servlet.ServletContext context;

			public Builder(EnvironmentSettingSource.Builder previous, javax.servlet.ServletContext context) {
				super(previous);
				this.context = context;
			}

			@Override
			protected EnvironmentSettingSource create(EnvironmentSettingSource defaults) {
				return new ServletContext(this.context, defaults);
			}

		}

		private final javax.servlet.ServletContext context;

		private final EnvironmentSettingSource defaults;

		public ServletContext(javax.servlet.ServletContext context, EnvironmentSettingSource defaults) {
			this.context = context;
			this.defaults = defaults;
		}

		public ServletContext(javax.servlet.ServletContext context) {
			this(context, null);
		}

		public String get(String name) {
			String value;

			value = this.context.getInitParameter(name);
			if (value != null) return value;
			if (this.defaults != null) return this.defaults.get(name);
			return null;
		}

	}

	public class Builder {

		private final Builder previous;

		public Builder(Builder previous) {
			this.previous = previous;
		}

		public Builder() {
			this(null);
		}

		public Builder fromSystemProperties() {
			return new SystemProperties.Builder(this);
		}

		public Builder fromEnvironmentVariables() {
			return new EnvironmentVariables.Builder(this);
		}

		public Builder fromServletContext(javax.servlet.ServletContext context) {
			return new ServletContext.Builder(this, context);
		}

		protected EnvironmentSettingSource create(EnvironmentSettingSource defaults) {
			return defaults;
		}

		protected EnvironmentSettingSource wrap(EnvironmentSettingSource defaults) {
			if (this.previous == null) return this.create(defaults);
			else return this.previous.wrap(this.create(defaults));
		}

		public EnvironmentSettingSource get() {
			return this.wrap(null);
		}

	}

	public String get(String name);

	public static Builder configure() {
		return new Builder();
	}

}
