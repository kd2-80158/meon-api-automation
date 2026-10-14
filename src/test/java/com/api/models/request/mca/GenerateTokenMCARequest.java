package com.api.models.request.mca;

import com.google.gson.annotations.JsonAdapter;

public class GenerateTokenMCARequest {
	
	private String static_id;
	
	public GenerateTokenMCARequest() {
		
	}
	
	public GenerateTokenMCARequest(String static_id)
	{
		this.static_id = static_id;
	}

	public String getStatic_id() {
		return static_id;
	}

	public void setStatic_id(String static_id) {
		this.static_id = static_id;
	}

	@Override
	public String toString() {
		return "GenerateTokenMCARequest [static_id=" + static_id + "]";
	}
	
	

}
