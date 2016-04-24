package at.pkgs.javastrap.core.sample;

import java.util.UUID;
import java.io.Serializable;
import java.io.IOException;
import javax.servlet.http.HttpSession;
import at.pkgs.javastrap.core.CoreHandler;

public abstract class KernelHandler extends CoreHandler {

	protected static class Token implements Serializable {

		private static final long serialVersionUID = 1L;

		private final String value;

		private final long expired;

		protected Token(long period) {
			this.value = UUID.randomUUID().toString().toLowerCase();
			this.expired = System.currentTimeMillis() + period;
		}

		protected String value() {
			return this.value;
		}

		protected boolean validate(String value) {
			if (!this.value.equals(value)) return false;
			if (System.currentTimeMillis() >= this.expired) return false;
			return true;
		}

	}

	@Override
	public KernelHolder getHolder() {
		return (KernelHolder)super.getHolder();
	}

	protected String getTokenSessionName() {
		return this.getClass().getName() + ".token";
	}

	protected long getTokenPeriod() {
		return 15000L;
	}

	protected void token() throws IOException, ErrorResponse {
		Token token;
		HttpSession session;

		if (this.noneOf(HttpMethod.POST))
			throw new ErrorResponse(ClientError.MethodNotAllowed);
		token = new Token(this.getTokenPeriod());
		session = this.getRequest().getSession(true);
		session.setAttribute(this.getTokenSessionName(), token);
		this.getResponse().sendResponse("text/plain", token.value());
		this.finish();
	}

	protected boolean validateToken(String name) {
		HttpSession session;
		Token token;

		session = this.getRequest().getSession(false);
		if (session == null) return false;
		token = (Token)session.getAttribute(this.getTokenSessionName());
		if (token == null) return false;
		session.removeAttribute(this.getTokenSessionName());
		return token.validate(this.getRequest().getParameter(name));
	}

}
