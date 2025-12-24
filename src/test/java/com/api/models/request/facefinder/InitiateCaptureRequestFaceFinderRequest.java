package com.api.models.request.facefinder;

import com.api.utility.JSONUtility;

public class InitiateCaptureRequestFaceFinderRequest {

	private boolean check_location;
	private boolean capture_video;
	private boolean match_face;
	private boolean read_script;
	private String text_script;
	private int video_time;
	private String image_to_be_match;

	public InitiateCaptureRequestFaceFinderRequest() {
		this.check_location = false;
		this.capture_video = false;
		this.match_face = false;
		this.read_script = false;
		this.text_script = "test";
		this.video_time = 0;
		this.image_to_be_match = JSONUtility.getFaceFinder().getImage_to_be_match();
	}

	public InitiateCaptureRequestFaceFinderRequest(boolean check_location, boolean capture_video, boolean match_face,
			boolean read_script, String text_script, int video_time, String image_to_be_match) {
		super();
		this.check_location = check_location;
		this.capture_video = capture_video;
		this.match_face = match_face;
		this.read_script = read_script;
		this.text_script = text_script;
		this.video_time = video_time;
		this.image_to_be_match = image_to_be_match;
	}

	public InitiateCaptureRequestFaceFinderRequest(boolean check_location, boolean match_face) {
		this.check_location = check_location;
		this.match_face = match_face;
	}

	public InitiateCaptureRequestFaceFinderRequest(boolean check_location, boolean match_face, int video_time,boolean capture_video) {
		this.check_location = check_location;
		this.match_face = match_face;
		this.video_time = video_time;
		this.capture_video = capture_video;
	}

	public InitiateCaptureRequestFaceFinderRequest(boolean match_face, String image_to_be_match) {
		this.match_face = match_face;
		this.image_to_be_match = image_to_be_match;
	}

	public InitiateCaptureRequestFaceFinderRequest(int video_time) {
		this.video_time = video_time;
	}

	public boolean isCheck_location() {
		return check_location;
	}

	public void setCheck_location(boolean check_location) {
		this.check_location = check_location;
	}

	public boolean isCapture_video() {
		return capture_video;
	}

	public void setCapture_video(boolean capture_video) {
		this.capture_video = capture_video;
	}

	public boolean isMatch_face() {
		return match_face;
	}

	public void setMatch_face(boolean match_face) {
		this.match_face = match_face;
	}

	public boolean isRead_script() {
		return read_script;
	}

	public void setRead_script(boolean read_script) {
		this.read_script = read_script;
	}

	public String getText_script() {
		return text_script;
	}

	public void setText_script(String text_script) {
		this.text_script = text_script;
	}

	public int getVideo_time() {
		return video_time;
	}

	public void setVideo_time(int video_time) {
		this.video_time = video_time;
	}

	public String getImage_to_be_match() {
		return image_to_be_match;
	}

	public void setImage_to_be_match(String image_to_be_match) {
		this.image_to_be_match = image_to_be_match;
	}

	@Override
	public String toString() {
		return "InitiateCaptureRequestFaceFinderRequest [check_location=" + check_location + ", capture_video="
				+ capture_video + ", match_face=" + match_face + ", read_script=" + read_script + ", text_script="
				+ text_script + ", video_time=" + video_time + ", image_to_be_match=" + image_to_be_match + "]";
	}

}
