package com.selimhorri.app.e2e.utils;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ApiClient {

	private String baseUrl;
	private String authToken;

	public ApiClient(String baseUrl) {
		this.baseUrl = baseUrl;
		RestAssured.baseURI = baseUrl;
	}

	public Response get(String endpoint) {
		log.info("GET request to: {}{}", baseUrl, endpoint);
		return buildRequest()
			.when()
			.get(endpoint);
	}

	public Response get(String endpoint, String... pathParams) {
		log.info("GET request to: {}{} with params: {}", baseUrl, endpoint, (Object[]) pathParams);
		return buildRequest()
			.when()
			.get(endpoint, (Object[]) pathParams);
	}

	public Response post(String endpoint, Object body) {
		log.info("POST request to: {}{} with body: {}", baseUrl, endpoint, body);
		return buildRequest()
			.body(body)
			.when()
			.post(endpoint);
	}

	public Response put(String endpoint, Object body) {
		log.info("PUT request to: {}{} with body: {}", baseUrl, endpoint, body);
		return buildRequest()
			.body(body)
			.when()
			.put(endpoint);
	}

	public Response put(String endpoint, Object body, String... pathParams) {
		log.info("PUT request to: {}{} with body: {}", baseUrl, endpoint, body);
		return buildRequest()
			.body(body)
			.when()
			.put(endpoint, (Object[]) pathParams);
	}

	public Response delete(String endpoint) {
		log.info("DELETE request to: {}{}", baseUrl, endpoint);
		return buildRequest()
			.when()
			.delete(endpoint);
	}

	public Response delete(String endpoint, String... pathParams) {
		log.info("DELETE request to: {}{}", baseUrl, endpoint);
		return buildRequest()
			.when()
			.delete(endpoint, (Object[]) pathParams);
	}

	private RequestSpecification buildRequest() {
		RequestSpecification spec = RestAssured.given()
			.contentType(ContentType.JSON)
			.accept(ContentType.JSON);

		if (authToken != null && !authToken.isEmpty()) {
			spec.header("Authorization", "Bearer " + authToken);
		}

		return spec;
	}

	public void setAuthToken(String token) {
		this.authToken = token;
		log.info("Auth token set");
	}

	public String getAuthToken() {
		return authToken;
	}

	public void clearAuthToken() {
		this.authToken = null;
		log.info("Auth token cleared");
	}

	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
		RestAssured.baseURI = baseUrl;
	}
}
