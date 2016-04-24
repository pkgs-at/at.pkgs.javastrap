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

public abstract class Lazy<Type> {

	public static enum Status {

		POSTPONED,

		COMPLETED,

		FAILURE;

	}

	public final class State {

		private final Status status;

		private final Type instance;

		private final Throwable cause;

		public State(Status status) {
			this.status = status;
			this.instance = null;
			this.cause = null;
		}

		public State(Type instance) {
			this.status = Status.COMPLETED;
			this.instance = instance;
			this.cause = null;
		}

		public State(Throwable cause) {
			this.status = Status.FAILURE;
			this.instance = null;
			this.cause = cause;
		}

		public final Status getStatus() {
			return this.status;
		}

		public final Type getInstance() {
			return this.instance;
		}

		public final Throwable getCause() {
			return this.cause;
		}

	}

	public static abstract class Purgeable<Type> extends Lazy<Type> {

		public void purge() {
			this.reset();
		}

	}

	private State state;

	public Lazy() {
		this.state = new State(Status.POSTPONED);
	}

	protected abstract Type initialize();

	public Type get() {
		if (this.state.getStatus() != Status.COMPLETED) {
			synchronized (this) {
				switch (this.state.getStatus()) {
				case POSTPONED :
					try {
						this.state = new State(this.initialize());
					}
					catch (Throwable cause) {
						this.state = new State(cause);
						throw cause;
					}
					break;
				case COMPLETED :
					break;
				case FAILURE :
					throw new IllegalStateException(this.state.getCause());
				}
			}
		}
		return this.state.getInstance();
	}

	protected void reset() {
		synchronized (this) {
			this.state = new State(Status.POSTPONED);
		}
	}

}
