package com.api.pojos;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.Map;

public class AadhaarData {

    @SerializedName("aadhar_address")
    @Expose
    private String aadharAddress;

    @SerializedName("aadhar_filename")
    @Expose
    private String aadharFilename;

    @SerializedName("aadhar_img_filename")
    @Expose
    private String aadharImgFilename;

    @SerializedName("aadhar_no")
    @Expose
    private String aadharNo;

    @SerializedName("aadhar_xml")
    @Expose
    private String aadharXml;

    @SerializedName("adharimg")
    @Expose
    private String adharImg;

    @Expose
    private String country;

    @SerializedName("date_time")
    @Expose
    private String dateTime;

    @Expose
    private String dist;

    @Expose
    private String dob;

    @Expose
    private String fathername;

    @Expose
    private String gender;

    @Expose
    private String house;

    @Expose
    private String locality;

    @Expose
    private String name;

    @SerializedName("name_on_pan")
    @Expose
    private String nameOnPan;

    @SerializedName("other_documents_files")
    @Expose
    private Map<String, Object> otherDocumentsFiles;

    @SerializedName("pan_image_path")
    @Expose
    private String panImagePath;

    @SerializedName("pan_number")
    @Expose
    private String panNumber;

    @Expose
    private String pincode;

    @Expose
    private String state;

    // Getters & setters ---------------------------------

    public String getAadharAddress() { return aadharAddress; }
    public void setAadharAddress(String aadharAddress) { this.aadharAddress = aadharAddress; }

    public String getAadharFilename() { return aadharFilename; }
    public void setAadharFilename(String aadharFilename) { this.aadharFilename = aadharFilename; }

    public String getAadharImgFilename() { return aadharImgFilename; }
    public void setAadharImgFilename(String aadharImgFilename) { this.aadharImgFilename = aadharImgFilename; }

    public String getAadharNo() { return aadharNo; }
    public void setAadharNo(String aadharNo) { this.aadharNo = aadharNo; }

    public String getAadharXml() { return aadharXml; }
    public void setAadharXml(String aadharXml) { this.aadharXml = aadharXml; }

    public String getAdharImg() { return adharImg; }
    public void setAdharImg(String adharImg) { this.adharImg = adharImg; }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }

    public String getDateTime() { return dateTime; }
    public void setDateTime(String dateTime) { this.dateTime = dateTime; }

    public String getDist() { return dist; }
    public void setDist(String dist) { this.dist = dist; }

    public String getDob() { return dob; }
    public void setDob(String dob) { this.dob = dob; }

    public String getFathername() { return fathername; }
    public void setFathername(String fathername) { this.fathername = fathername; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getHouse() { return house; }
    public void setHouse(String house) { this.house = house; }

    public String getLocality() { return locality; }
    public void setLocality(String locality) { this.locality = locality; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getNameOnPan() { return nameOnPan; }
    public void setNameOnPan(String nameOnPan) { this.nameOnPan = nameOnPan; }

    public Map<String, Object> getOtherDocumentsFiles() { return otherDocumentsFiles; }
    public void setOtherDocumentsFiles(Map<String, Object> otherDocumentsFiles) { this.otherDocumentsFiles = otherDocumentsFiles; }

    public String getPanImagePath() { return panImagePath; }
    public void setPanImagePath(String panImagePath) { this.panImagePath = panImagePath; }

    public String getPanNumber() { return panNumber; }
    public void setPanNumber(String panNumber) { this.panNumber = panNumber; }

    public String getPincode() { return pincode; }
    public void setPincode(String pincode) { this.pincode = pincode; }

    public String getState() { return state; }
    public void setState(String state) { this.state = state; }

    @Override
    public String toString() {
        return "AadhaarData{" +
                "aadharAddress='" + aadharAddress + '\'' +
                ", aadharFilename='" + aadharFilename + '\'' +
                ", aadharImgFilename='" + aadharImgFilename + '\'' +
                ", aadharNo='" + aadharNo + '\'' +
                ", aadharXml='" + aadharXml + '\'' +
                ", adharImg='" + adharImg + '\'' +
                ", country='" + country + '\'' +
                ", dateTime='" + dateTime + '\'' +
                ", dist='" + dist + '\'' +
                ", dob='" + dob + '\'' +
                ", fathername='" + fathername + '\'' +
                ", gender='" + gender + '\'' +
                ", house='" + house + '\'' +
                ", locality='" + locality + '\'' +
                ", name='" + name + '\'' +
                ", nameOnPan='" + nameOnPan + '\'' +
                ", otherDocumentsFiles=" + otherDocumentsFiles +
                ", panImagePath='" + panImagePath + '\'' +
                ", panNumber='" + panNumber + '\'' +
                ", pincode='" + pincode + '\'' +
                ", state='" + state + '\'' +
                '}';
    }
}
