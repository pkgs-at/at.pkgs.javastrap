package at.pkgs.javastrap.core.sample;

import java.io.File;
import at.pkgs.javastrap.core.Core;

public abstract class Kernel extends Core {

	@Override
	public String getSystemName() {
		return "at.pkgs.javastrap";
	}

	@Override
	public File getConfigurationDirectory() {
		return new File(this.getRootDirectory(), "config.sample");
	}

	public static Kernel get() {
		return (Kernel)Core.get();
	}

}
