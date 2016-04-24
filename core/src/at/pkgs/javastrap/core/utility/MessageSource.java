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

import java.util.Map;
import java.util.HashMap;
import java.util.Properties;
import java.text.Format;
import java.text.MessageFormat;

public class MessageSource {

	protected final Lazy<Properties> properties = new Lazy<Properties>() {

		@Override
		protected Properties initialize() {
			return MessageSource.this.loadProperties();
		}

	};

	public class FormatCache {

		private final Map<String, Format> formats;

		public FormatCache() {
			this.formats = new HashMap<String, Format>();
		}

		public Format get(String code) {
			if (!this.formats.containsKey(code))
				this.formats.put(code, MessageSource.this.createFormat(code));
			return this.formats.get(code);
		}

	}

	private final ThreadLocal<FormatCache> formats =new ThreadLocal<FormatCache>() {

		@Override
		protected FormatCache initialValue() {
			return new FormatCache();
		}

	};

	private final PropertiesSource source;

	private final boolean cache;

	public MessageSource(PropertiesSource source, boolean cache) {
		this.source = source;
		this.cache = cache;
	}

	public MessageSource(PropertiesSource source) {
		this(source, true);
	}

	protected Properties loadProperties() {
		return this.source.load();
	}

	protected Properties getProperties() {
		return this.cache ? this.properties.get() : this.loadProperties();
	}

	protected Format createFormat(String code) {
		String pattern;

		pattern = this.getProperties().getProperty(code);
		return pattern == null ? null : new MessageFormat(pattern);
	}

	public Format getFormat(String code) {
		return this.cache ? this.formats.get().get(code) : this.createFormat(code);
	}

	public String format(String code, Object arguments) {
		Format format;

		format = this.getFormat(code);
		if (format == null) throw new IllegalArgumentException("invalid message code: " + code);
		return format.format(arguments);
	}

}
