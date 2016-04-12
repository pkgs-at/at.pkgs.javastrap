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

import java.lang.reflect.Modifier;
import java.lang.reflect.Method;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.util.Arrays;
import java.util.Collections;

public class BeanCopier<Type> {

	public class Property {

		private final Class<?> type;

		private final String name;

		private final String getter;

		private final String setter;

		public Property(Class<?> type, String getter) {
			String body;

			this.type = type;
			body = getter.substring(getter.startsWith("is") ? 2 : 3);
			this.name = body.substring(0, 1).toLowerCase() + body.substring(1);
			this.getter = getter;
			this.setter = "set" + body;
		}

		public String getName() {
			return this.name;
		}

		public Object get(Type from) throws ReflectiveOperationException {
			return from.getClass().getMethod(this.getter).invoke(from);
		}

		public void set(Type to, Object value) throws ReflectiveOperationException {
			to.getClass().getMethod(this.setter, this.type).invoke(to, value);
		}

		public void copy(Type to, Type from) throws ReflectiveOperationException {
			this.set(to, this.get(from));
		}

	}

	private final List<Property> properties;

	public BeanCopier(Class<Type> type) {
		List<Property> properties;

		properties = new ArrayList<Property>();
		for (Method method : type.getMethods()) {
			if (!this.isProperty(method)) continue;
			properties.add(new Property(method.getReturnType(), method.getName()));
		}
		this.properties = Collections.unmodifiableList(properties);
	}

	protected boolean isProperty(Method method) {
		String name;

		if (Modifier.isStatic(method.getModifiers())) return false;
		if (method.getParameterCount() > 0) return false;
		name = method.getName();
		if (name.startsWith("get") && name.length() > 3) return true;
		if (!name.startsWith("is") || name.length() <= 2) return false;
		if (method.getReturnType() == boolean.class) return true;
		if (method.getReturnType() == Boolean.class) return true;
		return false;
	}

	public void copy(Type to, Type from, String... excludes) {
		Set<String> ignored;

		ignored = new HashSet<String>(Arrays.asList(excludes));
		try {
			for (Property property : this.properties) {
				if (ignored.contains(property.getName())) continue;
				property.copy(to, from);
			}
		}
		catch (ReflectiveOperationException cause) {
			throw new RuntimeException(cause);
		}
	}

}
