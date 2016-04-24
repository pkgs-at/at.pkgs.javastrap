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

import java.text.Format;
import java.io.IOException;
import javax.servlet.ServletException;
import at.pkgs.logging.Logger;
import at.pkgs.web.client.LocationBuilder;
import at.pkgs.web.http.HttpRequest;
import at.pkgs.web.http.HttpResponse;
import at.pkgs.web.trio.ContextHolder;
import at.pkgs.web.trio.AbstractHandler;

public abstract class CoreHandler extends AbstractHandler implements at.pkgs.logging.Loggable {

	public static enum HttpMethod {

		UNKNOWN,

		GET,

		HEAD,

		POST,

		PUT,

		DELETE,

		CONNECT,

		OPTIONS,

		TRACE,

		PATCH,

		LINK,

		UNLINK,

		;

		public static HttpMethod parse(String method) {
			if (method == null) return HttpMethod.UNKNOWN;
			try {
				return HttpMethod.valueOf(method.toUpperCase());
			}
			catch (IllegalArgumentException ignored) {
				return HttpMethod.UNKNOWN;
			}
		}

	}

	public static interface HttpStatusCode {

		public int getStatus();

		public String getCode();

	}

	public static enum ClientError implements HttpStatusCode {

		BadRequest(400),

		Unauthorized(401),

		PaymentRequired(402),

		Forbidden(403),

		NotFound(404),

		MethodNotAllowed(405),

		NotAcceptable(406),

		ProxyAuthenticationRequired(407),

		RequestTimeout(408),

		Conflict(409),

		Gone(410),

		LengthRequired(411),

		PreconditionFailed(412),

		PayloadTooLarge(413),

		UriTooLong(414),

		UnsupportedMediaType(415),

		RangeNotSatisfiable(416),

		ExpectationFailed(417),

		UnprocessableEntity(422),

		Locked(423),

		FailedDependency(424),

		UpgradeRequired(426),

		UnavailableForLegalReasons(451),

		;

		private final int status;

		private final String code;

		private ClientError(int status) {
			this.status = status;
			this.code = "httpStatus" + this.name();
		}

		public int getStatus() {
			return this.status;
		}

		public String getCode() {
			return this.code;
		}

	}

	public static enum ServerError implements HttpStatusCode {

		InternalServerError(500),

		NotImplemented(501),

		BadGateway(502),

		ServiceUnavailable(503),

		GatewayTimeout(504),

		HttpVersionNotSupported(505),

		VariantAlsoNegotiates(506),

		InsufficientStorage(507),

		BandwidthLimitExceeded(509),

		NotExtended(510),

		;

		private final int status;

		private final String code;

		private ServerError(int status) {
			this.status = status;
			this.code = "httpStatus" + this.name();
		}

		public int getStatus() {
			return this.status;
		}

		public String getCode() {
			return this.code;
		}

	}

	public class LocationFactory {

		public static final String PROTOCOL_REQUEST_HEADER = "request-header:";

		private final String base;

		public LocationFactory(String setting) {
			StringBuilder builder;

			builder = null;
			if (setting != null && !setting.isEmpty()) {
				if (setting.startsWith(LocationFactory.PROTOCOL_REQUEST_HEADER)) {
					String name;
					String header;

					name = setting.substring(LocationFactory.PROTOCOL_REQUEST_HEADER.length());
					header = CoreHandler.this.getRequest().getHeader(name);
					if (header != null && !header.isEmpty())
						builder = new StringBuilder(header);
				}
				else {
					builder = new StringBuilder(setting);
				}
			}
			if (builder == null) {
				builder = new StringBuilder();
				builder.append(CoreHandler.this.getRequest().getScheme());
				builder.append("://");
				builder.append(CoreHandler.this.getRequest().getServerName());
				switch (CoreHandler.this.getRequest().getScheme()) {
				case "http" :
					if (CoreHandler.this.getRequest().getServerPort() != 80)
						builder.append(':').append(CoreHandler.this.getRequest().getServerPort());
					break;
				case "https" :
					if (CoreHandler.this.getRequest().getServerPort() != 443)
						builder.append(':').append(CoreHandler.this.getRequest().getServerPort());
					break;
				}
				builder.append(CoreHandler.this.getRequest().getContextPath());
			}
			this.base = builder.toString();
		}

		public LocationBuilder get() {
			return new LocationBuilder(this.base);
		}

	}

	public static class ForwardRequest extends Exception {

		private static final long serialVersionUID = 1L;

		private final String path;

		public ForwardRequest(String path) {
			this.path = path;
		}

		public String getPath() {
			return this.path;
		}

	}

	public static class RedirectRequest extends Exception {

		private static final long serialVersionUID = 1L;

		private final LocationBuilder location;

		public RedirectRequest(LocationBuilder location) {
			this.location = location;
		}

		public LocationBuilder getLocation() {
			return this.location;
		}

	}

	public static class ErrorPage extends Exception {

		private static final long serialVersionUID = 1L;

		private final int status;

		public ErrorPage(int status) {
			this.status = status;
		}

		public ErrorPage(HttpStatusCode status) {
			this(status.getStatus());
		}

		public int getStatus() {
			return this.status;
		}

	}

	public static class ErrorResponse extends Exception {

		private static final long serialVersionUID = 1L;

		private final int status;

		private final String code;

		private final Object[] arguments;

		public ErrorResponse(Throwable cause, int status, String code, Object... arguments) {
			super(null, cause);
			this.status = status;
			this.code = code;
			this.arguments = arguments;
		}

		public ErrorResponse(Throwable cause, int status) {
			this(cause, status, null);
		}

		public ErrorResponse(int status, String code, Object... arguments) {
			this(null, status, code, arguments);
		}

		public ErrorResponse(int status) {
			this(null, status, null);
		}

		public ErrorResponse(Throwable cause, HttpStatusCode status, String code, Object... arguments) {
			this(cause, status.getStatus(), code, arguments);
		}

		public ErrorResponse(Throwable cause, HttpStatusCode status) {
			this(cause, status.getStatus(), status.getCode());
		}

		public ErrorResponse(HttpStatusCode status, String code, Object... arguments) {
			this(null, status.getStatus(), code, arguments);
		}

		public ErrorResponse(HttpStatusCode status) {
			this(null, status.getStatus(), status.getCode());
		}

		public int getStatus() {
			return this.status;
		}

		public String getCode() {
			return this.code;
		}

		public Object[] getArguments() {
			return this.arguments;
		}

	}

	public class Collector extends at.pkgs.javastrap.core.Collector {

		public Collector() {
			super(CoreHandler.this);
		}

	}

	private Logger logger = null;

	private HttpMethod method;

	@Override
	public Logger getLogger() {
		if (this.logger == null)
			this.logger = at.pkgs.logging.LoggerFactory.get(this);
		return this.logger;
	}

	protected Collector.Result createCollectorResult() {
		return new Collector.Message();
	}

	protected String getEncoding() {
		return "UTF-8";
	}

	protected String getCacheControl() {
		return "no-cache";
	}

	@Override
	public CoreHolder getHolder() {
		return (CoreHolder)super.getHolder();
	}

	public String format(String[] codes, Object... arguments) {
		if (codes.length < 1) throw new IllegalArgumentException();
		for (String code : codes) {
			Format format;

			format = Core.get().getMessageSource().getFormat(this.getClass().getName() + '.' + code);
			if (format != null) format.format(arguments);
		}
		return Core.get().format(codes[codes.length - 1], arguments);
	}

	@Override
	public String format(String code, Object... arguments) {
		return this.format(new String[] { code }, arguments);
	}

	@Override
	public void initialize(
			ContextHolder holder,
			HttpRequest request,
			HttpResponse response)
					throws Exception {
		String encoding;
		String cacheControl;

		super.initialize(holder, request, response);
		this.method = HttpMethod.parse(request.getMethod());
		encoding = this.getEncoding();
		if (encoding != null) {
			this.getRequest().setCharacterEncoding(encoding);
			this.getResponse().setCharacterEncoding(encoding);
		}
		cacheControl = this.getCacheControl();
		if (cacheControl != null)
			this.getResponse().setHeader("Cache-Control", cacheControl);
	}

	public HttpMethod getMethod() {
		return this.method;
	}

	public boolean oneOf(HttpMethod... methods) {
		for (HttpMethod method : methods) {
			if (this.getMethod() == method) return true;
		}
		return false;
	}

	public boolean noneOf(HttpMethod... methods) {
		return !this.oneOf(methods);
	}

	public void setResponseStatus(int status) {
		this.getResponse().setStatus(status);
	}

	public void setResponseStatus(HttpStatusCode status) {
		this.setResponseStatus(status.getStatus());
	}

	protected void redirect(LocationBuilder location) throws IOException {
		this.getResponse().sendRedirect(location.toString());
		this.finish();
	}

	protected void handle(ErrorResponse error) throws ServletException, IOException {
		String message;

		this.setResponseStatus(error.getStatus());
		message = (error.getCode() == null) ? null : this.format(error.getCode(), error.getArguments());
		throw new RuntimeException(message, error);
	}

	@Override
	protected void trap(Exception cause) throws ServletException, IOException {
		if (cause instanceof ForwardRequest) {
			this.forward(((ForwardRequest)cause).getPath());
			return;
		}
		if (cause instanceof RedirectRequest) {
			this.redirect(((RedirectRequest)cause).getLocation());
			return;
		}
		if (cause instanceof ErrorPage) {
			this.getResponse().sendError(((ErrorPage)cause).getStatus());
			this.finish();
			return;
		}
		if (cause instanceof ErrorResponse) {
			this.handle((ErrorResponse)cause);
			return;
		}
		super.trap(cause);
	}

}
