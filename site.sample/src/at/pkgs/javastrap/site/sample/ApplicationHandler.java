package at.pkgs.javastrap.site.sample;

import java.lang.reflect.Method;
import java.io.IOException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import at.pkgs.javastrap.core.utility.Lazy;
import at.pkgs.javastrap.core.sample.KernelHandler;

public abstract class ApplicationHandler extends KernelHandler {

	private static final Lazy<ObjectMapper> objectMapper  = new Lazy<ObjectMapper>() {

		@Override
		protected ObjectMapper initialize() {
			ObjectMapper mapper;

			mapper = new ObjectMapper();
			mapper.enable(SerializationFeature.INDENT_OUTPUT);
			return mapper;
		}

	};

	public Application getApplication() {
		return Application.get();
	}

	@Override
	public ApplicationHolder getHolder() {
		return (ApplicationHolder)super.getHolder();
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
			ActionType defaultAction) {
		String action;

		action = this.getRequest().getParameter("action");
		if (action == null) return defaultAction;
		try {
			Method method;

			method = defaultAction.getClass().getMethod("valueOf", String.class);
			return (ActionType)method.invoke(null, action.toUpperCase());
		}
		catch (java.lang.ReflectiveOperationException cause) {
			throw new RuntimeException(cause);
		}
	}

}
