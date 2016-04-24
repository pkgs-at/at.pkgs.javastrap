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

import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.Collections;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class Collector {

	public static interface Result {

		public void add(Collector collector, String code, Object... arguments);

		public void add(Collector collector, Field<?> field, String code, Object... arguments);

		public boolean hasError();

	}

	public static class Message implements Result, Serializable {

		public static class MessageList extends ArrayList<String> {

			private static final long serialVersionUID = 1L;

		}

		private static final long serialVersionUID = 1L;

		private final MessageList global;

		private final Map<String, MessageList> fields;

		public Message() {
			this.global = new MessageList();
			this.fields = new LinkedHashMap<String, MessageList>();
		}

		@Override
		public void add(Collector collector, String code, Object... arguments) {
			this.global.add(
					collector.getHandler().format(
							code,
							arguments));
		}

		@Override
		public void add(Collector collector, Field<?> field, String code, Object... arguments) {
			if (!this.fields.containsKey(field.name()))
				this.fields.put(field.name(), new MessageList());
			this.fields.get(field.name()).add(
					collector.getHandler().format(
							new String[] { code + '.' + field.name(), code },
							arguments));
		}

		public List<String> getGlobalErrors() {
			return this.global;
		}

		public List<String> getFieldErrors(String name) {
			if (this.fields.containsKey(name))
				return this.fields.get(name);
			else
				return Collections.emptyList();
		}

		public List<String> getFieldNames() {
			List<String> list;

			list = new ArrayList<String>();
			for (Map.Entry<String, MessageList> entry : this.fields.entrySet())
				if (entry.getValue().size() > 0) list.add(entry.getKey());
			return list;
		}

		@Override
		public boolean hasError() {
			if (this.global.size() > 0) return true;
			for (Map.Entry<String, MessageList> entry : this.fields.entrySet())
				if (entry.getValue().size() > 0) return true;
			return false;
		}

	}

	public static interface Field<Type> {

		public Collector getCollector();

		public String name();

		public String parameter();

		public Type value();

		public void value(Type value);

		public void error(String code, Object... arguments);

		public void generalError(String code, Object... arguments);

	}

	public abstract class AbstractField<Type> implements Field<Type> {

		private final String name;

		private final String parameter;

		private Type value;

		public AbstractField(String name) {
			this.name = name;
			this.parameter = this.getCollector().getParameter(name);
			this.initialize();
		}

		protected abstract void initialize();

		@Override
		public Collector getCollector() {
			return Collector.this;
		}

		@Override
		public String name() {
			return this.name;
		}

		@Override
		public String parameter() {
			return this.parameter;
		}

		@Override
		public Type value() {
			return this.value;
		}

		@Override
		public void value(Type value) {
			this.value = value;
		}

		protected abstract String getErrorPrefix();

		protected String getIllegalFormatCode() {
			return "IllegalFormat";
		}

		@Override
		public void error(String code, Object... arguments) {
			this.getCollector().getResult().add(
					Collector.this,
					this,
					this.getErrorPrefix() + code,
					arguments);
		}

		@Override
		public void generalError(String code, Object... arguments) {
			this.getCollector().getResult().add(
					Collector.this,
					this,
					"collectorGeneral" + code,
					arguments);
		}

		public void ifAbsent(Type value) {
			if (this.value != null) return;
			this.value(value);
		}

		public void required(String code) {
			if (this.value != null) return;
			this.generalError(code);
		}

		public void required() {
			this.required("Required");
		}

	}

	public class StringField extends AbstractField<String> {

		private int length;

		public StringField(String name) {
			super(name);
			this.length = 0;
		}

		@Override
		public String getErrorPrefix() {
			return "collectorString";
		}

		@Override
		public void value(String value) {
			super.value(value);
			this.length = (value == null) ? 0 : value.codePointCount(0, value.length());
		}

		protected int length() {
			return this.length;
		}

		@Override
		public void initialize() {
			this.value(this.parameter());
		}

		public void ifEmpty(String value) {
			if (this.value() == null || !this.value().isEmpty()) return;
			this.value(value);
		}

		public void ifBlank(String value) {
			if (this.value() != null && !this.value().isEmpty()) return;
			this.value(value);
		}

		public void notEmpty(String code) {
			if (this.value() == null || !this.value().isEmpty()) return;
			this.error(code);
		}

		public void notEmpty() {
			this.notEmpty("NotEmpty");
		}

		public void notBlank(String code) {
			if (this.value() != null && !this.value().isEmpty()) return;
			this.error(code);
		}

		public void notBlank() {
			this.notBlank("NotBlank");
		}

		public void lengthEqual(int length, String code) {
			if (this.value() == null) return;
			if (this.length() == length) return;
			this.error(code, this.value(), length);
		}

		public void lengthEqual(int length) {
			this.lengthEqual(length, "LengthEqual");
		}

		public void lengthLessThan(int length, String code) {
			if (this.value() == null) return;
			if (this.length() < length) return;
			this.error(code, this.value(), length);
		}

		public void lengthLessThan(int length) {
			this.lengthLessThan(length, "LengthLessThan");
		}

		public void lengthLessEqual(int length, String code) {
			if (this.value() == null) return;
			if (this.length() <= length) return;
			this.error(code, this.value(), length);
		}

		public void lengthLessEqual(int length) {
			this.lengthLessEqual(length, "LengthLessEqual");
		}

		public void lengthGreaterThan(int length, String code) {
			if (this.value() == null) return;
			if (this.length() > length) return;
			this.error(code, this.value(), length);
		}

		public void lengthGreaterThan(int length) {
			this.lengthGreaterThan(length, "LengthGreaterThan");
		}

		public void lengthGreaterEqual(int length, String code) {
			if (this.value() == null) return;
			if (this.length() >= length) return;
			this.error(code, this.value(), length);
		}

		public void lengthGreaterEqual(int length) {
			this.lengthGreaterEqual(length, "LengthGreaterEqual");
		}

		public void lengthBetween(int minimum, int maximum, String code) {
			if (this.value() == null) return;
			if (this.length() >= minimum && this.value().length() <= maximum) return;
			this.error(code, this.value(), minimum, maximum);
		}

		public void lengthBetween(int minimum, int maximum) {
			this.lengthBetween(minimum, maximum, "LengthBetween");
		}

		public void matches(Pattern pattern, String code) {
			Matcher matcher;

			if (this.value() == null) return;
			matcher = pattern.matcher(this.value());
			if (matcher.matches()) return;
			this.error(code,  this.value());
		}

		public void matches(Pattern pattern) {
			this.matches(pattern, "Matches");
		}

		public void matches(String pattern, int flags, String code) {
			if (this.value() == null) return;
			this.matches(Pattern.compile(pattern, flags), code);
		}

		public void matches(String pattern, int flags) {
			this.matches(pattern, flags, "Matches");
		}

		public void matches(String pattern, String code) {
			if (this.value() == null) return;
			this.matches(Pattern.compile(pattern), code);
		}

		public void matches(String pattern) {
			this.matches(Pattern.compile(pattern), "Matches");
		}

	}

	public class IntegerField extends AbstractField<Integer> {

		public IntegerField(String name) {
			super(name);
		}

		@Override
		public String getErrorPrefix() {
			return "collectorInteger";
		}

		@Override
		public void initialize() {
			if (this.parameter() == null) return;
			try {
				this.value(Integer.valueOf(this.parameter()));
			}
			catch (NumberFormatException cause) {
				this.error(this.getIllegalFormatCode(), this.value());
			}
		}

		public void lessThan(int value, String code) {
			if (this.value() == null) return;
			if (this.value() < value) return;
			this.error(code, this.value());
		}

		public void lessThan(int value) {
			this.lessThan(value, "LessThan");
		}

		public void lessEqual(int value, String code) {
			if (this.value() == null) return;
			if (this.value() <= value) return;
			this.error(code, this.value(), value);
		}

		public void lessEqual(int value) {
			this.lessEqual(value, "LessEqual");
		}

		public void greaterThan(int value, String code) {
			if (this.value() == null) return;
			if (this.value() > value) return;
			this.error(code, this.value(), value);
		}

		public void greaterThan(int value) {
			this.greaterThan(value, "GreaterThan");
		}

		public void greaterEqual(int value, String code) {
			if (this.value() == null) return;
			if (this.value() >= value) return;
			this.error(code, this.value(), value);
		}

		public void greaterEqual(int value) {
			this.greaterEqual(value, "GreaterEqual");
		}

		public void between(int minimum, int maximum, String code) {
			if (this.value() == null) return;
			if (this.value() >= minimum && this.value() <= maximum) return;
			this.error(code, this.value(), minimum, maximum);
		}

		public void between(int minimum, int maximum) {
			this.between(minimum, maximum, "Between");
		}

	}

	public class LongField extends AbstractField<Long> {

		public LongField(String name) {
			super(name);
		}

		@Override
		public String getErrorPrefix() {
			return "collectorLong";
		}

		@Override
		public void initialize() {
			if (this.parameter() == null) return;
			try {
				this.value(Long.valueOf(this.parameter()));
			}
			catch (NumberFormatException cause) {
				this.error(this.getIllegalFormatCode(), this.value());
			}
		}

		public void lessThan(long value, String code) {
			if (this.value() == null) return;
			if (this.value() < value) return;
			this.error(code, this.value(), value);
		}

		public void lessThan(long value) {
			this.lessThan(value, "LessThan");
		}

		public void lessEqual(long value, String code) {
			if (this.value() == null) return;
			if (this.value() <= value) return;
			this.error(code, this.value(), value);
		}

		public void lessEqual(long value) {
			this.lessEqual(value, "LessEqual");
		}

		public void greaterThan(long value, String code) {
			if (this.value() == null) return;
			if (this.value() > value) return;
			this.error(code, this.value(), value);
		}

		public void greaterThan(long value) {
			this.greaterThan(value, "GreaterThan");
		}

		public void greaterEqual(long value, String code) {
			if (this.value() == null) return;
			if (this.value() >= value) return;
			this.error(code, this.value(), value);
		}

		public void greaterEqual(long value) {
			this.greaterEqual(value, "GreaterEqual");
		}

		public void between(long minimum, long maximum, String code) {
			if (this.value() == null) return;
			if (this.value() >= minimum && this.value() <= maximum) return;
			this.error(code, this.value(), minimum, maximum);
		}

		public void between(long minimum, long maximum) {
			this.between(minimum, maximum, "Between");
		}

	}

	private final CoreHandler handler;

	private final Result result;

	public Collector(CoreHandler handler) {
		this.handler = handler;
		this.result = this.createResult();
	}

	protected Result createResult() {
		return new Message();
	}

	public CoreHandler getHandler() {
		return this.handler;
	}

	public Result getResult() {
		return this.result;
	}

	public String getParameter(String name) {
		return this.getHandler().getRequest().getParameter(name);
	}

	public void error(String code, Object... arguments) {
		this.getResult().add(
				this,
				code,
				arguments);
	}

}
