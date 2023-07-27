package org.upsmf.grievance.model;

import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;

@Component
public class KeyFactory {

	private KeyFactory() {
		super();
	}

	public static KeyHolder getkeyHolder() {
		return new GeneratedKeyHolder();
	}

}
