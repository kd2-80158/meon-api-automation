package com.api.models.response.ocr;

import com.google.gson.annotations.SerializedName;

public class SignatureOCRResponse {

	@SerializedName("is_signature")
	private boolean signature;

	private Result result;

	private boolean success;

	private String msg;

	public boolean isSignature() {
		return signature;
	}

	public Result getResult() {
		return result;
	}

	public boolean isSuccess() {
		return success;
	}

	public String getMsg() {
		return msg;
	}

	public static class Result {

		private Checks checks;
		private Features features;

		public Checks getChecks() {
			return checks;
		}

		public Features getFeatures() {
			return features;
		}
	}

	public static class Checks {

		@SerializedName("limited_stroke_components")
		private boolean limitedStrokeComponents;

		@SerializedName("mostly_plain_background")
		private boolean mostlyPlainBackground;

		@SerializedName("not_document_like")
		private boolean notDocumentLike;

		@SerializedName("plausible_ink_coverage")
		private boolean plausibleInkCoverage;

		public boolean isLimitedStrokeComponents() {
			return limitedStrokeComponents;
		}

		public boolean isMostlyPlainBackground() {
			return mostlyPlainBackground;
		}

		public boolean isNotDocumentLike() {
			return notDocumentLike;
		}

		public boolean isPlausibleInkCoverage() {
			return plausibleInkCoverage;
		}
	}

	public static class Features {

		@SerializedName("background_share")
		private double backgroundShare;

		@SerializedName("component_count")
		private int componentCount;

		@SerializedName("foreground_ratio")
		private double foregroundRatio;

		@SerializedName("paper_area_ratio")
		private double paperAreaRatio;

		@SerializedName("paper_detected")
		private boolean paperDetected;

		@SerializedName("stroke_bbox_aspect")
		private double strokeBboxAspect;

		@SerializedName("text_like_rows")
		private int textLikeRows;

		@SerializedName("transparent_background")
		private boolean transparentBackground;

		public double getBackgroundShare() {
			return backgroundShare;
		}

		public int getComponentCount() {
			return componentCount;
		}

		public double getForegroundRatio() {
			return foregroundRatio;
		}

		public double getPaperAreaRatio() {
			return paperAreaRatio;
		}

		public boolean isPaperDetected() {
			return paperDetected;
		}

		public double getStrokeBboxAspect() {
			return strokeBboxAspect;
		}

		public int getTextLikeRows() {
			return textLikeRows;
		}

		public boolean isTransparentBackground() {
			return transparentBackground;
		}
	}
}