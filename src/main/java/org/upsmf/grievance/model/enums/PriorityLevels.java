package org.upsmf.grievance.model.enums;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum PriorityLevels {

	LOW("LOW"), MOD("MODERATE"), HIGH("HIGH");

	private String value;

	PriorityLevels(final String value) {
		this.value = value;
	}

	@Override
	@JsonValue
	public String toString() {
		return StringUtils.capitalize(name());
	}

	@JsonCreator
	public static PriorityLevels fromValue(final String passedValue) {
		for (final PriorityLevels obj : PriorityLevels.values()) {
			if (String.valueOf(obj.value).equalsIgnoreCase(passedValue)) {
				return obj;
			}
		}
		return null;
	}

}
