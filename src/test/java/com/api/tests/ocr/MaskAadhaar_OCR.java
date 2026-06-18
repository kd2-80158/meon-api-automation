package com.api.tests.ocr;

import java.io.File;

import org.apache.logging.log4j.Logger;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.api.base.AuthService;
import com.api.base.BaseTest;
import com.api.dataprovider.OCRFileDataProvider;
import com.api.models.response.ocr.MaskAadhaarOCRResponse;
import com.api.utility.JSONUtility;
import com.api.utility.LoggerUtility;
import com.google.gson.Gson;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

@Listeners({ com.api.listeners.TestListener.class })
public class MaskAadhaar_OCR extends BaseTest {
	
	Logger logger;
	AuthService authService;
	Response response;
	RequestSpecification rs;
	Gson gson;
	
	@BeforeMethod
	public void setup()
	{
		authService = new AuthService("ocr");
		logger = LoggerUtility.getLogger(this.getClass());
		rs = RestAssured.given().relaxedHTTPSValidation();
		gson = new Gson();
	}

	@Test(description = "tc_maskaadhaar_01 - Verify Aadhaar masking with valid Aadhaar image", priority = 1, alwaysRun = true, groups = {
			"e2e", "smoke", "regression" }, dataProvider = "aadhaarMaskedFiles", dataProviderClass = OCRFileDataProvider.class)
	public void verifyResponseWithValidAadhaarImage_MaskAadhaarOCR(File imageFile) {

		if(!imageFile.exists() || imageFile.length()==0)
			throw new RuntimeException("Failed to find aadhaar file" + imageFile.getName());
		
		logger.info("===== Running OCR test for file: {} =====", imageFile.getName());
		
		response = rs.baseUri(JSONUtility.getOcr().getUrl())
				      .contentType(ContentType.MULTIPART)
				      .multiPart("file",imageFile)
				      .when()
				      .post(AuthService.BASE_PATH_OCR_MASK_AADHAAR);
		logger.info("Response is: "+response.asPrettyString());
		String responseBody = response.asPrettyString();
		MaskAadhaarOCRResponse aadhaarOCRResponse = gson.fromJson(responseBody,MaskAadhaarOCRResponse.class);
		softAssert.assertTrue(aadhaarOCRResponse.isSuccess());
		softAssert.assertNotNull(aadhaarOCRResponse.getMasked_image_base64());
		softAssert.assertAll();
	}
	
	@Test(description="tc_maskaadhaar_08 - Verify upload of image without Aadhaar number",priority=2,groups= {"e2e","sanity"})
	public void verifyResponseWithNonAadhaarImage_MaskAadhaarOCR()
	{
		File file = new File(System.getProperty("user.dir")+"/src/test/resources/testdata/"+File.separator+"PANcardSaurabh.jpeg");
	    if(!file.exists() || file.length()==0)
	    	throw new RuntimeException("Failed to located file "+file.getName());
	    
	    logger.info("===== Running OCR test for file: {} =====", file.getName());
	    
	    response = rs.baseUri(JSONUtility.getOcr().getUrl()).contentType(ContentType.MULTIPART)
	    		     .multiPart("file",file)
	    		     .when()
	    		     .post(AuthService.BASE_PATH_OCR_MASK_AADHAAR);
	    String responseBody = response.asPrettyString();
	    
	    MaskAadhaarOCRResponse maskAadhaarOCRResponse = gson.fromJson(responseBody, MaskAadhaarOCRResponse.class);
	    softAssert.assertTrue(maskAadhaarOCRResponse.isSuccess());
	    softAssert.assertEquals(maskAadhaarOCRResponse.getMsg(), "Not a valid aadhaar image.");
	    softAssert.assertAll();
	}
	
	@Test(description="tc_maskaadhaar_10 - Verify request with empty file",priority=3,groups= {"e2e","sanity"})
	public void verifyResponseWithEmptyFile_MaskAadhaarOCR()
	{
		File file = new File(System.getProperty("user.dir")+"/src/test/resources/testdata/aadhaar/"+File.separator+"emptyfile.png");
	    if(!file.exists() || file.length()==0)
	    	throw new RuntimeException("Failed to located file "+file.getName());
	    
	    logger.info("===== Running OCR test for file: {} =====", file.getName());
	    
	    response = rs.baseUri(JSONUtility.getOcr().getUrl()).contentType(ContentType.MULTIPART)
	    		     .multiPart("file",file)
	    		     .when()
	    		     .post(AuthService.BASE_PATH_OCR_MASK_AADHAAR);
	    String responseBody = response.asPrettyString();
	    
	    MaskAadhaarOCRResponse maskAadhaarOCRResponse = gson.fromJson(responseBody, MaskAadhaarOCRResponse.class);
	    softAssert.assertTrue(maskAadhaarOCRResponse.isSuccess());
	    softAssert.assertEquals(maskAadhaarOCRResponse.getMsg(), "Not a valid aadhaar image.");
	    softAssert.assertAll();
	}
	
	@Test(description="tc_maskaadhaar_11 - Verify upload of unsupported file format",priority=4,groups= {"e2e","sanity"})
	public void verifyResponseWithUnsupportedFileFormat_MaskAadhaarOCR()
	{
		File file = new File(System.getProperty("user.dir")+"/src/test/resources/testdata/aadhaar/"+File.separator+"emptyfile.png");
	    if(!file.exists() || file.length()==0)
	    	throw new RuntimeException("Failed to located file "+file.getName());
	    
	    logger.info("===== Running OCR test for file: {} =====", file.getName());
	    
	    response = rs.baseUri(JSONUtility.getOcr().getUrl()).contentType(ContentType.MULTIPART)
	    		     .multiPart("file",file)
	    		     .when()
	    		     .post(AuthService.BASE_PATH_OCR_MASK_AADHAAR);
	    String responseBody = response.asPrettyString();
	    
	    MaskAadhaarOCRResponse maskAadhaarOCRResponse = gson.fromJson(responseBody, MaskAadhaarOCRResponse.class);
	    softAssert.assertTrue(maskAadhaarOCRResponse.isSuccess());
	    softAssert.assertEquals(maskAadhaarOCRResponse.getMsg(), "Not a valid aadhaar image.");
	    softAssert.assertAll();
	}
	
	@Test(description="tc_maskaadhaar_12 - Verify upload of PDF instead of image",priority=5,groups= {"e2e","sanity"})
	public void verifyResponseWithPDFFileFormat_MaskAadhaarOCR()
	{
		File file = new File(System.getProperty("user.dir")+"/src/test/resources/testdata/aadhaar/"+File.separator+"aadhaarmask_neeraj.pdf");
	    if(!file.exists() || file.length()==0)
	    	throw new RuntimeException("Failed to located file "+file.getName());
	    
	    logger.info("===== Running OCR test for file: {} =====", file.getName());
	    
	    response = rs.baseUri(JSONUtility.getOcr().getUrl()).contentType(ContentType.MULTIPART)
	    		     .multiPart("file",file)
	    		     .when()
	    		     .post(AuthService.BASE_PATH_OCR_MASK_AADHAAR);
	    String responseBody = response.asPrettyString();
	    
	    MaskAadhaarOCRResponse maskAadhaarOCRResponse = gson.fromJson(responseBody, MaskAadhaarOCRResponse.class);
	    softAssert.assertTrue(maskAadhaarOCRResponse.isSuccess());
	    softAssert.assertEquals(maskAadhaarOCRResponse.getMsg(), "Not a valid aadhaar image.");
	    softAssert.assertAll();
	}

}
