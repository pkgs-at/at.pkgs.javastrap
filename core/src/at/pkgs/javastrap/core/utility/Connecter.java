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

import java.sql.Connection;
import java.sql.SQLException;

public abstract class Connecter implements AutoCloseable {

	private Connection connection;

	private boolean connected;

	public Connecter(Connection connection) {
		this.connection = connection;
		this.connected = false;
	}

	protected abstract Connection connect() throws SQLException;

	public Connection get() throws SQLException {
		if (this.connection == null) {
			this.connection = this.connect();
			this.connected = true;
		}
		return this.connection;
	}

	@Override
	public void close() throws SQLException {
		if (this.connected) {
			this.connection.close();
			this.connection = null;
			this.connected = false;
		}
	}

}
