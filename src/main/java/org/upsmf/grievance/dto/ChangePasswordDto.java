/**
 *
 */
package org.upsmf.grievance.dto;

/**
 * @author Manzarul
 *
 */
public class ChangePasswordDto {
	private Long userId;
	private String oldPass;
	private String newPass;
	private String confirmNewPass;

	public String getOldPass() {
		return oldPass;
	}

	public void setOldPass(String oldPass) {
		this.oldPass = oldPass;
	}

	public String getNewPass() {
		return newPass;
	}

	public void setNewPass(String newPass) {
		this.newPass = newPass;
	}

	public String getConfirmNewPass() {
		return confirmNewPass;
	}

	public void setConfirmNewPass(String confirmNewPass) {
		this.confirmNewPass = confirmNewPass;
	}

	@Override
	public String toString() {
		return "ChangePasswordDto [oldPass=" + oldPass + ", newPass=" + newPass + ", confirmNewPass=" + confirmNewPass
				+ "]";
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

}
