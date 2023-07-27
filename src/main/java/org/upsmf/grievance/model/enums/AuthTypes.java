package org.upsmf.grievance.model.enums;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum AuthTypes {

	GOOGLE("1"), CUSTOM("2");

	private String value;

	AuthTypes(final String value) {
		this.value = value;
	}

	@Override
	@JsonValue
	public String toString() {
		return StringUtils.capitalize(name());
	}

	@JsonCreator
	public static AuthTypes fromValue(final String passedValue) {
		for (final AuthTypes obj : AuthTypes.values()) {
			if (String.valueOf(obj.value).equalsIgnoreCase(passedValue)) {
				return obj;
			}
		}
		return null;
	}

}
