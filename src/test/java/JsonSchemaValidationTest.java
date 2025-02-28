import io.qameta.allure.*;
import io.restassured.RestAssured;
import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

import static io.restassured.RestAssured.given;

@Epic("API Schema Validation")
@Feature("JSON Schema Validation")
public class JsonSchemaValidationTest {

    @BeforeEach
    void setup() {
        RestAssured.baseURI = "https://reqres.in/api";
    }

    @Test
    @Story("Single User Schema Validation")
    @Description("Validates that the single user response matches the expected JSON schema")
    @Severity(SeverityLevel.CRITICAL)
    @Link(name = "ReqRes API", url = "https://reqres.in")
    void testSingleUserJsonSchema() {
        // Extract the response to add as attachment
        Response response = given()
                .when()
                .get("/users/2")
                .then()
                .assertThat()
                .statusCode(200)
                .body(JsonSchemaValidator.matchesJsonSchema(new File("src/test/resources/schemas/single-user-schema.json")))
                .extract().response();

        // Add response body as attachment
        Allure.addAttachment("Response Body", "application/json", response.asString(), ".json");

        // Add schema as attachment (try-catch to handle potential file reading issues)
        try {
            String schema = new String(Files.readAllBytes(Paths.get("src/test/resources/schemas/single-user-schema.json")));
            Allure.addAttachment("JSON Schema", "application/json", schema, ".json");
        } catch (Exception e) {
            Allure.addAttachment("Schema Error", "text/plain", "Could not read schema file: " + e.getMessage());
        }

        // Add response time
        Allure.addAttachment("Response Time", "text/plain", "Response Time: " + response.getTime() + "ms");
    }

    @Test
    @Story("User List Schema Validation")
    @Description("Validates that the user list response matches the expected JSON schema")
    @Severity(SeverityLevel.CRITICAL)
    @Link(name = "ReqRes API", url = "https://reqres.in")
    void testUserListJsonSchema() {
        // Extract the response to add as attachment
        Response response = given()
                .when()
                .get("/users?page=2")
                .then()
                .assertThat()
                .statusCode(200)
                .body(JsonSchemaValidator.matchesJsonSchema(new File("src/test/resources/schemas/user-list-schema.json")))
                .extract().response();

        // Add response body as attachment
        Allure.addAttachment("Response Body", "application/json", response.asString(), ".json");

        // Add schema as attachment
        try {
            String schema = new String(Files.readAllBytes(Paths.get("src/test/resources/schemas/user-list-schema.json")));
            Allure.addAttachment("JSON Schema", "application/json", schema, ".json");
        } catch (Exception e) {
            Allure.addAttachment("Schema Error", "text/plain", "Could not read schema file: " + e.getMessage());
        }

        // Add response time and pagination info
        Allure.addAttachment("Response Time", "text/plain", "Response Time: " + response.getTime() + "ms");
        Allure.addAttachment("Pagination Info", "text/plain",
                "Page: " + response.path("page") +
                        "\nPer Page: " + response.path("per_page") +
                        "\nTotal: " + response.path("total") +
                        "\nTotal Pages: " + response.path("total_pages"));
    }
}