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

package at.pkgs.javastrap.core.utility;

import java.util.List;
import java.util.Properties;
import java.util.Arrays;
import java.util.Collections;
import java.io.InputStream;
import java.io.BufferedInputStream;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.net.URL;

public interface PropertiesSource {

	public static class Resource implements PropertiesSource {

		private final URL resource;

		public Resource(URL resource) {
			this.resource = resource;
		}

		@Override
		public Properties load(Properties defaults) {
			Properties properties;

			properties = (defaults == null) ? new Properties() : new Properties(defaults);
			if (this.resource.getPath().endsWith(".xml")) {
				try (InputStream source = new BufferedInputStream(this.resource.openStream())) {
					properties.loadFromXML(source);
				}
				catch (IOException cause) {
					throw new RuntimeException(
							"cannot load xml properties from: " + this.resource.toString(),
							cause);
				}
			}
			else {
				try (Reader source = new BufferedReader(new InputStreamReader(this.resource.openStream(), StandardCharsets.UTF_8))) {
					properties.load(source);
				}
				catch (IOException cause) {
					throw new RuntimeException(
							"cannot load text properties from: " + this.resource.toString(),
							cause);
				}
			}
			return properties;
		}

	}

	public static class Prefix implements PropertiesSource {

		private final String prefix;

		private final PropertiesSource source;

		public Prefix(String prefix, PropertiesSource source) {
			this.prefix = prefix;
			this.source = source;
		}

		@Override
		public Properties load(Properties defaults) {
			Properties properties;
			Properties source;

			properties = (defaults == null) ? new Properties() : new Properties(defaults);
			source = this.source.load();
			for (String key : source.stringPropertyNames())
				properties.setProperty(this.prefix + key, source.getProperty(key));
			return properties;
		}

	}

	public static class Chain implements PropertiesSource {

		private final List<PropertiesSource> sources;

		public Chain(PropertiesSource... sources) {
			List<PropertiesSource> list;

			list = Arrays.asList(sources);
			Collections.reverse(list);
			this.sources = Collections.unmodifiableList(list);
		}

		@Override
		public Properties load(Properties defaults) {
			for (PropertiesSource source : this.sources)
				defaults = source.load(defaults);
			return defaults;
		}

	}

	public Properties load(Properties defaults);

	public default Properties load() {
		return this.load(null);
	}

	public default PropertiesSource chain(PropertiesSource defaults) {
		return new Chain(this, defaults);
	}

	public default PropertiesSource prefix(String prefix) {
		return new Prefix(prefix, this);
	}

	public default PropertiesSource scope(String scope) {
		return this.prefix(scope + '.');
	}

	public default PropertiesSource scope(Package scope) {
		return this.scope(scope.getName());
	}

	public default PropertiesSource scope(Class<?> scope) {
		return this.scope(scope.getName());
	}

}
