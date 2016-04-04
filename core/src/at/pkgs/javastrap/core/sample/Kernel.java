package at.pkgs.javastrap.core.sample;

import at.pkgs.javastrap.core.Core;

public abstract class Kernel extends Core {

	@Override
	public String getSystemName() {
		return "at.pkgs.javastrap";
	}

	public static Kernel get() {
		return (Kernel)Core.get();
	}

}
