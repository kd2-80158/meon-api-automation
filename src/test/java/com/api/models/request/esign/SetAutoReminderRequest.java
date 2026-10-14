package com.api.models.request.esign;

public class SetAutoReminderRequest {
	
	private String document_id;
	private int days_to_expire;
	private int everyValue;
	private String everyUnit;
	private String time;
	private boolean enablePersonalizeMsg;
	private String personalMsg;
	
	public SetAutoReminderRequest() {

	}

	public SetAutoReminderRequest(String document_id, int days_to_expire, int everyValue, String everyUnit,
			String time, boolean enablePersonalizeMsg, String personalMsg) {
		super();
		this.document_id = document_id;
		this.days_to_expire = days_to_expire;
		this.everyValue = everyValue;
		this.everyUnit = everyUnit;
		this.time = time;
		this.enablePersonalizeMsg = enablePersonalizeMsg;
		this.personalMsg = personalMsg;
	}

	public SetAutoReminderRequest(String document_id2, int days_to_expire1, int everyValue2, String everyUnit2,
			boolean enablePersonalizeMsg2, String personalMsg2) {
		this.document_id=document_id2;
		this.days_to_expire = days_to_expire1;
		this.everyValue = everyValue2;
		this.everyUnit = everyUnit2;
		this.enablePersonalizeMsg = enablePersonalizeMsg2;
		this.personalMsg = personalMsg2;
	}

	public String getDocument_id() {
		return document_id;
	}

	public void setDocument_id(String document_id) {
		this.document_id = document_id;
	}

	public int getDays_to_expire() {
		return days_to_expire;
	}

	public void setDays_to_expire(int days_to_expire) {
		this.days_to_expire = days_to_expire;
	}

	public int getEveryValue() {
		return everyValue;
	}

	public void setEveryValue(int everyValue) {
		this.everyValue = everyValue;
	}

	public String getEveryUnit() {
		return everyUnit;
	}

	public void setEveryUnit(String everyUnit) {
		this.everyUnit = everyUnit;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public boolean isEnablePersonalizeMsg() {
		return enablePersonalizeMsg;
	}

	public void setEnablePersonalizeMsg(boolean enablePersonalizeMsg) {
		this.enablePersonalizeMsg = enablePersonalizeMsg;
	}

	public String getPersonalMsg() {
		return personalMsg;
	}

	public void setPersonalMsg(String personalMsg) {
		this.personalMsg = personalMsg;
	}

	@Override
	public String toString() {
		return "SetAutoReminderResponse [document_id=" + document_id + ", days_to_expire=" + days_to_expire
				+ ", everyValue=" + everyValue + ", everyUnit=" + everyUnit + ", time=" + time
				+ ", enablePersonalizeMsg=" + enablePersonalizeMsg + ", personalMsg=" + personalMsg + "]";
	}

}
