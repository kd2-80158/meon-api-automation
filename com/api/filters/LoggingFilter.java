package com.api.filters;

import org.apache.logging.log4j.Logger;

import com.api.utility.LoggerUtility;

import io.restassured.filter.Filter;
import io.restassured.filter.FilterContext;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;

public class LoggingFilter implements Filter {
	
	Logger logger = LoggerUtility.getLogger(this.getClass());
	
	public void logRequest(FilterableRequestSpecification requestSpec)
	{
		logger.info("BaseURL: "+requestSpec.getBaseUri());
		logger.info("Request Headers: "+requestSpec.getHeaders());
		logger.info("Request Body: "+requestSpec.getBody());
	}
	
	public void logResponse(Response response)
	{
		logger.info("Status code: "+response.getStatusCode());
//		logger.info("Response Headers: "+response.headers());
//		logger.info("Response body: "+response.body().asPrettyString());
	}

	@Override
	public Response filter(FilterableRequestSpecification requestSpec, FilterableResponseSpecification responseSpec,
			FilterContext ctx) {
		logRequest(requestSpec);
		Response response = ctx.next(requestSpec, responseSpec);
		logResponse(response);
		return response;
	}

}
