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

import java.io.IOException;
import javax.servlet.ServletException;
import at.pkgs.logging.Logger;
import at.pkgs.web.http.HttpRequest;
import at.pkgs.web.http.HttpResponse;
import at.pkgs.web.trio.ContextHolder;
import at.pkgs.web.trio.AbstractHandler;
import at.pkgs.javastrap.core.utility.Lazy;

public abstract class CoreHandler extends AbstractHandler implements at.pkgs.logging.Loggable {

	private final Lazy<Logger> logger = new Lazy<Logger>() {

		@Override
		protected Logger initialize() {
			return at.pkgs.logging.LoggerFactory.get(CoreHandler.this);
		}

	};

	@Override
	public Logger getLogger() {
		return this.logger.get();
	}

	protected String getEncoding() {
		return "UTF-8";
	}

	protected String getCacheControl() {
		return "no-cache";
	}

	protected long getTokenLife() {
		return 15000L;
	}

	@Override
	public void initialize(
			ContextHolder holder,
			HttpRequest request,
			HttpResponse response)
					throws ServletException, IOException {
		String encoding;
		String cacheControl;

		super.initialize(holder, request, response);
		encoding = this.getEncoding();
		if (encoding != null) {
			this.getRequest().setCharacterEncoding(encoding);
			this.getResponse().setCharacterEncoding(encoding);
		}
		cacheControl = this.getCacheControl();
		if (cacheControl != null)
			this.getResponse().setHeader("Cache-Control", cacheControl);
	}

	@Override
	public CoreHolder getHolder() {
		return (CoreHolder)super.getHolder();
	}

}
