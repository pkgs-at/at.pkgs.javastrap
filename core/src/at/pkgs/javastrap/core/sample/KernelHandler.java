package at.pkgs.javastrap.core.sample;

import at.pkgs.javastrap.core.CoreHandler;

public abstract class KernelHandler extends CoreHandler {

	@Override
	public KernelHolder getHolder() {
		return (KernelHolder)super.getHolder();
	}

}
