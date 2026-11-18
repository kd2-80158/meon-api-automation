package com.api.filters;

import org.apache.logging.log4j.Logger;

import com.api.utility.LoggerUtility;

import io.restassured.filter.Filter;
import io.restassured.filter.FilterContext;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;

/**
 * Logs only non-sensitive request metadata. Request headers and bodies can
 * contain OAuth state, access tokens, Aadhaar identifiers, OTPs, or PINs.
 */
public class LoggingFilter implements Filter {

	private final Logger logger = LoggerUtility.getLogger(getClass());

	@Override
	public Response filter(FilterableRequestSpecification requestSpec,
			FilterableResponseSpecification responseSpec, FilterContext context) {
		logger.info("HTTP request sent; headers and body omitted for privacy.");
		Response response = context.next(requestSpec, responseSpec);
		logger.info("HTTP response status: {}", response.getStatusCode());
		return response;
	}
}
