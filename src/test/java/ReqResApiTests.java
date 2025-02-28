import io.qameta.allure.*;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.http.Headers;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@Epic("User Management API")
@Feature("RESTful API Testing")
@Owner("QA Team")
public class ReqResApiTests {

    private static final String BASE_URI = "https://reqres.in/api";

    @BeforeEach
    void setup() {
        RestAssured.baseURI = BASE_URI;
    }

    // ==================== GET Tests ====================
    @Test
    @Feature("Get Operations")
    @Story("Retrieve a single user")
    @Description("Verifies that a single user can be retrieved by ID")
    @Severity(SeverityLevel.BLOCKER)
    @Link(name = "ReqRes API", url = "https://reqres.in")
    void testGetSingleUser() {
        Response response = given()
                .when().get("/users/2")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("data.id", equalTo(2))
                .body("data.first_name", equalTo("Janet"))
                .body("data.last_name", equalTo("Weaver"))
                .extract().response();

        // Additional assertions
        assert response.path("data.avatar").toString().contains("https://");

        // Add essential reporting info
        attachBasicResponseDetails(response, "/users/2", "GET");
    }

    @Test
    @Feature("Get Operations")
    @Story("Retrieve multiple users")
    @Description("Verifies that a list of users can be retrieved with pagination")
    @Severity(SeverityLevel.CRITICAL)
    @Link(name = "ReqRes API", url = "https://reqres.in")
    void testGetListUsers() {
        Response response = given()
                .queryParam("page", 2)
                .when().get("/users")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("page", equalTo(2))
                .body("data", hasSize(greaterThan(0)))
                .body("total_pages", greaterThan(0))
                .extract().response();

        attachBasicResponseDetails(response, "/users?page=2", "GET");
        // Add only relevant pagination information
        Allure.addAttachment("Pagination Info", "text/plain",
                "Page: " + response.path("page") + ", Total: " + response.path("total"));
    }

    @Test
    @Feature("Get Operations")
    @Story("Handle non-existent resources")
    @Description("Verifies that a 404 status is returned for non-existent users")
    @Severity(SeverityLevel.NORMAL)
    void testGetNonExistentUser() {
        Response response = given()
                .when().get("/users/23")
                .then()
                .statusCode(404)
                .extract().response();

        attachBasicResponseDetails(response, "/users/23", "GET");
    }

    // ==================== POST Tests ====================
    @Test
    @Feature("Post Operations")
    @Story("Create a new user")
    @Description("Verifies that a new user can be created")
    @Severity(SeverityLevel.CRITICAL)
    void testCreateUser() {
        Map<String, String> user = Map.of("name", "John Doe", "job", "Software Tester");

        Response response = given()
                .contentType(ContentType.JSON)
                .body(user)
                .when().post("/users")
                .then()
                .statusCode(201)
                .body("name", equalTo("John Doe"))
                .body("job", equalTo("Software Tester"))
                .body("id", notNullValue())
                .body("createdAt", notNullValue())
                .extract().response();

        attachResponseDetails(response, "/users", "POST", user);
    }

    @Test
    @Feature("Post Operations")
    @Story("Register a new user")
    @Description("Verifies that a user can be registered with valid credentials")
    @Severity(SeverityLevel.CRITICAL)
    void testRegisterUser() {
        Map<String, String> user = Map.of("email", "eve.holt@reqres.in", "password", "pistol");

        Response response = given()
                .contentType(ContentType.JSON)
                .body(user)
                .when().post("/register")
                .then()
                .statusCode(200)
                .body("id", notNullValue())
                .body("token", notNullValue())
                .extract().response();

        attachResponseDetails(response, "/register", "POST", user);
        Allure.addAttachment("Token", "text/plain", response.jsonPath().getString("token"));
    }

    @Test
    @Feature("Post Operations")
    @Story("Handle invalid registration")
    @Description("Verifies that registration fails with missing required fields")
    @Severity(SeverityLevel.NORMAL)
    void testUnsuccessfulRegister() {
        Map<String, String> user = Map.of("email", "sydney@fife");

        Response response = given()
                .contentType(ContentType.JSON)
                .body(user)
                .when().post("/register")
                .then()
                .statusCode(400)
                .body("error", equalTo("Missing password"))
                .extract().response();

        attachResponseDetails(response, "/register", "POST", user);
        Allure.addAttachment("Error Message", "text/plain", response.jsonPath().getString("error"));
    }

    // ==================== PUT Tests ====================
    @Test
    @Feature("Put Operations")
    @Story("Update an existing user")
    @Description("Verifies that an existing user can be fully updated")
    @Severity(SeverityLevel.CRITICAL)
    void testUpdateUser() {
        Map<String, String> user = Map.of("name", "John Updated", "job", "Senior Tester");

        Response response = given()
                .contentType(ContentType.JSON)
                .body(user)
                .when().put("/users/2")
                .then()
                .statusCode(200)
                .body("name", equalTo("John Updated"))
                .body("job", equalTo("Senior Tester"))
                .body("updatedAt", notNullValue())
                .extract().response();

        attachResponseDetails(response, "/users/2", "PUT", user);
    }

    @Test
    @Feature("Patch Operations")
    @Story("Partially update an existing user")
    @Description("Verifies that an existing user can be partially updated")
    @Severity(SeverityLevel.CRITICAL)
    void testPartialUpdateUser() {
        Map<String, String> user = Map.of("name", "John Patched");

        Response response = given()
                .contentType(ContentType.JSON)
                .body(user)
                .when().patch("/users/2")
                .then()
                .statusCode(200)
                .body("name", equalTo("John Patched"))
                .body("updatedAt", notNullValue())
                .extract().response();

        attachResponseDetails(response, "/users/2", "PATCH", user);
    }

    // ==================== DELETE Tests ====================
    @Test
    @Feature("Delete Operations")
    @Story("Delete an existing user")
    @Description("Verifies that an existing user can be deleted")
    @Severity(SeverityLevel.CRITICAL)
    void testDeleteUser() {
        Response response = given()
                .when().delete("/users/2")
                .then()
                .statusCode(204)
                .extract().response();

        attachBasicResponseDetails(response, "/users/2", "DELETE");
    }

    // ==================== Additional Tests ====================
    @Test
    @Feature("Performance Testing")
    @Story("Verify API response time")
    @Description("Verifies that the API responds within acceptable time limits")
    @Severity(SeverityLevel.MINOR)
    void testResponseTime() {
        Response response = given()
                .when().get("/users?page=2")
                .then()
                .time(lessThan(2000L)) // Response time should be < 2s
                .extract().response();

        attachBasicResponseDetails(response, "/users?page=2", "GET");
        Allure.addAttachment("Performance", "text/plain",
                "Response time: " + response.getTime() + " ms (Max: 2000 ms)");
    }

    @Test
    @Feature("Security Testing")
    @Story("Verify response headers")
    @Description("Verifies that the API response includes expected security headers")
    @Severity(SeverityLevel.MINOR)
    void testResponseHeaders() {
        Response response = given()
                .when().get("/users/2")
                .then()
                .header("Content-Type", containsString("application/json"))
                .header("Transfer-Encoding", equalTo("chunked"))
                .header("Connection", equalTo("keep-alive"))
                .extract().response();

        attachBasicResponseDetails(response, "/users/2", "GET");
        // Only record the specific headers we're testing
        Allure.addAttachment("Key Headers", "text/plain",
                "Content-Type: " + response.getHeader("Content-Type") + "\n" +
                        "Transfer-Encoding: " + response.getHeader("Transfer-Encoding") + "\n" +
                        "Connection: " + response.getHeader("Connection"));
    }

    /**
     * Helper method to attach basic response details to Allure report
     */
    private void attachBasicResponseDetails(Response response, String endpoint, String method) {
        Allure.addAttachment("Request", "text/plain", method + " " + BASE_URI + endpoint);
        Allure.addAttachment("Status", "text/plain", String.valueOf(response.getStatusCode()));

        // Only attach response body if it's not empty
        if (response.asString() != null && !response.asString().isEmpty()) {
            String contentType = response.getContentType();
            boolean isJson = contentType != null && contentType.contains("json");
            Allure.addAttachment("Response Body",
                    isJson ? "application/json" : "text/plain",
                    response.asString());
        }
    }

    /**
     * Helper method to attach response details including request body
     */
    private void attachResponseDetails(Response response, String endpoint, String method, Map<String, String> requestBody) {
        attachBasicResponseDetails(response, endpoint, method);
        if (requestBody != null && !requestBody.isEmpty()) {
            Allure.addAttachment("Request Body", "application/json", formatMap(requestBody));
        }
    }

    /**
     * Helper method to format a Map as JSON for reporting
     */
    private String formatMap(Map<String, String> map) {
        StringBuilder builder = new StringBuilder("{\n");
        map.forEach((key, value) ->
                builder.append("  \"")
                        .append(key)
                        .append("\": \"")
                        .append(value)
                        .append("\",\n")
        );
        if (!map.isEmpty()) {
            builder.delete(builder.length() - 2, builder.length()); // Remove trailing comma
            builder.append("\n");
        }
        builder.append("}");
        return builder.toString();
    }
}