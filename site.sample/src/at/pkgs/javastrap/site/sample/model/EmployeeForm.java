package at.pkgs.javastrap.site.sample.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import at.pkgs.javastrap.core.sample.model.EmployeeContent;

public class EmployeeForm implements EmployeeContent {

	private static final long serialVersionUID = 1L;

	private String familyName;

	private String givenName;

	private String mailAddress;

	private String telephoneNumber;

	@Override
	@JsonProperty("family_name")
	public String getFamilyName() {
		return this.familyName;
	}

	public void setFamilyName(String value) {
		this.familyName = value;
	}

	@Override
	@JsonProperty("given_name")
	public String getGivenName() {
		return this.givenName;
	}

	public void setGivenName(String value) {
		this.givenName = value;
	}

	@Override
	@JsonProperty("mail_address")
	public String getMailAddress() {
		return this.mailAddress;
	}

	public void setMailAddress(String value) {
		this.mailAddress = value;
	}

	@Override
	@JsonProperty("telephone_number")
	public String getTelephoneNumber() {
		return this.telephoneNumber;
	}

	public void setTelephoneNumber(String value) {
		this.telephoneNumber = value;
	}

}
