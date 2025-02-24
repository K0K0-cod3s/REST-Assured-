import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.module.jsv.JsonSchemaValidator;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

public class JsonSchemaValidationTests {

    @Before
    public void setup() {
        RestAssured.baseURI = "https://reqres.in/api";
    }

    @Test
    public void testSingleUserJsonSchema() {
        // Assuming you have a single-user-schema.json file in src/test/resources
        RestAssured.given()
                .when()
                .get("/users/2")
                .then()
                .assertThat()
                .statusCode(200)
                .and()
                .body(JsonSchemaValidator.matchesJsonSchema(
                        new File("src/test/resources/schemas/single-user-schema.json")));
    }

    @Test
    public void testUserListJsonSchema() {
        // Assuming you have a user-list-schema.json file in src/test/resources
        RestAssured.given()
                .when()
                .get("/users?page=1")
                .then()
                .assertThat()
                .statusCode(200)
                .and()
                .body(JsonSchemaValidator.matchesJsonSchema(
                        new File("src/test/resources/schemas/user-list-schema.json")));
    }
}