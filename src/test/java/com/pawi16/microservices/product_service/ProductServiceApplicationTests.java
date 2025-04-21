package com.pawi16.microservices.product_service;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Import;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.shaded.org.hamcrest.Matchers;

import static org.hamcrest.Matchers.*;

@Import(TestcontainersConfiguration.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ProductServiceApplicationTests {

    @ServiceConnection
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:7.0.5");

    static {
        mongoDBContainer.start();
    }

    @LocalServerPort
    private Integer port;

    @BeforeEach
    void setup() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;
    }

    @Test
    void shouldCreateProduct() {
        String requestBody = """
                				{
                				"name": "iPhone 16",
                				"description":"good phone",
                				"price": 1000
                }
                """;

        RestAssured.given()
                .contentType("application/json")
                .body(requestBody)
                .when()
                .post("/api/product")
                .then()
                .statusCode(201)
                .body("id", notNullValue())
                .body("name", equalTo("iPhone 16"))
                .body("description", equalTo("good phone"))
                .body("price", equalTo(1000));
    }

    @Test
    void shouldGetAllProducts() {
        RestAssured.given()
                .when()
                .get("/api/product")
                .then()
                .statusCode(200)
                .body("size()", greaterThan(0)) // Ensure list is not empty
                .body("[0].id", notNullValue()) // Validate the first item's structure
                .body("[0].name", notNullValue())
                .body("[0].description", notNullValue())
                .body("[0].price", notNullValue());
    }

}
