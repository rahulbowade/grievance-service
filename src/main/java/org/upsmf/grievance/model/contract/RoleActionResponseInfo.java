package org.upsmf.grievance.model.contract;

import java.util.List;

import org.upsmf.grievance.model.Action;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * ResponseInfo should be used to carry metadata information about the response
 * from the server along with the requested data.
 */
@AllArgsConstructor
@Getter
@NoArgsConstructor
@Setter
public class RoleActionResponseInfo {

	private StatusInfo statusInfo;

	private List<Action> actions;

}
