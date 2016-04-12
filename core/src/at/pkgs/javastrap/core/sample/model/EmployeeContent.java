package at.pkgs.javastrap.core.sample.model;

import java.io.Serializable;
import at.pkgs.javastrap.core.utility.BeanCopier;

public interface EmployeeContent extends Serializable {

	public static final BeanCopier<EmployeeContent> COPIER =
			new BeanCopier<EmployeeContent>(EmployeeContent.class);

	public String getFamilyName();

	public String getGivenName();

	public String getMailAddress();

	public String getTelephoneNumber();

	public default void copyTo(EmployeeContent content, String... excludes) {
		EmployeeContent.COPIER.copy(content, this, excludes);
	}

}
