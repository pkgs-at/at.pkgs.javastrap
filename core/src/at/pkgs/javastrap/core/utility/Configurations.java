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

import java.util.Iterator;
import java.util.Properties;
import java.io.PrintStream;
import java.io.PrintWriter;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationUtils;

public class Configurations {

	public static interface Entry {

		public String getKey();

		public String getString();

	}

	public static void dump(Configuration configuration, PrintStream stream) {
		ConfigurationUtils.dump(configuration, stream);
		stream.println();
	}

	public static void dump(Configuration configuration, PrintWriter writer) {
		ConfigurationUtils.dump(configuration, writer);
		writer.println();
	}

	public static void dump(Properties properties, PrintStream stream) {
		for (String key : properties.stringPropertyNames()) {
			stream.print(key);
			stream.print('=');
			stream.print(properties.getProperty(key));
			stream.println();
		}
	}

	public static void dump(Properties properties, PrintWriter writer) {
		for (String key : properties.stringPropertyNames()) {
			writer.print(key);
			writer.print('=');
			writer.print(properties.getProperty(key));
			writer.println();
		}
	}

	public static Iterable<Entry> iterable(final Configuration configuration) {
		return new Iterable<Entry>() {

			@Override
			public Iterator<Entry> iterator() {
				final Iterator<String> keys;

				keys = configuration.getKeys();
				return new Iterator<Entry>() {

					@Override
					public boolean hasNext() {
						return keys.hasNext();
					}

					@Override
					public Entry next() {
						final String key;

						key = keys.next();
						return new Entry() {

							@Override
							public String getKey() {
								return key;
							}

							@Override
							public String getString() {
								return configuration.getString(key);
							}

						};
					}

				};
			}

		};
	}

}
