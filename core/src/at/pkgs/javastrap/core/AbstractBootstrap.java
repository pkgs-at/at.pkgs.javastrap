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

import org.apache.commons.configuration.CombinedConfiguration;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.configuration.tree.OverrideCombiner;
import at.pkgs.logging.Loggable;
import at.pkgs.logging.Logger;
import at.pkgs.logging.LoggerFactory;
import at.pkgs.javastrap.core.utility.Lazy;

public class AbstractBootstrap implements Loggable {

	private final String name;

	private final Lazy<DefaultFileManager> files;

	private final Logger logger;

	private final CombinedConfiguration configuration;

	public AbstractBootstrap(String name) {
		OverrideCombiner combiner;

		this.name = name;
		this.files = new Lazy<DefaultFileManager>() {

			@Override
			protected DefaultFileManager initialize() {
				// TODO Auto-generated method stub
				return null;
//				new DefaultFileManager(this.getClass().getName());
			}

		};
		// TODO configure logging
		this.logger = LoggerFactory.get(this);
		combiner = new OverrideCombiner();
		combiner.addListNode("property");
		this.configuration = new CombinedConfiguration(combiner);
	}

	@Override
	public Logger getLogger() {
		return this.logger;
	}

	public String getName() {
		return this.name;
	}

}
