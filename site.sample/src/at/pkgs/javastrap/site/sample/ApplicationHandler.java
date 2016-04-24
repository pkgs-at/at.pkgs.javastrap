package at.pkgs.javastrap.site.sample;

import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;
import java.io.IOException;
import javax.servlet.ServletException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import at.pkgs.web.client.LocationBuilder;
import at.pkgs.javastrap.core.utility.Lazy;
import at.pkgs.javastrap.core.sample.KernelHandler;

public abstract class ApplicationHandler extends KernelHandler {

	public static final String MESSAGE_INVALID_TOKEN = "handlerInvalidToken";

	private static final Lazy<ObjectMapper> objectMapper  = new Lazy<ObjectMapper>() {

		@Override
		protected ObjectMapper initialize() {
			ObjectMapper mapper;

			mapper = new ObjectMapper();
			mapper.enable(SerializationFeature.INDENT_OUTPUT);
			return mapper;
		}

	};

	private LocationFactory locationFactory = null;

	public Application getApplication() {
		return Application.get();
	}

	@Override
	public ApplicationHolder getHolder() {
		return (ApplicationHolder)super.getHolder();
	}

	public LocationBuilder location() {
		if (this.locationFactory == null)
			this.locationFactory = new LocationFactory(
					this.getApplication()
							.getApplicationConfiguration()
							.getString("location", null));
		return this.locationFactory.get();
	}

	protected String encode(Object data) {
		try {
			return ApplicationHandler.objectMapper.get().writeValueAsString(data);
		}
		catch (JsonProcessingException cause) {
			throw new RuntimeException(cause);
		}
	}

	protected <DataType> DataType decode(String code, Class<DataType> type) {
		try {
			return ApplicationHandler.objectMapper.get().readValue(code, type);
		}
		catch (JsonProcessingException cause) {
			throw new RuntimeException(cause);
		}
		catch (IOException cause) {
			throw new RuntimeException(cause);
		}
	}

	@SuppressWarnings("unchecked")
	protected <ActionType extends Enum<?>> ActionType dispatch(
			ActionType defaultAction)
					throws ErrorResponse {
		String action;

		action = this.getRequest().getParameter("action");
		if (action == null) return defaultAction;
		try {
			Method method;

			method = defaultAction.getClass().getMethod("valueOf", String.class);
			return (ActionType)method.invoke(null, action.toUpperCase());
		}
		catch (InvocationTargetException cause) {
			if (cause.getCause() instanceof IllegalArgumentException)
				throw new ErrorResponse(ClientError.BadRequest);
			else
				throw new RuntimeException(cause);
		}
		catch (ReflectiveOperationException cause) {
			throw new RuntimeException(cause);
		}
	}

	@Override
	protected void handle(ErrorResponse error) throws ServletException, IOException {
		this.setResponseStatus(error.getStatus());
		if (error.getCode() != null)
			this.addErrorMessage(error.getCode(), error.getArguments());
		this.forward("/error/message.htpl");
	}

}
