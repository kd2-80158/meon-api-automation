package com.api.models.response.ocr;

public class ExtractDataPANOCRResponse {

	private int average_match_percentage;
	private ExtractedData extracted_data;
	private MatchingResults matching_results;
	private boolean success;
	private String msg;
	
	public ExtractDataPANOCRResponse(String msg)
	{
		this.msg = msg;
	}

	public String getMsg() {
		return msg;
	}

	public int getAverage_match_percentage() {
		return average_match_percentage;
	}

	public ExtractedData getExtracted_data() {
		return extracted_data;
	}

	public MatchingResults getMatching_results() {
		return matching_results;
	}

	public boolean isSuccess() {
		return success;
	}

	public static class ExtractedData {
		private String ocr_dob;
		private String ocr_father_name;
		private String ocr_name;
		private String ocr_pan_number;

		public String getOcr_dob() {
			return ocr_dob;
		}

		public String getOcr_father_name() {
			return ocr_father_name;
		}

		public String getOcr_name() {
			return ocr_name;
		}

		public String getOcr_pan_number() {
			return ocr_pan_number;
		}
	}

	public static class MatchingResults {
		private int dob_match_percentage;
		private int father_name_match_percentage;
		private int name_match_percentage;
		private int pan_number_match_percentage;

		public int getDob_match_percentage() {
			return dob_match_percentage;
		}

		public int getFather_name_match_percentage() {
			return father_name_match_percentage;
		}

		public int getName_match_percentage() {
			return name_match_percentage;
		}

		public int getPan_number_match_percentage() {
			return pan_number_match_percentage;
		}
	}
}
