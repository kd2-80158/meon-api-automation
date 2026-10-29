package com.api.pages;

import java.io.File;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

public class SignatureOCRPage {

	private static final String BASE_URL = "https://ocr-uat.meon.co.in";

	private static final String CLASSIFY_SIGNATURE_ENDPOINT = "/api/classify-signature";

	/**
	 * Upload signature image.
	 *
	 * Rest Assured automatically generates the multipart boundary. Do not manually
	 * pass the multipart boundary.
	 */
	public Response classifySignature(File signatureFile) {

		return RestAssured.given().relaxedHTTPSValidation().baseUri(BASE_URL).accept(ContentType.JSON)
				.multiPart("signature", signatureFile).when().post(CLASSIFY_SIGNATURE_ENDPOINT);
	}

	public Response classifyWithPartName(String partName, File file) {

		return RestAssured.given().relaxedHTTPSValidation().baseUri(BASE_URL).accept(ContentType.JSON)
				.multiPart(partName, file).when().post(CLASSIFY_SIGNATURE_ENDPOINT);
	}

	public Response postWithoutSignature() {

		return RestAssured.given().relaxedHTTPSValidation().baseUri(BASE_URL).accept(ContentType.JSON)
				.contentType(ContentType.MULTIPART).when().post(CLASSIFY_SIGNATURE_ENDPOINT);
	}

	public Response postJson(String body) {

		return RestAssured.given().relaxedHTTPSValidation().baseUri(BASE_URL).accept(ContentType.JSON)
				.contentType(ContentType.JSON).body(body).when().post(CLASSIFY_SIGNATURE_ENDPOINT);
	}

	public Response get() {

		return RestAssured.given().relaxedHTTPSValidation().baseUri(BASE_URL).when().get(CLASSIFY_SIGNATURE_ENDPOINT);
	}

	public Response put() {

		return RestAssured.given().relaxedHTTPSValidation().baseUri(BASE_URL).when().put(CLASSIFY_SIGNATURE_ENDPOINT);
	}

	public Response delete() {

		return RestAssured.given().relaxedHTTPSValidation().baseUri(BASE_URL).when()
				.delete(CLASSIFY_SIGNATURE_ENDPOINT);
	}
}