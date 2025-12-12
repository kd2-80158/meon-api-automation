package com.api.models.response.kyc;
import com.google.gson.annotations.SerializedName;

public class GetUserDataKYCResponse {

        @SerializedName("PDF-_id")
	    private String PDF__id;
	    private String aadhar_address;
	    private String aadhar_country;
	    private String aadhar_dist;
	    private String aadhar_dob;
	    private String aadhar_fathername;
	    private String aadhar_file;
	    private String aadhar_gender;
	    private String aadhar_house;
	    private String aadhar_image;
	    private String aadhar_name;
	    private String aadhar_no;
	    private String aadhar_pincode;
	    private String aadhar_state;
	    private String aadhar_xml;
	    private String adharimg;

	    // Client/Workflow Details
	    private String clientimage;
	    private String clienttoken;
	    private String current_stepname;
	    private String digilocker3;
	    private String digilocker_timestamp;
	    private String digitrans;
	    private String document_verification;
	    private String documentverification_timestamp;
	    private String entry_type;
	    private String faces_matched;
	    private String from_previous;
	    private String from_previous_position;
	    private String future_position;
	    private Integer id; // Numeric
	    private String identity;
	    private String image_to_be_matched;
	    private String ipv_transaction_id;
	    private String latitude;
	    private String liveimage_timestamp;
	    private String livevideo;
	    private String locality;
	    private String location;
	    private String longitude;
	    private String mobile;
	    private String mobile_otp_timestamp;
	    private String mobile_timestamp;
	    private String otp;
	    private String pan_from_digilocker;
	    private String pdf_timestamp;
	    private Integer position; // Numeric
	    private String review_edit;
	    private String stage;
	    private String uploadPAN;
	    private String workflowName;
	    private String workflow_key;
	    
	    public GetUserDataKYCResponse() {
			super();
		}
	    
	    
	    
		public GetUserDataKYCResponse(String pDF__id, String aadhar_address, String aadhar_country, String aadhar_dist,
				String aadhar_dob, String aadhar_fathername, String aadhar_file, String aadhar_gender,
				String aadhar_house, String aadhar_image, String aadhar_name, String aadhar_no, String aadhar_pincode,
				String aadhar_state, String aadhar_xml, String adharimg, String clientimage, String clienttoken,
				String current_stepname, String digilocker3, String digilocker_timestamp, String digitrans,
				String document_verification, String documentverification_timestamp, String entry_type,
				String faces_matched, String from_previous, String from_previous_position, String future_position,
				Integer id, String identity, String image_to_be_matched, String ipv_transaction_id, String latitude,
				String liveimage_timestamp, String livevideo, String locality, String location, String longitude,
				String mobile, String mobile_otp_timestamp, String mobile_timestamp, String otp,
				String pan_from_digilocker, String pdf_timestamp, Integer position, String review_edit, String stage,
				String uploadPAN, String workflowName, String workflow_key) {
			super();
			PDF__id = pDF__id;
			this.aadhar_address = aadhar_address;
			this.aadhar_country = aadhar_country;
			this.aadhar_dist = aadhar_dist;
			this.aadhar_dob = aadhar_dob;
			this.aadhar_fathername = aadhar_fathername;
			this.aadhar_file = aadhar_file;
			this.aadhar_gender = aadhar_gender;
			this.aadhar_house = aadhar_house;
			this.aadhar_image = aadhar_image;
			this.aadhar_name = aadhar_name;
			this.aadhar_no = aadhar_no;
			this.aadhar_pincode = aadhar_pincode;
			this.aadhar_state = aadhar_state;
			this.aadhar_xml = aadhar_xml;
			this.adharimg = adharimg;
			this.clientimage = clientimage;
			this.clienttoken = clienttoken;
			this.current_stepname = current_stepname;
			this.digilocker3 = digilocker3;
			this.digilocker_timestamp = digilocker_timestamp;
			this.digitrans = digitrans;
			this.document_verification = document_verification;
			this.documentverification_timestamp = documentverification_timestamp;
			this.entry_type = entry_type;
			this.faces_matched = faces_matched;
			this.from_previous = from_previous;
			this.from_previous_position = from_previous_position;
			this.future_position = future_position;
			this.id = id;
			this.identity = identity;
			this.image_to_be_matched = image_to_be_matched;
			this.ipv_transaction_id = ipv_transaction_id;
			this.latitude = latitude;
			this.liveimage_timestamp = liveimage_timestamp;
			this.livevideo = livevideo;
			this.locality = locality;
			this.location = location;
			this.longitude = longitude;
			this.mobile = mobile;
			this.mobile_otp_timestamp = mobile_otp_timestamp;
			this.mobile_timestamp = mobile_timestamp;
			this.otp = otp;
			this.pan_from_digilocker = pan_from_digilocker;
			this.pdf_timestamp = pdf_timestamp;
			this.position = position;
			this.review_edit = review_edit;
			this.stage = stage;
			this.uploadPAN = uploadPAN;
			this.workflowName = workflowName;
			this.workflow_key = workflow_key;
		}



		public String getPDF__id() {
			return PDF__id;
		}
		public void setPDF__id(String pDF__id) {
			PDF__id = pDF__id;
		}
		public String getAadhar_address() {
			return aadhar_address;
		}
		public void setAadhar_address(String aadhar_address) {
			this.aadhar_address = aadhar_address;
		}
		public String getAadhar_country() {
			return aadhar_country;
		}
		public void setAadhar_country(String aadhar_country) {
			this.aadhar_country = aadhar_country;
		}
		public String getAadhar_dist() {
			return aadhar_dist;
		}
		public void setAadhar_dist(String aadhar_dist) {
			this.aadhar_dist = aadhar_dist;
		}
		public String getAadhar_dob() {
			return aadhar_dob;
		}
		public void setAadhar_dob(String aadhar_dob) {
			this.aadhar_dob = aadhar_dob;
		}
		public String getAadhar_fathername() {
			return aadhar_fathername;
		}
		public void setAadhar_fathername(String aadhar_fathername) {
			this.aadhar_fathername = aadhar_fathername;
		}
		public String getAadhar_file() {
			return aadhar_file;
		}
		public void setAadhar_file(String aadhar_file) {
			this.aadhar_file = aadhar_file;
		}
		public String getAadhar_gender() {
			return aadhar_gender;
		}
		public void setAadhar_gender(String aadhar_gender) {
			this.aadhar_gender = aadhar_gender;
		}
		public String getAadhar_house() {
			return aadhar_house;
		}
		public void setAadhar_house(String aadhar_house) {
			this.aadhar_house = aadhar_house;
		}
		public String getAadhar_image() {
			return aadhar_image;
		}
		public void setAadhar_image(String aadhar_image) {
			this.aadhar_image = aadhar_image;
		}
		public String getAadhar_name() {
			return aadhar_name;
		}
		public void setAadhar_name(String aadhar_name) {
			this.aadhar_name = aadhar_name;
		}
		public String getAadhar_no() {
			return aadhar_no;
		}
		public void setAadhar_no(String aadhar_no) {
			this.aadhar_no = aadhar_no;
		}
		public String getAadhar_pincode() {
			return aadhar_pincode;
		}
		public void setAadhar_pincode(String aadhar_pincode) {
			this.aadhar_pincode = aadhar_pincode;
		}
		public String getAadhar_state() {
			return aadhar_state;
		}
		public void setAadhar_state(String aadhar_state) {
			this.aadhar_state = aadhar_state;
		}
		public String getAadhar_xml() {
			return aadhar_xml;
		}
		public void setAadhar_xml(String aadhar_xml) {
			this.aadhar_xml = aadhar_xml;
		}
		public String getAdharimg() {
			return adharimg;
		}
		public void setAdharimg(String adharimg) {
			this.adharimg = adharimg;
		}
		public String getClientimage() {
			return clientimage;
		}
		public void setClientimage(String clientimage) {
			this.clientimage = clientimage;
		}
		public String getClienttoken() {
			return clienttoken;
		}
		public void setClienttoken(String clienttoken) {
			this.clienttoken = clienttoken;
		}
		public String getCurrent_stepname() {
			return current_stepname;
		}
		public void setCurrent_stepname(String current_stepname) {
			this.current_stepname = current_stepname;
		}
		public String getDigilocker3() {
			return digilocker3;
		}
		public void setDigilocker3(String digilocker3) {
			this.digilocker3 = digilocker3;
		}
		public String getDigilocker_timestamp() {
			return digilocker_timestamp;
		}
		public void setDigilocker_timestamp(String digilocker_timestamp) {
			this.digilocker_timestamp = digilocker_timestamp;
		}
		public String getDigitrans() {
			return digitrans;
		}
		public void setDigitrans(String digitrans) {
			this.digitrans = digitrans;
		}
		public String getDocument_verification() {
			return document_verification;
		}
		public void setDocument_verification(String document_verification) {
			this.document_verification = document_verification;
		}
		public String getDocumentverification_timestamp() {
			return documentverification_timestamp;
		}
		public void setDocumentverification_timestamp(String documentverification_timestamp) {
			this.documentverification_timestamp = documentverification_timestamp;
		}
		public String getEntry_type() {
			return entry_type;
		}
		public void setEntry_type(String entry_type) {
			this.entry_type = entry_type;
		}
		public String getFaces_matched() {
			return faces_matched;
		}
		public void setFaces_matched(String faces_matched) {
			this.faces_matched = faces_matched;
		}
		public String getFrom_previous() {
			return from_previous;
		}
		public void setFrom_previous(String from_previous) {
			this.from_previous = from_previous;
		}
		public String getFrom_previous_position() {
			return from_previous_position;
		}
		public void setFrom_previous_position(String from_previous_position) {
			this.from_previous_position = from_previous_position;
		}
		public String getFuture_position() {
			return future_position;
		}
		public void setFuture_position(String future_position) {
			this.future_position = future_position;
		}
		public Integer getId() {
			return id;
		}
		public void setId(Integer id) {
			this.id = id;
		}
		public String getIdentity() {
			return identity;
		}
		public void setIdentity(String identity) {
			this.identity = identity;
		}
		public String getImage_to_be_matched() {
			return image_to_be_matched;
		}
		public void setImage_to_be_matched(String image_to_be_matched) {
			this.image_to_be_matched = image_to_be_matched;
		}
		public String getIpv_transaction_id() {
			return ipv_transaction_id;
		}
		public void setIpv_transaction_id(String ipv_transaction_id) {
			this.ipv_transaction_id = ipv_transaction_id;
		}
		public String getLatitude() {
			return latitude;
		}
		public void setLatitude(String latitude) {
			this.latitude = latitude;
		}
		public String getLiveimage_timestamp() {
			return liveimage_timestamp;
		}
		public void setLiveimage_timestamp(String liveimage_timestamp) {
			this.liveimage_timestamp = liveimage_timestamp;
		}
		public String getLivevideo() {
			return livevideo;
		}
		public void setLivevideo(String livevideo) {
			this.livevideo = livevideo;
		}
		public String getLocality() {
			return locality;
		}
		public void setLocality(String locality) {
			this.locality = locality;
		}
		public String getLocation() {
			return location;
		}
		public void setLocation(String location) {
			this.location = location;
		}
		public String getLongitude() {
			return longitude;
		}
		public void setLongitude(String longitude) {
			this.longitude = longitude;
		}
		public String getMobile() {
			return mobile;
		}
		public void setMobile(String mobile) {
			this.mobile = mobile;
		}
		public String getMobile_otp_timestamp() {
			return mobile_otp_timestamp;
		}
		public void setMobile_otp_timestamp(String mobile_otp_timestamp) {
			this.mobile_otp_timestamp = mobile_otp_timestamp;
		}
		public String getMobile_timestamp() {
			return mobile_timestamp;
		}
		public void setMobile_timestamp(String mobile_timestamp) {
			this.mobile_timestamp = mobile_timestamp;
		}
		public String getOtp() {
			return otp;
		}
		public void setOtp(String otp) {
			this.otp = otp;
		}
		public String getPan_from_digilocker() {
			return pan_from_digilocker;
		}
		public void setPan_from_digilocker(String pan_from_digilocker) {
			this.pan_from_digilocker = pan_from_digilocker;
		}
		public String getPdf_timestamp() {
			return pdf_timestamp;
		}
		public void setPdf_timestamp(String pdf_timestamp) {
			this.pdf_timestamp = pdf_timestamp;
		}
		public Integer getPosition() {
			return position;
		}
		public void setPosition(Integer position) {
			this.position = position;
		}
		public String getReview_edit() {
			return review_edit;
		}
		public void setReview_edit(String review_edit) {
			this.review_edit = review_edit;
		}
		public String getStage() {
			return stage;
		}
		public void setStage(String stage) {
			this.stage = stage;
		}
		public String getUploadPAN() {
			return uploadPAN;
		}
		public void setUploadPAN(String uploadPAN) {
			this.uploadPAN = uploadPAN;
		}
		public String getWorkflowName() {
			return workflowName;
		}
		public void setWorkflowName(String workflowName) {
			this.workflowName = workflowName;
		}
		public String getWorkflow_key() {
			return workflow_key;
		}
		public void setWorkflow_key(String workflow_key) {
			this.workflow_key = workflow_key;
		}
		@Override
		public String toString() {
			return "GetUserDataResponse [PDF__id=" + PDF__id + ", aadhar_address=" + aadhar_address
					+ ", aadhar_country=" + aadhar_country + ", aadhar_dist=" + aadhar_dist + ", aadhar_dob="
					+ aadhar_dob + ", aadhar_fathername=" + aadhar_fathername + ", aadhar_file=" + aadhar_file
					+ ", aadhar_gender=" + aadhar_gender + ", aadhar_house=" + aadhar_house + ", aadhar_image="
					+ aadhar_image + ", aadhar_name=" + aadhar_name + ", aadhar_no=" + aadhar_no + ", aadhar_pincode="
					+ aadhar_pincode + ", aadhar_state=" + aadhar_state + ", aadhar_xml=" + aadhar_xml + ", adharimg="
					+ adharimg + ", clientimage=" + clientimage + ", clienttoken=" + clienttoken + ", current_stepname="
					+ current_stepname + ", digilocker3=" + digilocker3 + ", digilocker_timestamp="
					+ digilocker_timestamp + ", digitrans=" + digitrans + ", document_verification="
					+ document_verification + ", documentverification_timestamp=" + documentverification_timestamp
					+ ", entry_type=" + entry_type + ", faces_matched=" + faces_matched + ", from_previous="
					+ from_previous + ", from_previous_position=" + from_previous_position + ", future_position="
					+ future_position + ", id=" + id + ", identity=" + identity + ", image_to_be_matched="
					+ image_to_be_matched + ", ipv_transaction_id=" + ipv_transaction_id + ", latitude=" + latitude
					+ ", liveimage_timestamp=" + liveimage_timestamp + ", livevideo=" + livevideo + ", locality="
					+ locality + ", location=" + location + ", longitude=" + longitude + ", mobile=" + mobile
					+ ", mobile_otp_timestamp=" + mobile_otp_timestamp + ", mobile_timestamp=" + mobile_timestamp
					+ ", otp=" + otp + ", pan_from_digilocker=" + pan_from_digilocker + ", pdf_timestamp="
					+ pdf_timestamp + ", position=" + position + ", review_edit=" + review_edit + ", stage=" + stage
					+ ", uploadPAN=" + uploadPAN + ", workflowName=" + workflowName + ", workflow_key=" + workflow_key
					+ "]";
		}
}
