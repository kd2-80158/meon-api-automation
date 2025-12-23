package com.api.models.response.ocr;

public class ExtractDataAadhaarOCRResponse {

	private double average_match_percentage;
	private ExtractedData extracted_data;
	private MatchingResults matching_results;
	private boolean success;
	private String msg;
	
	
	public ExtractDataAadhaarOCRResponse(String msg) {
		this.msg = msg;
	}

	public double getAverage_match_percentage() {
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
		private String ocr_address;
		private String ocr_address_for_match;
		private String ocr_adhar_number;
		private String ocr_dob;
		private String ocr_father_name;
		private String ocr_name;
		private String ocr_pin_code;
		private String ocr_gender;

		public String getOcr_pin_code() {
			return ocr_pin_code;
		}

		public String getOcr_gender() {
			return ocr_gender;
		}

		public String getOcr_address() {
			return ocr_address;
		}

		public String getOcr_address_for_match() {
			return ocr_address_for_match;
		}

		public String getOcr_adhar_number() {
			return ocr_adhar_number;
		}

		public String getOcr_dob() {
			return ocr_dob;
		}

		public String getOcr_father_name() {
			return ocr_father_name;
		}

		public String getOcr_name() {
			return ocr_name;
		}
	}

	public static class MatchingResults {
		private double adharno_number_match_percentage;
		private double dob_match_percentage;
		private double name_match_percentage;
		private double ocr_address_match_percentage;
		private double ocr_father_name_match_percentage;

		public double getAdharno_number_match_percentage() {
			return adharno_number_match_percentage;
		}

		public double getDob_match_percentage() {
			return dob_match_percentage;
		}

		public double getName_match_percentage() {
			return name_match_percentage;
		}

		public double getOcr_address_match_percentage() {
			return ocr_address_match_percentage;
		}

		public double getOcr_father_name_match_percentage() {
			return ocr_father_name_match_percentage;
		}
	}

	public String getMsg() {
		return msg;
	}
}
