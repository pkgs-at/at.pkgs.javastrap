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

public abstract class Singleton<Type> {

	public class State {

		private final boolean initialized;

		private final Type instance;

		public State(boolean initialized, Type instance) {
			this.initialized = initialized;
			this.instance = instance;
		}

		public boolean isInitialized() {
			return this.initialized;
		}

		public Type getInstance() {
			return this.instance;
		}

	}

	private State state;

	public Singleton() {
		this.state = new State(false, null);
	}

	protected abstract Type initialize();

	public Type get() {
		if (!this.state.isInitialized()) {
			synchronized (this) {
				if (!this.state.isInitialized()) {
					this.state = new State(true, this.initialize());
				}
			}
		}
		return this.state.getInstance();
	}

}
