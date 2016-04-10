package at.pkgs.javastrap.site.sample;

import at.pkgs.javastrap.core.sample.KernelHandler;

public abstract class ApplicationHandler extends KernelHandler {

	public Application getApplication() {
		return Application.get();
	}

	@Override
	public ApplicationHolder getHolder() {
		return (ApplicationHolder)super.getHolder();
	}

}
