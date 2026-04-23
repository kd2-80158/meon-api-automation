package com.api.models.response.esign;

public class SetAutoReminderResponse {

	
	private String msg;
	private boolean success;
	private long starts_in_seconds;
	
	public SetAutoReminderResponse() {
	}

	public SetAutoReminderResponse(String msg, boolean success, long starts_in_seconds) {
		super();
		this.msg = msg;
		this.success = success;
		this.starts_in_seconds = starts_in_seconds;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public long getStarts_in_seconds() {
		return starts_in_seconds;
	}

	public void setStarts_in_seconds(long starts_in_seconds) {
		this.starts_in_seconds = starts_in_seconds;
	}

	@Override
	public String toString() {
		return "SetAutoReminderResponse [msg=" + msg + ", success=" + success + ", starts_in_seconds="
				+ starts_in_seconds + "]";
	}
}
